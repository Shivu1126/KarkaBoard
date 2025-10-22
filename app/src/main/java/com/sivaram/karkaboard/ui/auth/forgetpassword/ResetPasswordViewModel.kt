package com.sivaram.karkaboard.ui.auth.forgetpassword

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.utils.AuthUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
): ViewModel() {
    fun resetPassword(newPassword: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.resetPassword(newPassword, onResult)
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
        }
    }

    fun validatePassword(password: String): Boolean{
        return !AuthUtils.validatePassword(password)
    }
}