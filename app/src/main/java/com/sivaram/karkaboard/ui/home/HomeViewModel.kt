package com.sivaram.karkaboard.ui.home

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.local.ResetPasswordPref
import com.sivaram.karkaboard.data.local.RolePrefs
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.state.LogoutState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel()
{

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    fun signOut(context: Context){
        viewModelScope.launch {
            _logoutState.value = LogoutState.Loading
            _logoutState.value = authRepository.signOut(context)
        }
    }

    fun resetLogoutState(){
        _logoutState.value = LogoutState.Idle
    }

    fun resetLocalData(context: Context){
        viewModelScope.launch {
            ResetPasswordPref.setResetInProgress(context, false)
            RolePrefs.clear(context)
        }
    }
    fun getCurrentEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }
}