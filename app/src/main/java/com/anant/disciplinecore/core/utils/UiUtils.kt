package com.anant.disciplinecore.core.utils

import android.content.Context
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast

object UiUtils {

    // =========================================================
    // SHOW TOAST
    // =========================================================

    fun showToast(
        context: Context,
        message: String
    ) {

        Toast.makeText(
            context,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    // =========================================================
    // FADE IN
    // =========================================================

    fun fadeIn(
        view: View,
        duration: Long = 300
    ) {

        view.alpha = 0f
        view.visibility = View.VISIBLE

        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(
                DecelerateInterpolator()
            )
            .start()
    }

    // =========================================================
    // FADE OUT
    // =========================================================

    fun fadeOut(
        view: View,
        duration: Long = 300
    ) {

        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {

                view.visibility = View.GONE
            }
            .start()
    }

    // =========================================================
    // SCALE CLICK EFFECT
    // =========================================================

    fun applyClickAnimation(
        view: View
    ) {

        view.animate()
            .scaleX(0.96f)
            .scaleY(0.96f)
            .setDuration(80)
            .withEndAction {

                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(80)
                    .start()
            }
            .start()
    }

    // =========================================================
    // ENABLE VIEW
    // =========================================================

    fun enable(
        view: View
    ) {

        view.isEnabled = true
        view.alpha = 1f
    }

    // =========================================================
    // DISABLE VIEW
    // =========================================================

    fun disable(
        view: View
    ) {

        view.isEnabled = false
        view.alpha = 0.5f
    }
}