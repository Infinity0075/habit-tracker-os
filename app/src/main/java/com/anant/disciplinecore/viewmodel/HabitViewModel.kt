package com.anant.disciplinecore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.anant.disciplinecore.data.Habit
import com.anant.disciplinecore.data.HabitDatabase
import com.anant.disciplinecore.data.HabitRepository
import kotlinx.coroutines.launch

class HabitViewModel(application: Application) :
    AndroidViewModel(application) {

    private val repository: HabitRepository

    val allHabits: LiveData<List<Habit>>

    init {

        val database =
            HabitDatabase.getDatabase(application)

        val habitDao =
            database.habitDao()

        val habitLogDao =
            database.habitLogDao()

        repository = HabitRepository(
            habitDao,
            habitLogDao
        )

        allHabits = repository.allHabits

        refreshDailyState()
    }

    fun insert(habit: Habit) =
        viewModelScope.launch {
            repository.insert(habit)
        }

    fun delete(habit: Habit) =
        viewModelScope.launch {
            repository.delete(habit)
        }

    fun toggleCompletion(habit: Habit) =
        viewModelScope.launch {
            repository.toggleCompletion(habit)
        }

    // ── Added for Settings reset button ──────────────────────────
    fun deleteAllHabits() =
        viewModelScope.launch {
            repository.deleteAllHabits()
        }

    private fun refreshDailyState() =
        viewModelScope.launch {
            repository.refreshDailyState()
        }
}