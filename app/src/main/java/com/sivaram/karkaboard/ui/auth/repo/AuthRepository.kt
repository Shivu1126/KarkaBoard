package com.sivaram.karkaboard.ui.auth.repo

import android.content.Context
import com.sivaram.karkaboard.data.dto.StudentsData
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.LoginState
import com.sivaram.karkaboard.ui.auth.state.VerifyState

interface AuthRepository {
    suspend fun checkIfUserExists(
        email: String, mobileNumber: String, onResult: (Boolean, String) -> Unit
    )

    suspend fun getOtp(
        mobileNumber: String, context: Context, onTimeOut: (AuthFlowState.OtpTimeout) -> Unit
    ): AuthFlowState

    suspend fun verifyPhoneCredential(
        otp: String, verificationId: String, mobile: String, email: String, password: String
    ): VerifyState

    suspend fun registerStudentDetails(studentsData: StudentsData, onResult: (Boolean) -> Unit)
    suspend fun login(email: String, password: String): LoginState
}