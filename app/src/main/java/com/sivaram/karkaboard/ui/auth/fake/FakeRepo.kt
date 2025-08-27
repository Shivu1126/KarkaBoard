package com.sivaram.karkaboard.ui.auth.fake

import android.content.Context
import com.sivaram.karkaboard.data.dto.StudentsData
import com.sivaram.karkaboard.ui.auth.repo.AuthRepository
import com.sivaram.karkaboard.ui.auth.state.AuthFlowState
import com.sivaram.karkaboard.ui.auth.state.LoginState
import com.sivaram.karkaboard.ui.auth.state.VerifyState


class FakeRepo : AuthRepository {
    override suspend fun checkIfUserExists(
        email: String,
        mobileNumber: String,
        onResult: (Boolean, String) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getOtp(
        mobileNumber: String,
        context: Context,
        onTimeOut: (AuthFlowState.OtpTimeout) -> Unit
    ): AuthFlowState {
        TODO("Not yet implemented")
    }

    override suspend fun verifyPhoneCredential(
        otp: String,
        verificationId: String,
        mobile: String,
        email: String,
        password: String
    ): VerifyState {
        TODO("Not yet implemented")
    }

    override suspend fun registerStudentDetails(
        studentsData: StudentsData,
        onResult: (Boolean) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun login(
        email: String,
        password: String
    ): LoginState {
        TODO("Not yet implemented")
    }

}