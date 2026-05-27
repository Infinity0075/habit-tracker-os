package com.anant.disciplinecore.core.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.anant.disciplinecore.R
import kotlin.math.min

class FocusTimerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(
    context,
    attrs
) {

    private var progress = 100f
    private var timerText = "25:00"

    private val backgroundPaint = Paint().apply {

        color = context.getColor(
            R.color.bg_input
        )

        style = Paint.Style.STROKE
        strokeWidth = 22f
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {

        color = context.getColor(
            R.color.accent_amber
        )

        style = Paint.Style.STROKE
        strokeWidth = 22f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint().apply {

        color = context.getColor(
            android.R.color.white
        )

        textSize = 82f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val labelPaint = Paint().apply {

        color = context.getColor(
            R.color.soft_gold
        )

        textSize = 30f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    // =========================================================
    // DRAW
    // =========================================================

    override fun onDraw(
        canvas: Canvas
    ) {

        super.onDraw(canvas)

        val size =
            min(width, height).toFloat()

        val radius =
            (size / 2f) - 40f

        val centerX = width / 2f
        val centerY = height / 2f

        // Background Circle
        canvas.drawCircle(
            centerX.toFloat(),
            centerY.toFloat(),
            radius,
            backgroundPaint
        )

        // Progress Arc
        val sweepAngle =
            (progress / 100f) * 360f

        val rect = RectF(
            40f,
            40f,
            width - 40f,
            height - 40f
        )

        canvas.drawArc(
            rect,
            -90f,
            sweepAngle,
            false,
            progressPaint
        )

        // Timer Text
        canvas.drawText(
            timerText,
            centerX.toFloat(),
            centerY.toFloat() + 20f,
            textPaint
        )

        // Label
        canvas.drawText(
            "FOCUS",
            centerX.toFloat(),
            centerY.toFloat() + 90f,
            labelPaint
        )
    }

    // =========================================================
    // SET PROGRESS
    // =========================================================

    fun setProgress(
        value: Float
    ) {

        progress =
            value.coerceIn(0f, 100f)

        invalidate()
    }

    // =========================================================
    // SET TIMER TEXT
    // =========================================================

    fun setTimerText(
        text: String
    ) {

        timerText = text

        invalidate()
    }
}