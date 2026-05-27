package com.anant.disciplinecore.core.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.anant.disciplinecore.R

class StreakBadgeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(
    context,
    attrs
) {

    init {

        background =
            context.getDrawable(
                R.drawable.bg_streak_badge
            )

        setTextColor(
            context.getColor(
                android.R.color.white
            )
        )

        textSize = 13f

        setPadding(
            28,
            14,
            28,
            14
        )

        text = "🔥 0 Day Streak"
    }

    // =========================================================
    // SET STREAK
    // =========================================================

    fun setStreak(
        streak: Int
    ) {

        text = when {

            streak <= 0 ->
                "Start Today"

            streak == 1 ->
                "🔥 1 Day Streak"

            else ->
                "🔥 $streak Day Streak"
        }
    }
}