package com.sivaram.karkaboard.ui.base.state

sealed class ConnectionState {
    object Unknown : ConnectionState()
    object Connected : ConnectionState()
    object Disconnected : ConnectionState()
}