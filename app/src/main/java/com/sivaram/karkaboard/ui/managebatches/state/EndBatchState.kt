package com.sivaram.karkaboard.ui.managebatches.state

sealed class EndBatchState {
    object Idle: EndBatchState()
    object Loading: EndBatchState()
    data class Success(val message: String): EndBatchState()
    data class Error(val message: String): EndBatchState()
}