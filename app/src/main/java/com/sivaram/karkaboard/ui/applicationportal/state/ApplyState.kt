package com.sivaram.karkaboard.ui.applicationportal.state

sealed class ApplyState {
    object Idle : ApplyState()
    object Loading : ApplyState()
    data class Success(val message: String, val isApplied: Boolean) : ApplyState()
    data class Error(val message: String) : ApplyState()
}