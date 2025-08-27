package com.sivaram.karkaboard.ui.home

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sivaram.karkaboard.appconstants.NavConstants

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(navController: NavController, context: Context){
    Scaffold(
        content = {
            HomeViewContent(
                navController,
                context
            )
        }
    )
}

@Composable
fun HomeViewContent(navController: NavController, context: Context){
    Column(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.secondaryContainer),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(
            text = "Home",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight
            )
        )
        Text(
            text = FirebaseAuth.getInstance().currentUser?.email.toString(),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = MaterialTheme.typography.headlineSmall.fontWeight
            )
        )
        Text(
            text = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString(),
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                fontWeight = MaterialTheme.typography.headlineSmall.fontWeight
            )
        )
        TextButton(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(NavConstants.LOGIN){
                    popUpTo(0)
                }
            }
        ) {
            Text(
                text = "Logout",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = MaterialTheme.typography.headlineSmall.fontWeight
                )
            )
        }
    }
}