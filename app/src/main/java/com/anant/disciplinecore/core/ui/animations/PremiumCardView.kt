package com.anant.disciplinecore.core.ui.components

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.card.MaterialCardView
import com.anant.disciplinecore.R

class PremiumCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(
    context,
    attrs,
    defStyleAttr
) {

    init {

        radius = 32f

        cardElevation = 0f

        strokeWidth = 2

        setCardBackgroundColor(
            context.getColor(
                R.color.bg_card
            )
        )

        strokeColor =
            context.getColor(
                R.color.stroke_light
            )

        preventCornerOverlap = true
        useCompatPadding = true
    }
}