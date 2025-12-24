package com.sivaram.karkaboard.ui.faculty.taskmanagement.state

sealed class StudentLoadState
{
    object Idle: StudentLoadState()
    object Loading: StudentLoadState()
    object Success: StudentLoadState()
    object Error: StudentLoadState()
}
