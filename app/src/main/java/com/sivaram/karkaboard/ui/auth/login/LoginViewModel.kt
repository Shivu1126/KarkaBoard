package com.sivaram.karkaboard.ui.auth.login

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.LoginState
import com.sivaram.karkaboard.ui.auth.state.VerifyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    private val _verifyState = MutableStateFlow<VerifyState>(VerifyState.Idle)
    val verifyState: StateFlow<VerifyState> = _verifyState

    private val _otp = MutableStateFlow(List(6) { "" }) // 6 boxes
    val otp: StateFlow<List<String>> = _otp

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authFlowState = MutableStateFlow<AuthFlowState>(AuthFlowState.Idle)
    val authFlowState: StateFlow<AuthFlowState> = _authFlowState

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

    fun getMobileNoByMail(email: String, onResult: (Boolean, String, String) -> Unit){
        viewModelScope.launch {
            authRepository.getMobileNoByMail(email, onResult)
        }
    }

    fun checkEnd(emailId: String, mailEndIndex: Int, roleItemData: List<RolesData>, onResult: (Boolean, String) -> Unit){
        var isEnd = false
        var mailEnd = ""
        if (mailEndIndex != 0) {
            if(! emailId.endsWith(roleItemData[mailEndIndex].content)){
                mailEnd = roleItemData[mailEndIndex].content
            }
        }
        else{
            roleItemData.forEach {
                if (emailId.endsWith(it.content)) {
                    isEnd = true
                }
            }
        }
        onResult(isEnd, emailId+mailEnd)
    }

    fun getOtp(
        mobileNumber: String,
        context: Context,
        onTimeOut: (AuthFlowState.OtpTimeout) -> Unit
    ) {
        viewModelScope.launch {
            _authFlowState.value = AuthFlowState.Loading
            _authFlowState.value = authRepository.getOtp(mobileNumber, context, onTimeOut)
        }
    }

    fun updateOtp(index: Int, value: String) {
        _otp.value = _otp.value.toMutableList().also { it[index] = value }
    }

    fun verifyCredential(verificationId: String, otp: String) {
        viewModelScope.launch {
            _verifyState.value = VerifyState.Loading
            _verifyState.value = verifyOtp(verificationId, otp)
        }
    }

    private suspend fun verifyOtp(verificationId: String, otp: String): VerifyState{
        return try{
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user
            if(user != null){
                VerifyState.Success("Verification successful", user.uid)
            }
            else{
                VerifyState.Error("Verification failed")
            }
        }
        catch (e: Exception){
            VerifyState.Error("Verification failed")
        }
    }


    fun resetLoginState(){
        _loginState.value = LoginState.Idle
    }

    fun resetVerifyState(){
        _verifyState.value = VerifyState.Idle
    }

    fun resetAuthFlowState() {
        _authFlowState.value = AuthFlowState.Idle
    }
}