package com.sivaram.karkaboard.ui.faculty.taskmanagement.state

sealed class AssignTaskState {
    object Idle : AssignTaskState()
    object Loading : AssignTaskState()
    data class Error(val message: String) : AssignTaskState()
    data class Success(val message: String) : AssignTaskState()
}