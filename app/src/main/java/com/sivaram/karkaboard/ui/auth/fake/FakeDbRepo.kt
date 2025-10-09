package com.sivaram.karkaboard.ui.auth.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.ApplicationPortalData
import com.sivaram.karkaboard.data.dto.AppliedStudentData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StudentData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.interviewmanagement.state.AcceptState
import com.sivaram.karkaboard.ui.interviewmanagement.state.ApplicationState
import com.sivaram.karkaboard.ui.interviewmanagement.state.DeclineState

class FakeDbRepo: DatabaseRepository {
    override fun getRoles(): LiveData<List<RolesData>> {
        return MutableLiveData(emptyList())
    }

    override fun getCurrentUser(): LiveData<UserData?> {
        return MutableLiveData(null)
    }

    override suspend fun getAllBatches(): LiveData<List<BatchData>> {
        return MutableLiveData(emptyList())
    }

    override suspend fun getStudentData(uid: String): LiveData<StudentData?> {
        return MutableLiveData()
    }

    override suspend fun getApplicationPortalData(studentId: String): LiveData<List<ApplicationPortalData>> {
        return MutableLiveData(emptyList())
    }

    override fun getAppliedStudentDetail(batchId: String, filterId: Int): LiveData<List<AppliedStudentData>> {
        return MutableLiveData(emptyList())
    }

    override suspend fun shortlistForInterview(
        applicationId: String,
        processId: Int
    ): AcceptState {
        return AcceptState.Idle
    }

    override suspend fun declineForInterview(applicationData: ApplicationData): DeclineState {
        return DeclineState.Idle
    }

    override suspend fun selectedForTraining(
        applicationData: ApplicationData,
        studentDocId: String
    ): ApplicationState {
        return ApplicationState.Idle
    }

    override suspend fun rejectedFromInterview(
        applicationData: ApplicationData
    ): ApplicationState {
        return ApplicationState.Idle
    }
}