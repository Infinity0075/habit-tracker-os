package com.anant.disciplinecore.core.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.anant.disciplinecore.core.utils.UiUtils

abstract class BaseFragment(
    layoutRes: Int
) : Fragment(layoutRes) {

    // =========================================================
    // LIFECYCLE
    // =========================================================

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {

        super.onViewCreated(
            view,
            savedInstanceState
        )

        setupViews()
        setupListeners()
        observeData()
    }

    // =========================================================
    // ABSTRACT FUNCTIONS
    // =========================================================

    protected open fun setupViews() {}

    protected open fun setupListeners() {}

    protected open fun observeData() {}

    // =========================================================
    // HELPERS
    // =========================================================

    protected fun showToast(
        message: String
    ) {

        context?.let {
            UiUtils.showToast(
                it,
                message
            )
        }
    }

    protected fun fadeIn(
        view: View
    ) {

        UiUtils.fadeIn(view)
    }

    protected fun fadeOut(
        view: View
    ) {

        UiUtils.fadeOut(view)
    }
}