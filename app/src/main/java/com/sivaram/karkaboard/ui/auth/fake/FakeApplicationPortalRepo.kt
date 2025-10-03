package com.sivaram.karkaboard.ui.auth.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.ui.applicationportal.repo.ApplicationPortalRepo
import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState

class FakeApplicationPortalRepo: ApplicationPortalRepo {
    override suspend fun applyForTraining(
        batchId: String,
        studentId: String
    ): ApplyState {
        return ApplyState.Idle
    }

    override suspend fun getApplicationData(
        batchId: String,
        studentId: String
    ): LiveData<ApplicationData> {
        return MutableLiveData()
    }
}