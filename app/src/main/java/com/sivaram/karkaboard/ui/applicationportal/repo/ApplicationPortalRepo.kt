package com.sivaram.karkaboard.ui.applicationportal.repo

import androidx.lifecycle.LiveData
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState

interface ApplicationPortalRepo {
    suspend fun applyForTraining(batchId: String, studentId: String): ApplyState
    suspend fun getApplicationData(batchId: String, studentId: String): LiveData<ApplicationData?>
}