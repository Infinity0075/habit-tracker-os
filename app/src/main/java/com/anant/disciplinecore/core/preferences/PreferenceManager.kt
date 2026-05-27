package com.anant.disciplinecore.core.preferences

import android.content.Context

class PreferenceManager(
    private val context: Context
) {

    companion object {

        private const val PREFS = "settings_prefs"

        private const val KEY_NAME = "user_name"

        private const val KEY_REMINDER = "daily_reminder"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MIN = "reminder_min"

        private const val KEY_MIDNIGHT = "midnight_reset"

        private const val KEY_FOCUS = "focus_min"
        private const val KEY_SHORT_BREAK = "short_break_min"
        private const val KEY_LONG_BREAK = "long_break_min"

        // ─── STATS ─────────────────────────────────────────
        private const val KEY_CURRENT_STREAK = "current_streak"
        private const val KEY_TOTAL_FOCUS = "total_focus_minutes"
        private const val KEY_COMPLETED_DAYS = "completed_days"
        private const val KEY_TOTAL_TRACKED_DAYS = "total_tracked_days"
    }

    private val prefs =
        context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

    // =========================================================
    // USER
    // =========================================================

    fun saveUserName(name: String) {
        prefs.edit().putString(KEY_NAME, name).apply()
    }

    fun getUserName(): String {
        return prefs.getString(KEY_NAME, "") ?: ""
    }

    // =========================================================
    // DAILY REMINDER
    // =========================================================

    fun setDailyReminderEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMINDER, enabled).apply()
    }

    fun isDailyReminderEnabled(): Boolean {
        return prefs.getBoolean(KEY_REMINDER, true)
    }

    fun saveReminderHour(hour: Int) {
        prefs.edit().putInt(KEY_REMINDER_HOUR, hour).apply()
    }

    fun getReminderHour(): Int {
        return prefs.getInt(KEY_REMINDER_HOUR, 8)
    }

    fun saveReminderMinute(minute: Int) {
        prefs.edit().putInt(KEY_REMINDER_MIN, minute).apply()
    }

    fun getReminderMinute(): Int {
        return prefs.getInt(KEY_REMINDER_MIN, 0)
    }

    // =========================================================
    // MIDNIGHT RESET
    // =========================================================

    fun setMidnightResetEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MIDNIGHT, enabled).apply()
    }

    fun isMidnightResetEnabled(): Boolean {
        return prefs.getBoolean(KEY_MIDNIGHT, true)
    }

    // =========================================================
    // POMODORO
    // =========================================================

    fun saveFocusMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_FOCUS, minutes).apply()
    }

    fun getFocusMinutes(): Int {
        return prefs.getInt(KEY_FOCUS, 25)
    }

    fun saveShortBreak(minutes: Int) {
        prefs.edit().putInt(KEY_SHORT_BREAK, minutes).apply()
    }

    fun getShortBreak(): Int {
        return prefs.getInt(KEY_SHORT_BREAK, 5)
    }

    fun saveLongBreak(minutes: Int) {
        prefs.edit().putInt(KEY_LONG_BREAK, minutes).apply()
    }

    fun getLongBreak(): Int {
        return prefs.getInt(KEY_LONG_BREAK, 15)
    }

    // =========================================================
    // STATS
    // =========================================================

    fun saveCurrentStreak(streak: Int) {
        prefs.edit().putInt(KEY_CURRENT_STREAK, streak).apply()
    }

    fun getCurrentStreak(): Int {
        return prefs.getInt(KEY_CURRENT_STREAK, 0)
    }

    fun saveTotalFocusMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_TOTAL_FOCUS, minutes).apply()
    }

    fun getTotalFocusMinutes(): Int {
        return prefs.getInt(KEY_TOTAL_FOCUS, 0)
    }

    fun addFocusMinutes(minutes: Int) {
        val current = getTotalFocusMinutes()
        saveTotalFocusMinutes(current + minutes)
    }

    fun saveCompletedDays(days: Int) {
        prefs.edit().putInt(KEY_COMPLETED_DAYS, days).apply()
    }

    fun getCompletedDays(): Int {
        return prefs.getInt(KEY_COMPLETED_DAYS, 0)
    }

    fun saveTotalTrackedDays(days: Int) {
        prefs.edit().putInt(KEY_TOTAL_TRACKED_DAYS, days).apply()
    }

    fun getTotalTrackedDays(): Int {
        return prefs.getInt(KEY_TOTAL_TRACKED_DAYS, 0)
    }

    // =========================================================
    // CLEAR
    // =========================================================

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}