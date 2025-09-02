package com.sivaram.karkaboard.ui.base.state

sealed class NetworkState {
    object Connected : NetworkState()
    object Disconnected : NetworkState()
}