package com.anant.disciplinecore.core.ui.animations

import android.view.View
import android.view.animation.OvershootInterpolator

object CardAnimations {

    // =========================================================
    // CARD POP IN
    // =========================================================

    fun popIn(
        view: View,
        duration: Long = 350
    ) {

        view.scaleX = 0.85f
        view.scaleY = 0.85f
        view.alpha = 0f
        view.visibility = View.VISIBLE

        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(duration)
            .setInterpolator(
                OvershootInterpolator()
            )
            .start()
    }

    // =========================================================
    // CARD PRESS
    // =========================================================

    fun press(
        view: View,
        duration: Long = 80
    ) {

        view.animate()
            .scaleX(0.97f)
            .scaleY(0.97f)
            .setDuration(duration)
            .withEndAction {

                release(view)
            }
            .start()
    }

    // =========================================================
    // CARD RELEASE
    // =========================================================

    fun release(
        view: View,
        duration: Long = 80
    ) {

        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .start()
    }

    // =========================================================
    // SLIDE UP
    // =========================================================

    fun slideUp(
        view: View,
        duration: Long = 350
    ) {

        view.translationY = 80f
        view.alpha = 0f
        view.visibility = View.VISIBLE

        view.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .start()
    }

    // =========================================================
    // SLIDE DOWN
    // =========================================================

    fun slideDown(
        view: View,
        duration: Long = 300
    ) {

        view.animate()
            .translationY(80f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {

                view.visibility = View.GONE
            }
            .start()
    }
}