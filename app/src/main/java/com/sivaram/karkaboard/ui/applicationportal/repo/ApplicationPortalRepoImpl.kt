package com.sivaram.karkaboard.ui.applicationportal.repo

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState
import kotlinx.coroutines.tasks.await

class ApplicationPortalRepoImpl: ApplicationPortalRepo {

    private val firebaseFireStore = FirebaseFirestore.getInstance()

    override suspend fun applyForTraining(batchId: String, studentId: String): ApplyState {
        Log.d("LogData","applyForTraining() -> $batchId $studentId")
        return try {
            val applicationDoc = firebaseFireStore.collection(DbConstants.APPLICATION_TABLE).document()
            val applicationData = ApplicationData(
                batchId = batchId,
                studentId = studentId,
                processId = 1
            )
            applicationDoc.set(applicationData).await()
            ApplyState.Success("Applied Successfully")
        }
        catch (e: Exception){
            ApplyState.Error(e.localizedMessage ?: "Check failed")
        }
    }
}