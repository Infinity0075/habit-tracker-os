package com.anant.disciplinecore.data

import androidx.lifecycle.LiveData
import java.text.SimpleDateFormat
import java.util.*

class HabitRepository(
    private val dao: HabitDao,
    private val logDao: HabitLogDao
) {

    // ------------------------------------------------
    // LIVE HABITS
    // ------------------------------------------------

    val allHabits: LiveData<List<Habit>> =
        dao.getAllHabits()

    // ------------------------------------------------
    // DATE HELPERS
    // ------------------------------------------------

    private fun today(): String =
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())

    private fun yesterday(): String {

        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(cal.time)
    }

    fun daysAgo(days: Int): String {

        val cal = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }

        return SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(cal.time)
    }

    // ------------------------------------------------
    // INSERT
    // ------------------------------------------------

    suspend fun insert(habit: Habit) {

        dao.insertHabit(
            habit.copy(
                daysTracked = 1,
                consistencyScore = 0,
                weeklyPattern = "0000000"
            )
        )
    }

    // ------------------------------------------------
    // DELETE
    // ------------------------------------------------

    suspend fun delete(habit: Habit) =
        dao.deleteHabit(habit)

    // ------------------------------------------------
    // TOGGLE COMPLETION
    // ------------------------------------------------

    suspend fun toggleCompletion(habit: Habit) {

        val today = today()

        val alreadyDoneToday =
            habit.isCompletedToday &&
                    habit.lastCompletedDate == today

        // ============================================
        // UNDO COMPLETION
        // ============================================

        if (alreadyDoneToday) {

            val restoredStreak =
                (habit.streak - 1)
                    .coerceAtLeast(0)

            val restoredTotal =
                (habit.totalCompletions - 1)
                    .coerceAtLeast(0)

            val updatedPattern =
                updateWeeklyPattern(
                    habit.weeklyPattern,
                    completed = false
                )

            val updatedConsistency =
                calculateConsistency(
                    restoredTotal,
                    habit.daysTracked
                )

            dao.updateHabit(
                habit.copy(
                    isCompletedToday = false,
                    streak = restoredStreak,
                    totalCompletions = restoredTotal,
                    consistencyScore = updatedConsistency,
                    weeklyPattern = updatedPattern,
                    lastCompletedDate = ""
                )
            )

            return
        }

        // ============================================
        // STREAK LOGIC
        // ============================================

        val newStreak = when (habit.lastCompletedDate) {

            yesterday() ->
                habit.streak + 1

            today ->
                habit.streak

            else ->
                1
        }

        val newLongest =
            maxOf(newStreak, habit.longestStreak)

        // ============================================
        // ANALYTICS
        // ============================================

        val updatedDaysTracked =
            maxOf(
                habit.daysTracked,
                calculateDaysTracked(
                    habit.createdAt
                )
            )

        val updatedTotal =
            habit.totalCompletions + 1

        val updatedConsistency =
            calculateConsistency(
                updatedTotal,
                updatedDaysTracked
            )

        val updatedPattern =
            updateWeeklyPattern(
                habit.weeklyPattern,
                completed = true
            )

        // ============================================
        // SAVE LOG
        // ============================================

        logDao.insertLog(
            HabitLog(
                habitId = habit.id,
                date = today,
                completed = true
            )
        )

        // ============================================
        // UPDATE HABIT
        // ============================================

        dao.updateHabit(
            habit.copy(
                isCompletedToday = true,
                streak = newStreak,
                longestStreak = newLongest,
                totalCompletions = updatedTotal,
                consistencyScore = updatedConsistency,
                weeklyPattern = updatedPattern,
                daysTracked = updatedDaysTracked,
                lastCompletedDate = today
            )
        )
    }

    // ------------------------------------------------
    // DAILY RESET
    // ------------------------------------------------

    suspend fun refreshDailyState() {

        dao.resetStaleCompletions(today())
    }

    // ------------------------------------------------
    // COUNTS
    // ------------------------------------------------

    suspend fun getTotalCount() =
        dao.getTotalCount()

    suspend fun getCompletedTodayCount() =
        dao.getCompletedTodayCount()

    // ------------------------------------------------
    // 30 DAY DATA
    // ------------------------------------------------

    suspend fun getLast30DaysData():
            Map<String, Int> {

        val startDate = daysAgo(29)

        val counts =
            logDao.getDailyCompletionCounts(startDate)

        return counts.associate {
            it.date to it.count
        }
    }

    // ------------------------------------------------
    // 7 DAY DATA
    // ------------------------------------------------

    suspend fun getLast7DaysData():
            Map<String, Int> {

        val startDate = daysAgo(6)

        val counts =
            logDao.getDailyCompletionCounts(startDate)

        return counts.associate {
            it.date to it.count
        }
    }

    // ------------------------------------------------
    // DELETE ALL
    // ------------------------------------------------

    suspend fun deleteAllHabits() {

        dao.deleteAllHabits()

        logDao.deleteAllLogs()
    }

    // =================================================
    // ANALYTICS HELPERS
    // =================================================

    // ------------------------------------------------
    // CONSISTENCY %
    // ------------------------------------------------

    private fun calculateConsistency(
        completions: Int,
        daysTracked: Int
    ): Int {

        if (daysTracked <= 0) return 0

        return (
                (completions.toFloat() /
                        daysTracked.toFloat()) * 100f
                ).toInt()
            .coerceIn(0, 100)
    }

    // ------------------------------------------------
    // DAYS TRACKED
    // ------------------------------------------------

    private fun calculateDaysTracked(
        createdAt: Long
    ): Int {

        val diff =
            System.currentTimeMillis() - createdAt

        return ((diff / (1000 * 60 * 60 * 24)) + 1)
            .toInt()
    }

    // ------------------------------------------------
    // WEEKLY PATTERN
    // ------------------------------------------------

    private fun updateWeeklyPattern(
        current: String,
        completed: Boolean
    ): String {

        val normalized =
            current.padStart(7, '0')
                .takeLast(7)

        val updated =
            normalized.drop(1) +
                    if (completed) "1" else "0"

        return updated
    }
}