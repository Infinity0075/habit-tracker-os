package com.anant.disciplinecore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.anant.disciplinecore.data.Habit
import com.anant.disciplinecore.data.HabitDatabase
import com.anant.disciplinecore.data.HabitRepository
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: HabitRepository

    val allHabits: LiveData<List<Habit>>

    // ── Real heatmap data ─────────────────────────────────────────
    private val _last30Days = MutableLiveData<Map<String, Int>>()
    val last30Days: LiveData<Map<String, Int>> = _last30Days

    private val _last7Days = MutableLiveData<Map<String, Int>>()
    val last7Days: LiveData<Map<String, Int>> = _last7Days

    init {
        val database    = HabitDatabase.getDatabase(application)
        val habitDao    = database.habitDao()
        val habitLogDao = database.habitLogDao()

        repository = HabitRepository(habitDao, habitLogDao)
        allHabits  = repository.allHabits

        refreshDailyState()
        loadChartData()
    }

    fun insert(habit: Habit) =
        viewModelScope.launch { repository.insert(habit) }

    fun delete(habit: Habit) =
        viewModelScope.launch { repository.delete(habit) }

    fun toggleCompletion(habit: Habit) =
        viewModelScope.launch {
            repository.toggleCompletion(habit)
            loadChartData() // refresh charts after every toggle
        }

    fun deleteAllHabits() =
        viewModelScope.launch {
            repository.deleteAllHabits()
            _last30Days.postValue(emptyMap())
            _last7Days.postValue(emptyMap())
        }

    private fun refreshDailyState() =
        viewModelScope.launch { repository.refreshDailyState() }

    // ── Load real chart data from DB ──────────────────────────────
    fun loadChartData() =
        viewModelScope.launch {
            _last30Days.postValue(repository.getLast30DaysData())
            _last7Days.postValue(repository.getLast7DaysData())
        }
}