package com.sivaram.karkaboard.ui.auth.fake

import android.content.Context
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.LoginState
import com.sivaram.karkaboard.ui.auth.state.LogoutState
import com.sivaram.karkaboard.ui.auth.state.VerifyState


class FakeRepo : AuthRepository {
    override suspend fun checkIfUserExists(
        email: String,
        mobileNumber: String,
        onResult: (Boolean, String) -> Unit
    ) {
        onResult(true, "")
    }

    override suspend fun getOtp(
        mobileNumber: String,
        context: Context,
        onTimeOut: (AuthFlowState.OtpTimeout) -> Unit
    ): AuthFlowState {
        return AuthFlowState.Idle
    }

    override suspend fun verifyPhoneCredential(
        otp: String,
        verificationId: String,
        mobile: String,
        email: String,
        password: String
    ): VerifyState {
        return VerifyState.Idle
    }

    override suspend fun registerStudentDetails(
        studentsData: UserData,
        onResult: (Boolean) -> Unit
    ) {

    }

    override suspend fun login(
        email: String,
        password: String
    ): LoginState {
        return LoginState.Idle
    }

    override suspend fun getMobileNoByMail(email: String, onResult: (Boolean, String, String) -> Unit) {

    }

    override suspend fun resetPassword(
        password: String,
        context: Context,
        onResult: (Boolean) -> Unit
    ) {

    }

    override suspend fun signOut(context: Context): LogoutState {
        return LogoutState.Idle
    }

}