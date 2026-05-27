package com.anant.disciplinecore.core.utils

import java.util.concurrent.TimeUnit

object TimeUtils {

    // =========================================================
    // FORMAT TIMER
    // =========================================================

    fun formatTime(
        millis: Long
    ): String {

        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(millis)

        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        return String.format(
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    // =========================================================
    // MINUTES TO MILLIS
    // =========================================================

    fun minutesToMillis(
        minutes: Int
    ): Long {

        return minutes * 60 * 1000L
    }

    // =========================================================
    // SECONDS TO MILLIS
    // =========================================================

    fun secondsToMillis(
        seconds: Int
    ): Long {

        return seconds * 1000L
    }

    // =========================================================
    // MILLIS TO MINUTES
    // =========================================================

    fun millisToMinutes(
        millis: Long
    ): Int {

        return TimeUnit.MILLISECONDS
            .toMinutes(millis)
            .toInt()
    }

    // =========================================================
    // HOURS + MINUTES FORMAT
    // =========================================================

    fun formatHoursMinutes(
        totalMinutes: Int
    ): String {

        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return when {

            hours > 0 ->
                "${hours}h ${minutes}m"

            else ->
                "${minutes}m"
        }
    }

    // =========================================================
    // SESSION LABEL
    // =========================================================

    fun getSessionLabel(
        durationMinutes: Int
    ): String {

        return when {

            durationMinutes >= 120 ->
                "Deep Work"

            durationMinutes >= 60 ->
                "Focused Session"

            durationMinutes >= 25 ->
                "Pomodoro Session"

            else ->
                "Quick Focus"
        }
    }
}