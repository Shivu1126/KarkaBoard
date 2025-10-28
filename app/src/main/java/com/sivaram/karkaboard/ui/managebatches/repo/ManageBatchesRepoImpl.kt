package com.sivaram.karkaboard.ui.managebatches.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.ui.managebatches.state.CreateBatchState
import com.sivaram.karkaboard.ui.managebatches.state.EndBatchState
import kotlinx.coroutines.tasks.await

class ManageBatchesRepoImpl : ManageBatchesRepo {

    private val firebaseFireStore = FirebaseFirestore.getInstance()

    override suspend fun createBatch(batchData: BatchData): CreateBatchState {
        Log.d("ManageBatchesRepoImpl", "createBatch1 ->$batchData")
        return try{
            batchData.createdAt = System.currentTimeMillis()
            val batchDoc = firebaseFireStore.collection(DbConstants.BATCHES_TABLE).document()
            batchData.docId = batchDoc.id
            batchDoc.set(batchData).await()
            CreateBatchState.Success("Batch created successfully")
        }catch (e: Exception){
            CreateBatchState.Error(e.localizedMessage ?: "Check failed")
        }
//        return CreateBatchState.Success("Batch created successfully")
    }

    override suspend fun closeBatchPortal(
        batchId: String,
        onResult: (Boolean) -> Unit
    ) {
        return try {
            firebaseFireStore.collection(DbConstants.BATCHES_TABLE).document(batchId)
                .update("open", false).await()
            onResult(true)
        }catch (e: Exception){
            onResult(false)
        }
    }

    override suspend fun endBatch(batchId: String): EndBatchState {
        return try{
            firebaseFireStore.collection(DbConstants.BATCHES_TABLE).document(batchId)
                .update("end", true).await()
            EndBatchState.Success("Batch ended successfully")
        }catch (e: Exception){
            EndBatchState.Error("Something went wrong")
        }
    }
}