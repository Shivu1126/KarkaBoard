package com.sivaram.karkaboard.ui.auth.fake

import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.ui.managebatches.repo.ManageBatchesRepo
import com.sivaram.karkaboard.ui.managebatches.state.CreateBatchState
import com.sivaram.karkaboard.ui.managebatches.state.EndBatchState

class FakeManageBatchesRepo: ManageBatchesRepo{
    override suspend fun createBatch(batchData: BatchData): CreateBatchState {
        return CreateBatchState.Idle
    }

    override suspend fun closeBatchPortal(
        batchId: String,
        onResult: (Boolean) -> Unit
    ) {
        return onResult(true)
    }
    override suspend fun endBatch(batchId: String): EndBatchState {
        return EndBatchState.Idle
    }
}