package com.sivaram.karkaboard.data.remote.db

import androidx.lifecycle.LiveData
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.InterviewHistoryData
import com.sivaram.karkaboard.data.dto.ApplicationPortalData
import com.sivaram.karkaboard.data.dto.AppliedStudentData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StudentData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.interviewmanagement.state.AcceptState
import com.sivaram.karkaboard.ui.interviewmanagement.state.ApplicationState
import com.sivaram.karkaboard.ui.interviewmanagement.state.DeclineState

interface DatabaseRepository {
    fun getRoles(): LiveData<List<RolesData>>
    fun getCurrentUser(): LiveData<UserData?>
    suspend fun getAllBatches(): LiveData<List<BatchData>>
    suspend fun getStudentData(uid: String): LiveData<StudentData?>
    suspend fun getApplicationPortalData(studentId: String): LiveData<List<ApplicationPortalData>>
    fun getAppliedStudentDetail(batchId: String, filterId: Int): LiveData<List<AppliedStudentData>>
    suspend fun shortlistForInterview(applicationId: String, processId: Int): AcceptState
    suspend fun declineForInterview(applicationData: ApplicationData): DeclineState
    suspend fun selectedForTraining(applicationData: ApplicationData, studentDocId: String): ApplicationState
    suspend fun rejectedFromInterview(applicationData: ApplicationData): ApplicationState
    suspend fun getInterviewHistory(studentId: String): LiveData<List<InterviewHistoryData>>
}