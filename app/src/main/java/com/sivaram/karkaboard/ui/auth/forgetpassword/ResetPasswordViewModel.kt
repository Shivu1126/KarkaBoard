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
    fun resetPassword(newPassword: String, context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.resetPassword(newPassword, context, onResult)
        }
    }

    fun signOut(context: Context) {
        viewModelScope.launch {
            repository.signOut(context)
        }
    }

    fun validatePassword(password: String): Boolean{
        return !AuthUtils.validatePassword(password)
    }
}