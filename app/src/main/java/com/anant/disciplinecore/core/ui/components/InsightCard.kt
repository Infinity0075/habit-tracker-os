package com.anant.disciplinecore.core.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.anant.disciplinecore.R

class InsightCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(
    context,
    attrs
) {

    private val titleText: TextView
    private val messageText: TextView

    init {

        orientation = VERTICAL

        background =
            context.getDrawable(
                R.drawable.bg_home_insight
            )

        setPadding(
            40,
            32,
            40,
            32
        )

        titleText = TextView(context).apply {

            text = "Daily Insight"

            textSize = 14f

            setTextColor(
                context.getColor(
                    R.color.soft_gold
                )
            )
        }

        messageText = TextView(context).apply {

            text = "Consistency compounds into greatness."

            textSize = 16f

            setTextColor(
                context.getColor(
                    android.R.color.white
                )
            )

            setPadding(
                0,
                16,
                0,
                0
            )
        }

        addView(titleText)
        addView(messageText)
    }

    // =========================================================
    // SET INSIGHT
    // =========================================================

    fun setInsight(
        title: String,
        message: String
    ) {

        titleText.text = title
        messageText.text = message
    }
}