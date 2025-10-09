package com.sivaram.karkaboard.ui.interviewmanagement.state

sealed class ApplicationState {
    object Idle : ApplicationState()
    object Loading : ApplicationState()
    data class Success(val message: String, val isSelected: Boolean) : ApplicationState()
    data class Error(val message: String) : ApplicationState()
}