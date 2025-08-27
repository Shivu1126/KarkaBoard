package com.sivaram.karkaboard.ui.auth.state

sealed class ValidationState {
    object Success : ValidationState()
    data class Error(val message: String) : ValidationState()
}