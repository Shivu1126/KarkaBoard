package com.sivaram.karkaboard.ui.auth.utils

object AuthUtils {
    fun validatePassword(password: String): Boolean {
        val passwordRegex = Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$")
        return passwordRegex.matches(password)
    }
}