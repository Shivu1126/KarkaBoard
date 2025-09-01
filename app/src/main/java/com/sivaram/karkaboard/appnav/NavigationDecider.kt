package com.sivaram.karkaboard.appnav

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.data.local.ResetPasswordPref
import com.sivaram.karkaboard.ui.theme.KarkaBoardTheme
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavigationDecider(navController: NavController, context: Context) {
    val auth = FirebaseAuth.getInstance()
    val resetInProgress by ResetPasswordPref.isResetInProgress(context).collectAsState(initial = false)
    LaunchedEffect(true) {
        delay(3000)

        if (auth.currentUser != null) {
            Log.d("phoneBook", "user: ${auth.currentUser}")
            Log.d("phoneBook", "resetInProgress: $resetInProgress")
            if(resetInProgress){
                navController.navigate(NavConstants.RESET_PASSWORD) {
                    popUpTo(NavConstants.DECIDER) {
                        inclusive = true
                    }
                }
            }
            else {
                navController.navigate(NavConstants.HOME) {
                    popUpTo(NavConstants.DECIDER) {
                        inclusive = true
                    }
                }
            }
        } else {
            navController.navigate(NavConstants.LOGIN) {
                popUpTo(NavConstants.DECIDER) {
                    inclusive = true
                }
            }
        }
    }
    Scaffold(
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
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