package com.sivaram.karkaboard.ui.interviewmanagement.state

sealed class AcceptState {
    object Idle : AcceptState()
    object Loading : AcceptState()
    data class Success(val message: String) : AcceptState()
    data class Error(val message: String) : AcceptState()
}