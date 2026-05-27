package com.anant.disciplinecore.core.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.anant.disciplinecore.R

class MoodChip @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(
    context,
    attrs
) {

    private var selectedState = false

    init {

        background =
            context.getDrawable(
                R.drawable.bg_mood_unselected
            )

        setTextColor(
            context.getColor(
                android.R.color.white
            )
        )

        textSize = 14f

        setPadding(
            32,
            18,
            32,
            18
        )

        isClickable = true
        isFocusable = true
    }

    // =========================================================
    // SELECT
    // =========================================================

    fun setChipSelected(
        selected: Boolean
    ) {

        selectedState = selected

        background =
            context.getDrawable(
                if (selected)
                    R.drawable.bg_mood_selected
                else
                    R.drawable.bg_mood_unselected
            )
    }

    // =========================================================
    // STATE
    // =========================================================

    fun isChipSelected(): Boolean {
        return selectedState
    }
}