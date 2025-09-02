package com.sivaram.karkaboard.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.ui.base.state.ConnectionState
import com.sivaram.karkaboard.ui.base.state.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(
    connectivityService: NetworkConnectivityService
) :ViewModel() {
    val connectionState: StateFlow<ConnectionState> =
        connectivityService.networkStatus
            .map { status ->
                when (status) {
                    NetworkState.Connected -> ConnectionState.Connected
                    NetworkState.Disconnected -> ConnectionState.Disconnected
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConnectionState.Unknown)
}