package com.sivaram.karkaboard.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.state.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val databaseRepository: DatabaseRepository
): ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _rolesList = MutableLiveData<List<RolesData>>(emptyList())
    val rolesList: LiveData<List<RolesData>> = _rolesList

    fun login(email: String, password: String){
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            _loginState.value = authRepository.login(email, password)
        }
    }

    fun getRolesList(){
        viewModelScope.launch {
            databaseRepository.getRoles().observeForever { data ->
                _rolesList.value = data
            }
        }
    }

    fun resetLoginState(){
        _loginState.value = LoginState.Idle
    }
}