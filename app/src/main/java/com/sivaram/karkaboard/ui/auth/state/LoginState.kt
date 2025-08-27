package com.sivaram.karkaboard.ui.auth.state

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String, val userUid: String?) : LoginState()
    data class Error(val message: String) : LoginState()
}