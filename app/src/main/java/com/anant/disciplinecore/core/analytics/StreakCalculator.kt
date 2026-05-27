package com.anant.disciplinecore.core.analytics

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StreakCalculator {

    private val dateFormat =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // =========================================================
    // CHECK IF YESTERDAY
    // =========================================================

    fun isYesterday(
        lastCompletedDate: String?
    ): Boolean {

        if (lastCompletedDate.isNullOrEmpty()) {
            return false
        }

        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        return lastCompletedDate ==
                dateFormat.format(yesterday.time)
    }

    // =========================================================
    // CHECK IF TODAY
    // =========================================================

    fun isToday(
        date: String?
    ): Boolean {

        if (date.isNullOrEmpty()) {
            return false
        }

        val today =
            dateFormat.format(Calendar.getInstance().time)

        return date == today
    }

    // =========================================================
    // CALCULATE NEW STREAK
    // =========================================================

    fun calculateUpdatedStreak(
        currentStreak: Int,
        lastCompletedDate: String?
    ): Int {

        return when {

            isToday(lastCompletedDate) ->
                currentStreak

            isYesterday(lastCompletedDate) ->
                currentStreak + 1

            else ->
                1
        }
    }

    // =========================================================
    // CALCULATE LONGEST STREAK
    // =========================================================

    fun calculateLongestStreak(
        currentStreak: Int,
        longestStreak: Int
    ): Int {

        return maxOf(
            currentStreak,
            longestStreak
        )
    }

    // =========================================================
    // TODAY DATE
    // =========================================================

    fun getTodayDate(): String {

        return dateFormat.format(
            Calendar.getInstance().time
        )
    }
}