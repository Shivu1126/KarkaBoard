package com.sivaram.karkaboard.ui.managebatches.repo

import androidx.lifecycle.LiveData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.ui.managebatches.state.CreateBatchState

interface ManageBatchesRepo {
    suspend fun createBatch(batchData: BatchData): CreateBatchState
    suspend fun closeBatchPortal(batchId: String, onResult: (Boolean) -> Unit)
}