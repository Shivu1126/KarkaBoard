package com.sivaram.karkaboard.ui.applicationportal.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState
import kotlinx.coroutines.tasks.await

class ApplicationPortalRepoImpl: ApplicationPortalRepo {

    private val firebaseFireStore = FirebaseFirestore.getInstance()

    override suspend fun applyForTraining(batchId: String, studentId: String): ApplyState {
        Log.d("LogData","applyForTraining() -> $batchId $studentId")
        return try {
            val studentData = firebaseFireStore.collection(DbConstants.APPLICATION_TABLE).whereEqualTo("studentId",studentId)
                .get().await().toObjects(ApplicationData::class.java)
            for (data in studentData){
                if(data.processId>=1 && data.processId<=3){
                    return ApplyState.Success("You are already in process.", false)
                }
            }
            val applicationDoc = firebaseFireStore.collection(DbConstants.APPLICATION_TABLE).document()
            val docId = applicationDoc.id
            val applicationData = ApplicationData(
                batchId = batchId,
                studentId = studentId,
                processId = 1,
                docId = docId,
                appliedAt = System.currentTimeMillis(),
                feedback = "",
                performanceRating = 0
            )
            applicationDoc.set(applicationData).await()
            val batchObj = firebaseFireStore.collection(DbConstants.BATCHES_TABLE).document(batchId)
                            .get().await().toObject(BatchData::class.java)
            firebaseFireStore.collection(DbConstants.BATCHES_TABLE).document(batchId).update(
                "appliedCount", batchObj?.appliedCount?.plus(1)
            ).await()
            ApplyState.Success("Applied Successfully.", true)
        }
        catch (e: Exception){
            ApplyState.Error(e.localizedMessage ?: "Check failed")
        }
    }

    override suspend fun getApplicationData(
        batchId: String,
        studentId: String
    ): LiveData<ApplicationData?> {
        val applicationData = MutableLiveData<ApplicationData?>()
        try{
            firebaseFireStore.collection(DbConstants.APPLICATION_TABLE)
                .addSnapshotListener {
                        snapshot, error ->
                    if (error != null) {
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val result = snapshot.documents.mapNotNull{
                                if(it.get("batchId") == batchId && it.get("studentId") == studentId){
                                    it.toObject(ApplicationData::class.java)
                                }
                                else{
                                    null
                                }
                             }
                        if(result.isNotEmpty()) {
                            applicationData.value = result[0]
                        }
                    }
                }
        }
        catch (e: Exception){
            applicationData.value = null
        }
        Log.d("LogData","getApplicationData() -> ${applicationData.value}")
        return applicationData
    }
}