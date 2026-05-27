package com.anant.disciplinecore.core.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    // =========================================================
    // LOADING STATE
    // =========================================================

    private val _isLoading =
        MutableLiveData(false)

    val isLoading: LiveData<Boolean>
        get() = _isLoading

    // =========================================================
    // ERROR STATE
    // =========================================================

    private val _errorMessage =
        MutableLiveData<String?>()

    val errorMessage: LiveData<String?>
        get() = _errorMessage

    // =========================================================
    // SUCCESS MESSAGE
    // =========================================================

    private val _successMessage =
        MutableLiveData<String?>()

    val successMessage: LiveData<String?>
        get() = _successMessage

    // =========================================================
    // STATE HELPERS
    // =========================================================

    protected fun setLoading(
        loading: Boolean
    ) {

        _isLoading.value = loading
    }

    protected fun showError(
        message: String
    ) {

        _errorMessage.value = message
    }

    protected fun showSuccess(
        message: String
    ) {

        _successMessage.value = message
    }

    fun clearMessages() {

        _errorMessage.value = null
        _successMessage.value = null
    }
}