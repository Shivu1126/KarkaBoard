package com.sivaram.karkaboard.ui.auth.fake

import com.sivaram.karkaboard.ui.applicationportal.repo.ApplicationPortalRepo
import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState

class FakeApplicationPortalRepo: ApplicationPortalRepo {
    override suspend fun applyForTraining(
        batchId: String,
        studentId: String
    ): ApplyState {
        return ApplyState.Idle
    }
}