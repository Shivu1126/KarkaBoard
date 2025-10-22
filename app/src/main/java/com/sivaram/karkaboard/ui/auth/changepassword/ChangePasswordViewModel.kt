package com.sivaram.karkaboard.ui.auth.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.state.ChangePasswordState
import com.sivaram.karkaboard.ui.auth.utils.AuthUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: MutableStateFlow<ChangePasswordState> = _changePasswordState

    fun changePassword(email: String, oldPassword: String, newPassword:String){
        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading
            _changePasswordState.value = authRepository.changePassword(email, oldPassword, newPassword)
        }
    }

    fun validatePassword(password: String): Boolean{
        return !AuthUtils.validatePassword(password)
    }

    fun resetChangePasswordState(){
        _changePasswordState.value = ChangePasswordState.Idle
    }
}