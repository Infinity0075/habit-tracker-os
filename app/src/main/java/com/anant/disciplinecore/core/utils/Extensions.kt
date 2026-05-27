package com.anant.disciplinecore.core.utils

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

// =========================================================
// VIEW VISIBILITY
// =========================================================

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// =========================================================
// ENABLE / DISABLE
// =========================================================

fun View.enable() {
    isEnabled = true
    alpha = 1f
}

fun View.disable() {
    isEnabled = false
    alpha = 0.5f
}

// =========================================================
// CLICK ANIMATION
// =========================================================

fun View.applyScaleAnimation() {

    animate()
        .scaleX(0.96f)
        .scaleY(0.96f)
        .setDuration(80)
        .withEndAction {

            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(80)
                .start()
        }
        .start()
}

// =========================================================
// SNACKBAR
// =========================================================

fun View.showSnackBar(
    message: String
) {

    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_SHORT
    ).show()
}

// =========================================================
// TEXT COLOR
// =========================================================

fun TextView.setTextColorRes(
    colorRes: Int
) {

    setTextColor(
        ContextCompat.getColor(
            context,
            colorRes
        )
    )
}