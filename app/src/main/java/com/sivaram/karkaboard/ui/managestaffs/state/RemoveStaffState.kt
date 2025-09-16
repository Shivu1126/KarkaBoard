package com.sivaram.karkaboard.ui.managestaffs.state

sealed class RemoveStaffState {
    object Idle: RemoveStaffState()
    object Loading: RemoveStaffState()
    data class Success(val message: String): RemoveStaffState()
    data class Error(val message: String): RemoveStaffState()
}