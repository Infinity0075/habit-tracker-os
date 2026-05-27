package com.anant.disciplinecore.core.ui.components

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.anant.disciplinecore.R

class GlowButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.buttonStyle
) : AppCompatButton(
    context,
    attrs,
    defStyleAttr
) {

    init {

        background =
            context.getDrawable(
                R.drawable.bg_primary_button
            )

        setTextColor(
            context.getColor(
                android.R.color.white
            )
        )

        textSize = 15f

        isAllCaps = false

        elevation = 0f

        setPadding(
            32,
            20,
            32,
            20
        )

        stateListAnimator = null
    }
}