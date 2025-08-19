package com.sivaram.karkaboard.appnav

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import kotlinx.coroutines.delay

@Composable
fun NavigationDecider(navController: NavController, context: Context){
    LaunchedEffect(true) {
        delay(3000)
        navController.navigate(NavConstants.LOGIN){
            popUpTo(NavConstants.DECIDER){
                inclusive = true
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer
            ),
        contentAlignment = Alignment.Center,
        content = {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    )
}

@Preview
@Composable
fun NavigationDeciderPreview() {
    KarkaBoardTheme {
        NavigationDecider(
            navController = NavController(LocalContext.current),
            context = LocalContext.current
        )
    }
}