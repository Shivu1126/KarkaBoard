package com.sivaram.karkaboard.ui.auth.state

sealed class AuthFlowState {
    object Idle : AuthFlowState()
    object Loading : AuthFlowState()
    data class OtpSent(val verificationId: String, val token: String) : AuthFlowState()
    data class MailAndPhoneNumVerified(val message: String) : AuthFlowState()
    data class OtpTimeout(val message: String) : AuthFlowState()
    data class Success(val uId: String) : AuthFlowState()
    data class Error(val message: String) : AuthFlowState()
}
