package com.anant.disciplinecore.core.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {

    private const val DEFAULT_PATTERN = "yyyy-MM-dd"

    // =========================================================
    // CURRENT DATE
    // =========================================================

    fun getCurrentDate(): String {

        return SimpleDateFormat(
            DEFAULT_PATTERN,
            Locale.getDefault()
        ).format(Date())
    }

    // =========================================================
    // FORMAT DATE
    // =========================================================

    fun formatDate(
        timestamp: Long,
        pattern: String = DEFAULT_PATTERN
    ): String {

        return SimpleDateFormat(
            pattern,
            Locale.getDefault()
        ).format(Date(timestamp))
    }

    // =========================================================
    // GET TODAY CALENDAR
    // =========================================================

    fun getTodayCalendar(): Calendar {

        return Calendar.getInstance()
    }

    // =========================================================
    // CHECK SAME DAY
    // =========================================================

    fun isSameDay(
        firstDate: Long,
        secondDate: Long
    ): Boolean {

        val firstCalendar = Calendar.getInstance().apply {
            timeInMillis = firstDate
        }

        val secondCalendar = Calendar.getInstance().apply {
            timeInMillis = secondDate
        }

        return firstCalendar.get(Calendar.YEAR) ==
                secondCalendar.get(Calendar.YEAR)
                &&
                firstCalendar.get(Calendar.DAY_OF_YEAR) ==
                secondCalendar.get(Calendar.DAY_OF_YEAR)
    }

    // =========================================================
    // DAYS BETWEEN
    // =========================================================

    fun getDaysBetween(
        start: Long,
        end: Long
    ): Int {

        val diff = end - start

        return (diff / (1000 * 60 * 60 * 24)).toInt()
    }

    // =========================================================
    // GET GREETING
    // =========================================================

    fun getGreeting(): String {

        val hour =
            Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        return when {

            hour < 12 ->
                "Good Morning"

            hour < 17 ->
                "Good Afternoon"

            else ->
                "Good Evening"
        }
    }
}