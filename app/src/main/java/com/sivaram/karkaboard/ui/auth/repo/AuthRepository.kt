package com.sivaram.karkaboard.ui.auth.repo

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.ChangePasswordState
import com.sivaram.karkaboard.ui.auth.state.LoginState
import com.sivaram.karkaboard.ui.auth.state.LogoutState
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

    suspend fun registerStudentDetails(studentsData: UserData, onResult: (Boolean) -> Unit)
    suspend fun login(email: String, password: String): LoginState
    suspend fun getMobileNoByMail(email: String, onResult: (Boolean, String, String) -> Unit)
    suspend fun resetPassword(password: String, onResult: (Boolean) -> Unit)
    suspend fun signOut(): LogoutState
    suspend fun getAuth(): FirebaseAuth
    suspend fun changePassword(email: String, oldPassword: String, newPassword: String): ChangePasswordState
}