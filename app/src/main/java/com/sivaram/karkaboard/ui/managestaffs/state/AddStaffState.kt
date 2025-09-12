package com.sivaram.karkaboard.ui.managestaffs.state

sealed class AddStaffState {
    object Idle: AddStaffState()
    object Loading: AddStaffState()
    data class Success(val message: String): AddStaffState()
    data class Error(val message: String): AddStaffState()
}