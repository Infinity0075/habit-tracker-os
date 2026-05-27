package com.anant.disciplinecore.core.utils

object Constants {

    // =========================================================
    // DATABASE
    // =========================================================

    const val DATABASE_NAME =
        "discipline_core_database"

    // =========================================================
    // NOTIFICATION
    // =========================================================

    const val NOTIFICATION_CHANNEL_ID =
        "discipline_core_channel"

    const val NOTIFICATION_CHANNEL_NAME =
        "DisciplineCore"

    // =========================================================
    // REMINDER
    // =========================================================

    const val DAILY_REMINDER_REQUEST_CODE =
        1001

    const val DAILY_REMINDER_ACTION =
        "DAILY_REMINDER"

    // =========================================================
    // POMODORO DEFAULTS
    // =========================================================

    const val DEFAULT_FOCUS_MINUTES =
        25

    const val DEFAULT_SHORT_BREAK =
        5

    const val DEFAULT_LONG_BREAK =
        15

    // =========================================================
    // LIMITS
    // =========================================================

    const val MAX_FOCUS_MINUTES =
        60

    const val MIN_FOCUS_MINUTES =
        5

    const val MAX_SHORT_BREAK =
        30

    const val MIN_SHORT_BREAK =
        1

    const val MAX_LONG_BREAK =
        60

    const val MIN_LONG_BREAK =
        5

    // =========================================================
    // DISCIPLINE SCORE
    // =========================================================

    const val MAX_DISCIPLINE_SCORE =
        1000

    // =========================================================
    // ANIMATION
    // =========================================================

    const val DEFAULT_ANIMATION_DURATION =
        300L

    const val CLICK_ANIMATION_DURATION =
        80L

    // =========================================================
    // SHARED PREFS
    // =========================================================

    const val SETTINGS_PREFS =
        "settings_prefs"

    const val THEME_PREFS =
        "theme_prefs"

    const val JOURNAL_PREFS =
        "journal_prefs"
}