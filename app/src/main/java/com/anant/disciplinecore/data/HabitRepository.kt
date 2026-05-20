package com.anant.disciplinecore.data

import androidx.lifecycle.LiveData
import java.text.SimpleDateFormat
import java.util.*

class HabitRepository(private val dao: HabitDao) {

    val allHabits: LiveData<List<Habit>> = dao.getAllHabits()

    private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun yesterday(): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    suspend fun insert(habit: Habit) = dao.insertHabit(habit)

    suspend fun delete(habit: Habit) = dao.deleteHabit(habit)

    suspend fun toggleCompletion(habit: Habit) {
        val today = today()
        val isAlreadyDoneToday = habit.isCompletedToday && habit.lastCompletedDate == today

        if (isAlreadyDoneToday) {
            // Undo today's completion
            val restoredStreak = (habit.streak - 1).coerceAtLeast(0)
            dao.updateHabit(
                habit.copy(
                    isCompletedToday = false,
                    streak = restoredStreak,
                    totalCompletions = (habit.totalCompletions - 1).coerceAtLeast(0),
                    lastCompletedDate = ""
                )
            )
        } else {
            // Mark complete — calculate new streak
            val newStreak = when (habit.lastCompletedDate) {
                yesterday() -> habit.streak + 1   // maintained streak
                today       -> habit.streak        // idempotent (shouldn't reach here)
                else        -> 1                   // streak broken, restart
            }
            val newLongest = maxOf(newStreak, habit.longestStreak)
            dao.updateHabit(
                habit.copy(
                    isCompletedToday = true,
                    streak = newStreak,
                    longestStreak = newLongest,
                    totalCompletions = habit.totalCompletions + 1,
                    lastCompletedDate = today
                )
            )
        }
    }

    /** Called on app open to reset completions from prior days */
    suspend fun refreshDailyState() = dao.resetStaleCompletions(today())

    suspend fun getTotalCount() = dao.getTotalCount()
    suspend fun getCompletedTodayCount() = dao.getCompletedTodayCount()
}
