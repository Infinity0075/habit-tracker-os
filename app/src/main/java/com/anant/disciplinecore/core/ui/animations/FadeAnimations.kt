package com.anant.disciplinecore.core.ui.animations

import android.view.View
import android.view.animation.DecelerateInterpolator

object FadeAnimations {

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
    // FADE TO HALF
    // =========================================================

    fun fadeHalf(
        view: View,
        duration: Long = 300
    ) {

        view.animate()
            .alpha(0.5f)
            .setDuration(duration)
            .start()
    }

    // =========================================================
    // FADE FULL
    // =========================================================

    fun fadeFull(
        view: View,
        duration: Long = 300
    ) {

        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()
    }
}