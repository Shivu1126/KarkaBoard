package com.sivaram.karkaboard.data.remote.db

import androidx.lifecycle.LiveData
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.ApplicationPortalData
import com.sivaram.karkaboard.data.dto.AppliedStudentData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.StudentsData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.interviewmanagement.state.SelectState
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState

interface DatabaseRepository {
    fun getRoles(): LiveData<List<RolesData>>
    fun getCurrentUser(): LiveData<UserData?>
    suspend fun getAllBatches(): LiveData<List<BatchData>>
    suspend fun getStudentData(uid: String): LiveData<StudentsData?>
    suspend fun getApplicationPortalData(studentId: String): LiveData<List<ApplicationPortalData>>
    suspend fun getAppliedStudentDetail(batchId: String): LiveData<List<AppliedStudentData>>
    suspend fun moveToNextProcess(applicationId: String, processId: Int): SelectState
}