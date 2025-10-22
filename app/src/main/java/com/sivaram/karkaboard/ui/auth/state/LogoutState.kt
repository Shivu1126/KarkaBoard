package com.sivaram.karkaboard.ui.auth.state

sealed class LogoutState {
    object Idle : LogoutState()
    object Success : LogoutState()
    data class Error(val message: String) : LogoutState()
}