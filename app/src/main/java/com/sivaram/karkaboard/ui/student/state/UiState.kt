package com.sivaram.karkaboard.ui.student.state

sealed class UiState {
    object Empty : UiState()
    object Loading : UiState()
    object Success: UiState()
    object Error : UiState()
}