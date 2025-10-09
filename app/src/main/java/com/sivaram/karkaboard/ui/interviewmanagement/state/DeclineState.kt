package com.sivaram.karkaboard.ui.interviewmanagement.state

sealed class DeclineState {
    object Idle : DeclineState()
    object Loading : DeclineState()
    data class Success(val message: String) : DeclineState()
    data class Error(val message: String) : DeclineState()
}