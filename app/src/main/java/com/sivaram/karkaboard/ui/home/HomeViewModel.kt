package com.sivaram.karkaboard.ui.home

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.sivaram.karkaboard.data.dto.StudentsData
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
    private val authRepository: AuthRepository,
    private val databaseRepository: DatabaseRepository
): ViewModel()
{

    private val _logoutState = MutableStateFlow<LogoutState>(LogoutState.Idle)
    val logoutState: StateFlow<LogoutState> = _logoutState

    private var _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    private val _studentData = MutableLiveData<StudentsData?>(null)
    val studentData: LiveData<StudentsData?> = _studentData

    fun signOut(context: Context){
        viewModelScope.launch {
            _logoutState.value = LogoutState.Idle
            _logoutState.value = authRepository.signOut(context)
        }
    }

    fun getStudentData(uid: String){
        viewModelScope.launch {
            databaseRepository.getStudentData(uid).observeForever {data ->
                _studentData.value = data
            }
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

    fun setUserData(userData: UserData?){
        _userData.value = userData
    }

    fun getCurrentEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email
    }
}