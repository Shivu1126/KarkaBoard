package com.sivaram.karkaboard.ui.auth.register

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.ValidationState
import com.sivaram.karkaboard.ui.auth.state.VerifyState
import com.sivaram.karkaboard.ui.auth.utils.AuthUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val _authFlowState = MutableStateFlow<AuthFlowState>(AuthFlowState.Idle)
    val authFlowState: StateFlow<AuthFlowState> = _authFlowState

    private val _verifyState = MutableStateFlow<VerifyState>(VerifyState.Idle)
    val verifyState: StateFlow<VerifyState> = _verifyState

    private val _validationState = MutableLiveData<ValidationState>()
    val validationState: LiveData<ValidationState> = _validationState

    private val _otp = MutableStateFlow(List(6) { "" }) // 6 boxes
    val otp: StateFlow<List<String>> = _otp

    val rolesData: LiveData<List<RolesData>> = databaseRepository.getRoles()

    fun checkIfUserExists(
        email: String,
        mobileNumber: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            authRepository.checkIfUserExists(email, mobileNumber, onResult)
        }
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

    fun verifyPhoneCredential(
        otp: String,
        verificationId: String,
        mobile: String,
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            _verifyState.value = VerifyState.Loading
            _verifyState.value =
                authRepository.verifyPhoneCredential(otp, verificationId, mobile, email, password)
        }
    }

    fun registerStudentDetails(studentsData: UserData, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            authRepository.registerStudentDetails(studentsData, onResult)
        }
    }

    fun validateInputs(studentsData: UserData, password: String) {
        Log.d("studentsData", studentsData.toString())
        if (studentsData.profileImgUrl.isEmpty() || studentsData.profileImgUrl == "null") {
            _validationState.value = ValidationState.Error("Please upload a profile picture")
            return
        }
        if (studentsData.name.isEmpty()) {
            _validationState.value = ValidationState.Error("Please enter your full name")
            return
        }
        if (studentsData.email.isEmpty() || !validateEmail(studentsData.email)) {
            _validationState.value = ValidationState.Error("Please enter valid email")
            return
        }
        if (password.isEmpty() || !AuthUtils.validatePassword(password) ) {
            _validationState.value = ValidationState.Error("Please enter valid password")
            return
        }
        if (studentsData.mobile.isEmpty() || !validateMobile(studentsData.mobile)) {
            _validationState.value = ValidationState.Error("Please enter valid mobile number")
            return
        }
        if (studentsData.dob == "NA") {
            _validationState.value = ValidationState.Error("Please pick your date of birth")
            return
        }
        if(calculateAge(studentsData.dob) < 18){
            _validationState.value = ValidationState.Error("You must be 18 years old to register")
            return
        }
        if (studentsData.collegeName.isEmpty()) {
            _validationState.value = ValidationState.Error("Please enter your college name")
            return
        }
        if (studentsData.degree.isEmpty()) {
            _validationState.value = ValidationState.Error("Please enter your degree")
            return
        }
        if (studentsData.resumeUrl.isEmpty() || studentsData.resumeUrl == "null") {
            _validationState.value = ValidationState.Error("Please upload a resume")
            return
        }

        rolesData.value?.let { roleList ->
            val isAllowed = roleList.none { role -> studentsData.email.endsWith(role.content) }
            if (!isAllowed) {
                _validationState.value = ValidationState.Error("This email is not allowed")
                return
            }
        }



        _validationState.value = ValidationState.Success

    }
    private fun validateEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }
    private fun validateMobile(mobile: String): Boolean {
        val mobileRegex = Regex("^[0-9]{10}$")
        return mobileRegex.matches(mobile)
    }

    private fun calculateAge(dob: String): Int{
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dob = sdf.parse(dob) ?: return -1

        val dobCalendar = Calendar.getInstance().apply { time = dob }
        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - dobCalendar.get(Calendar.YEAR)

        // If today’s date is before the birthday this year, subtract 1
        if (today.get(Calendar.DAY_OF_YEAR) < dobCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    fun updateOtp(index: Int, value: String) {
        _otp.value = _otp.value.toMutableList().also { it[index] = value }
    }

    fun clearOtp() {
        _otp.value = List(6) { "" }
    }

    fun resetAuthFlowState() {
        _authFlowState.value = AuthFlowState.Idle
    }

    fun resetVerifyState() {
        _verifyState.value = VerifyState.Idle
    }
}