package com.anant.disciplinecore.features.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anant.disciplinecore.core.gamification.XPManager
import com.anant.disciplinecore.data.local.database.HabitDatabase
import com.anant.disciplinecore.data.local.entities.Habit
import com.anant.disciplinecore.data.local.entities.UserProgress
import com.anant.disciplinecore.data.repository.HabitRepository
import com.anant.disciplinecore.data.repository.UserProgressRepository
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) :
    AndroidViewModel(application) {

    // ------------------------------------------------
    // REPOSITORIES
    // ------------------------------------------------

    private val repository: HabitRepository

    private val progressRepository: UserProgressRepository

    // ------------------------------------------------
    // HABITS
    // ------------------------------------------------

    val allHabits: LiveData<List<Habit>>

    // ------------------------------------------------
    // USER PROGRESS
    // ------------------------------------------------

    val userProgress =
        MutableLiveData<UserProgress?>()

    // ------------------------------------------------
    // CHART DATA
    // ------------------------------------------------

    private val _last30Days =
        MutableLiveData<Map<String, Int>>()

    val last30Days:
            LiveData<Map<String, Int>> = _last30Days

    private val _last7Days =
        MutableLiveData<Map<String, Int>>()

    val last7Days:
            LiveData<Map<String, Int>> = _last7Days

    // ------------------------------------------------
    // DAILY SCORE
    // ------------------------------------------------

    private val _dailyScore =
        MutableLiveData<Int>()

    val dailyScore:
            LiveData<Int> = _dailyScore

    // ------------------------------------------------
    // CONSISTENCY LEVEL
    // ------------------------------------------------

    private val _consistencyMessage =
        MutableLiveData<String>()

    val consistencyMessage:
            LiveData<String> = _consistencyMessage

    // ------------------------------------------------
    // MOTIVATION MESSAGE
    // ------------------------------------------------

    private val _motivationMessage =
        MutableLiveData<String>()

    val motivationMessage:
            LiveData<String> = _motivationMessage

    // ------------------------------------------------
    // INIT
    // ------------------------------------------------

    init {

        val database =
            HabitDatabase.getDatabase(application)

        val habitDao =
            database.habitDao()

        val habitLogDao =
            database.habitLogDao()

        repository =
            HabitRepository(
                habitDao,
                habitLogDao
            )

        progressRepository =
            UserProgressRepository(
                database.userProgressDao()
            )

        allHabits = repository.allHabits

        // =============================================
        // USER PROGRESS OBSERVER
        // =============================================

        progressRepository.progress.observeForever {

            userProgress.postValue(it)
        }

        // =============================================
        // INITIALIZE PROGRESS
        // =============================================

        viewModelScope.launch {

            progressRepository.initialize()
        }

        // =============================================
        // EXISTING
        // =============================================

        refreshDailyState()

        loadChartData()

        observeHabitChanges()
    }

    // ------------------------------------------------
    // INSERT
    // ------------------------------------------------

    fun insert(habit: Habit) =
        viewModelScope.launch {

            repository.insert(habit)

            refreshDashboard()
        }

    // ------------------------------------------------
    // DELETE
    // ------------------------------------------------

    fun delete(habit: Habit) =
        viewModelScope.launch {

            repository.delete(habit)

            refreshDashboard()
        }

    // ------------------------------------------------
    // TOGGLE COMPLETION
    // ------------------------------------------------

    fun toggleCompletion(habit: Habit) =
        viewModelScope.launch {

            val wasCompleted =
                habit.isCompletedToday

            repository.toggleCompletion(habit)

            progressRepository
                .incrementHabitsCompleted()

            // =========================================
            // XP + GLOBAL STREAK
            // =========================================

            if (!wasCompleted) {

                progressRepository.addXp(
                    userProgress.value,
                    XPManager.HABIT_COMPLETE_XP
                )

                progressRepository.updateGlobalStreak(
                    userProgress.value
                )
            }

            refreshDashboard()
        }

    // ------------------------------------------------
    // DELETE ALL
    // ------------------------------------------------

    fun deleteAllHabits() =
        viewModelScope.launch {

            repository.deleteAllHabits()

            _last30Days.postValue(emptyMap())

            _last7Days.postValue(emptyMap())

            refreshDashboard()
        }

    // ------------------------------------------------
    // REFRESH DAILY STATE
    // ------------------------------------------------

    private fun refreshDailyState() =
        viewModelScope.launch {

            repository.refreshDailyState()
        }

    // ------------------------------------------------
    // LOAD CHART DATA
    // ------------------------------------------------

    fun loadChartData() =
        viewModelScope.launch {

            _last30Days.postValue(
                repository.getLast30DaysData()
            )

            _last7Days.postValue(
                repository.getLast7DaysData()
            )
        }

    // ------------------------------------------------
    // OBSERVE HABITS
    // ------------------------------------------------

    private fun observeHabitChanges() {

        allHabits.observeForever { habits ->

            calculateDailyScore(habits)

            generateConsistencyMessage(habits)

            generateMotivationMessage(habits)
        }
    }

    // ------------------------------------------------
    // DAILY SCORE ENGINE
    // ------------------------------------------------

    private fun calculateDailyScore(
        habits: List<Habit>
    ) {

        val total = habits.size

        if (total == 0) {

            _dailyScore.postValue(0)

            return
        }

        val completed =
            habits.count {
                it.isCompletedToday
            }

        val completionRatio =
            completed.toFloat() / total.toFloat()

        val streakBonus =
            habits.sumOf {
                it.streak
            }.coerceAtMost(100)

        val consistencyBonus =
            habits.sumOf {
                it.consistencyScore
            }.coerceAtMost(100)

        val score =
            (
                    (completionRatio * 70f) +
                            (streakBonus * 0.15f) +
                            (consistencyBonus * 0.15f)
                    ).toInt()
                .coerceIn(0, 100)

        _dailyScore.postValue(score)
    }

    // ------------------------------------------------
    // CONSISTENCY MESSAGE
    // ------------------------------------------------

    private fun generateConsistencyMessage(
        habits: List<Habit>
    ) {

        val completed =
            habits.count {
                it.isCompletedToday
            }

        val total = habits.size

        val pct =
            if (total > 0)
                (completed * 100) / total
            else
                0

        val message = when {

            total == 0 ->
                "Start by adding your first habit."

            pct == 100 ->
                "Perfect consistency today 🔥"

            pct >= 80 ->
                "You're building serious momentum ⚡"

            pct >= 60 ->
                "Strong progress today 💪"

            pct >= 40 ->
                "Keep pushing forward 🎯"

            pct > 0 ->
                "Small wins still matter 🌱"

            else ->
                "Discipline starts with action."
        }

        _consistencyMessage.postValue(message)
    }

    // ------------------------------------------------
    // MOTIVATION MESSAGE
    // ------------------------------------------------

    private fun generateMotivationMessage(
        habits: List<Habit>
    ) {

        val bestHabit =
            habits.maxByOrNull {
                it.streak
            }

        val message = when {

            habits.isEmpty() ->
                "Create habits that shape your future."

            bestHabit != null &&
                    bestHabit.streak >= 30 ->

                "Your ${bestHabit.name} streak is elite 🔥"

            bestHabit != null &&
                    bestHabit.streak >= 14 ->

                "You're becoming more consistent daily."

            bestHabit != null &&
                    bestHabit.streak >= 7 ->

                "Momentum is building nicely ⚡"

            else ->
                "Consistency beats motivation."
        }

        _motivationMessage.postValue(message)
    }

    // ------------------------------------------------
    // REFRESH DASHBOARD
    // ------------------------------------------------

    private fun refreshDashboard() {

        loadChartData()
    }

    // ------------------------------------------------
    // CLEANUP
    // ------------------------------------------------

    override fun onCleared() {
        super.onCleared()
    }
}