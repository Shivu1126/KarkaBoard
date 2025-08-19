package com.sivaram.karkaboard.appnav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.ui.HomeView
import com.sivaram.karkaboard.ui.LoginView
import com.sivaram.karkaboard.ui.RegisterView

@Composable
fun Navigation( navController: NavHostController, context: Context){
    NavHost(navController = navController, startDestination = NavConstants.DECIDER){
        composable(NavConstants.DECIDER){
            NavigationDecider(navController, context)
        }
        composable(NavConstants.HOME){
            HomeView(navController, context)
        }
        composable(NavConstants.LOGIN){
            LoginView(navController, context)
        }
        composable(NavConstants.REGISTER){
            RegisterView(navController, context)
        }
    }
}