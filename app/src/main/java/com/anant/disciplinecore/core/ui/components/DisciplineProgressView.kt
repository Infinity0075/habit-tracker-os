package com.anant.disciplinecore.core.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.anant.disciplinecore.R
import kotlin.math.min

class DisciplineProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(
    context,
    attrs
) {

    private var progress = 0f

    private val backgroundPaint = Paint().apply {

        color = context.getColor(
            R.color.bg_input
        )

        style = Paint.Style.STROKE
        strokeWidth = 18f
        isAntiAlias = true
    }

    private val progressPaint = Paint().apply {

        color = context.getColor(
            R.color.accent_amber
        )

        style = Paint.Style.STROKE
        strokeWidth = 18f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint().apply {

        color = context.getColor(
            android.R.color.white
        )

        textSize = 52f
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
            (size / 2f) - 24f

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

        canvas.drawArc(
            24f,
            24f,
            width - 24f,
            height - 24f,
            -90f,
            sweepAngle,
            false,
            progressPaint
        )

        // Percentage Text
        val text =
            "${progress.toInt()}%"

        canvas.drawText(
            text,
            centerX.toFloat(),
            centerY + 18f,
            textPaint
        )
    }

    // =========================================================
    // SET PROGRESS
    // =========================================================

    fun setProgress(
        value: Int
    ) {

        progress =
            value.coerceIn(0, 100).toFloat()

        invalidate()
    }
}