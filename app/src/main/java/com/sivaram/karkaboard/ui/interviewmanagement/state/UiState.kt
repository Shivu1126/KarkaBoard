package com.sivaram.karkaboard.ui.interviewmanagement.state

sealed class UiState {
    object Empty : UiState()
    object Loading : UiState()
    object Success: UiState()
    object Error : UiState()
}