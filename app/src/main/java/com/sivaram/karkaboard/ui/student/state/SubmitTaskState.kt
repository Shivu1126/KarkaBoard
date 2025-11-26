package com.sivaram.karkaboard.ui.student.state

sealed class SubmitTaskState {
    object Idle : SubmitTaskState()
    object Loading : SubmitTaskState()
    data class Error(val message: String) : SubmitTaskState()
    data class Success(val message: String) : SubmitTaskState()
}