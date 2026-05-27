package com.anant.disciplinecore.core.preferences

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(
    private val context: Context
) {

    companion object {

        private const val PREFS = "theme_prefs"

        private const val KEY_AMOLED = "amoled_mode"
        private const val KEY_DYNAMIC = "dynamic_colors"
    }

    private val prefs =
        context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

    // =========================================================
    // AMOLED MODE
    // =========================================================

    fun setAmoledMode(enabled: Boolean) {

        prefs.edit()
            .putBoolean(KEY_AMOLED, enabled)
            .apply()
    }

    fun isAmoledMode(): Boolean {

        return prefs.getBoolean(
            KEY_AMOLED,
            true
        )
    }

    // =========================================================
    // DYNAMIC COLORS
    // =========================================================

    fun setDynamicColors(enabled: Boolean) {

        prefs.edit()
            .putBoolean(KEY_DYNAMIC, enabled)
            .apply()
    }

    fun isDynamicColorsEnabled(): Boolean {

        return prefs.getBoolean(
            KEY_DYNAMIC,
            false
        )
    }

    // =========================================================
    // APPLY THEME
    // =========================================================

    fun applyTheme() {

        AppCompatDelegate.setDefaultNightMode(
            AppCompatDelegate.MODE_NIGHT_YES
        )
    }
}