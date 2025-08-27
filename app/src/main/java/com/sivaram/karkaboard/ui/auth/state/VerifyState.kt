package com.sivaram.karkaboard.ui.auth.state

sealed class VerifyState {
    object Idle : VerifyState()
    object Loading : VerifyState()
    data class Success(val message: String, val userUid: String?) : VerifyState()
    data class Error(val message: String) : VerifyState()
}