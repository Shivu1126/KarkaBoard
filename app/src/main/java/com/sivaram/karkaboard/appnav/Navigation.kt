package com.sivaram.karkaboard.appnav

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sivaram.karkaboard.appconstants.NavConstants
import com.sivaram.karkaboard.ui.auth.forgetpassword.ResetPasswordView
import com.sivaram.karkaboard.ui.home.HomeView
import com.sivaram.karkaboard.ui.auth.login.LoginView
import com.sivaram.karkaboard.ui.auth.register.RegisterView
import com.sivaram.karkaboard.ui.base.BaseView

@Composable
fun Navigation( navController: NavHostController, context: Context){
    NavHost(navController = navController, startDestination = NavConstants.DECIDER){
        composable(NavConstants.DECIDER){
            BaseView(
                topBar = null
            ) { innerPadding ->
                NavigationDecider(navController, context)
            }
        }
        composable(NavConstants.HOME){
            HomeView(navController, context)
        }
        composable(NavConstants.LOGIN){
            BaseView(
                topBar = null
            ) { innerPadding ->
                LoginView(navController, context)
            }
        }
        composable(NavConstants.REGISTER){
            BaseView(
                topBar = null
            ) { innerPadding ->
                RegisterView(navController, context)
            }
        }
        composable(NavConstants.RESET_PASSWORD){
            BaseView(
                topBar = null
            ) { innerPadding ->
                ResetPasswordView(navController, context)
            }
        }
    }
}