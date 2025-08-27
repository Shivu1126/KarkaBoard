package com.sivaram.karkaboard.data.remote.state

sealed class DbState {
    object Idle : DbState()
    object Loading : DbState()
    data class Success(val message: String) : DbState()
    data class Error(val message: String) : DbState()
}