package com.sivaram.karkaboard.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.base.state.ConnectionState
import com.sivaram.karkaboard.ui.base.state.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BaseViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val connectivityService: NetworkConnectivityService
) :ViewModel() {

    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData

    fun loadUser() {
        databaseRepository.getCurrentUser().observeForever {
            _userData.value = it
        }
    }

    fun loadUserRole() {
        viewModelScope.launch {

        }
    }

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