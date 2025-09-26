package com.sivaram.karkaboard.ui.managebatches.state

sealed class CreateBatchState {
    object Idle: CreateBatchState()
    object Loading: CreateBatchState()
    data class Success(val message: String): CreateBatchState()
    data class Error(val message: String): CreateBatchState()
}