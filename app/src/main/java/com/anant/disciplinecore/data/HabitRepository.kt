package com.anant.disciplinecore.data

import androidx.lifecycle.LiveData
import java.text.SimpleDateFormat
import java.util.*

class HabitRepository(
    private val dao: HabitDao,
    private val logDao: HabitLogDao
) {

    val allHabits: LiveData<List<Habit>> = dao.getAllHabits()

    private fun today(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private fun yesterday(): String {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(cal.time)
    }

    // ── Date X days ago ───────────────────────────────────────────
    fun daysAgo(days: Int): String {
        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(cal.time)
    }

    suspend fun insert(habit: Habit) =
        dao.insertHabit(habit)

    suspend fun delete(habit: Habit) =
        dao.deleteHabit(habit)

    suspend fun toggleCompletion(habit: Habit) {

        val today = today()

        val isAlreadyDoneToday =
            habit.isCompletedToday &&
                    habit.lastCompletedDate == today

        if (isAlreadyDoneToday) {

            // Undo completion
            val restoredStreak =
                (habit.streak - 1).coerceAtLeast(0)

            dao.updateHabit(
                habit.copy(
                    isCompletedToday = false,
                    streak = restoredStreak,
                    totalCompletions = (habit.totalCompletions - 1)
                        .coerceAtLeast(0),
                    lastCompletedDate = ""
                )
            )

        } else {

            // Calculate streak
            val newStreak = when (habit.lastCompletedDate) {
                yesterday() -> habit.streak + 1
                today       -> habit.streak
                else        -> 1
            }

            val newLongest =
                maxOf(newStreak, habit.longestStreak)

            // Insert history log
            logDao.insertLog(
                HabitLog(
                    habitId = habit.id,
                    date = today,
                    completed = true
                )
            )

            // Update habit
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

    // Reset stale completions
    suspend fun refreshDailyState() =
        dao.resetStaleCompletions(today())

    suspend fun getTotalCount() =
        dao.getTotalCount()

    suspend fun getCompletedTodayCount() =
        dao.getCompletedTodayCount()

    // ── Real 30-day heatmap data ──────────────────────────────────
    suspend fun getLast30DaysData(): Map<String, Int> {
        val startDate = daysAgo(29)
        val counts    = logDao.getDailyCompletionCounts(startDate)
        return counts.associate { it.date to it.count }
    }

    // ── Real 7-day weekly data ────────────────────────────────────
    suspend fun getLast7DaysData(): Map<String, Int> {
        val startDate = daysAgo(6)
        val counts    = logDao.getDailyCompletionCounts(startDate)
        return counts.associate { it.date to it.count }
    }

    // ── Delete all data ───────────────────────────────────────────
    suspend fun deleteAllHabits() {
        dao.deleteAllHabits()
        logDao.deleteAllLogs()
    }
}