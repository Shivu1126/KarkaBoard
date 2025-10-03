package com.sivaram.karkaboard.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.base.state.ConnectionState
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseView(
    topBar: @Composable (() -> Unit)? = null,
    darkIcons: Boolean = !isSystemInDarkTheme(),
    content: @Composable (  PaddingValues, user: UserData?) -> Unit
){
    val baseViewModel: BaseViewModel = hiltViewModel()
    val connectionState by baseViewModel.connectionState.collectAsStateWithLifecycle()

    val bgNavColor = MaterialTheme.colorScheme.secondaryContainer
    val sysUi = rememberSystemUiController()
    SideEffect {
        sysUi.setStatusBarColor(bgNavColor, darkIcons = darkIcons)
        sysUi.setNavigationBarColor(bgNavColor, darkIcons = darkIcons)
    }
    val user by baseViewModel.userData.observeAsState()

    LaunchedEffect(true) {
            baseViewModel.loadUser()

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Scaffold(
            topBar = {topBar?.invoke()},
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            containerColor = Color.Transparent,
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                content(padding, user)

                // Show bottom banner above nav bar
                ConnectionBanner(
                    connectionState = connectionState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}

@Composable
fun ConnectionBanner(
    connectionState: ConnectionState,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    var previousState by remember { mutableStateOf<ConnectionState?>(null) }
    var firstLaunchHandled by remember { mutableStateOf(false) }

    LaunchedEffect(connectionState) {
        if (!firstLaunchHandled) {
            previousState = connectionState
            firstLaunchHandled = true
            visible = connectionState is ConnectionState.Disconnected
            return@LaunchedEffect
        }

        when {
            connectionState is ConnectionState.Disconnected -> {
                visible = true // show gray
            }
            connectionState is ConnectionState.Connected &&
                    previousState is ConnectionState.Disconnected -> {
                visible = true
                delay(2000) // auto-hide after 2s
                visible = false
            }
            else -> {
                visible = false // hide in all other cases (Unknown / Connected baseline)
            }
        }
        previousState = connectionState
    }


    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier
            .fillMaxWidth()
    ) {
        val bgColor = when (connectionState) {
            is ConnectionState.Connected -> Color(0xFF4CAF50)
            else -> Color.DarkGray
        }

        val message = when (connectionState) {
            is ConnectionState.Connected -> "Connected"
            is ConnectionState.Disconnected -> "No internet connection"
            else -> "" //nothing for Unknown
        }
        if (message.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = message, color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
