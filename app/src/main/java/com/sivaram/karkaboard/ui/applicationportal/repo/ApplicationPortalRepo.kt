package com.sivaram.karkaboard.ui.applicationportal.repo

import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState

interface ApplicationPortalRepo {
    suspend fun applyForTraining(batchId: String, studentId: String): ApplyState
}