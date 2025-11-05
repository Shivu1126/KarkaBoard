package com.sivaram.karkaboard.ui.faculty.taskmanagement.state

sealed class ValidationState {
    object Idle : ValidationState()
    object Success : ValidationState()
    data class Error(val message: String) : ValidationState()
}