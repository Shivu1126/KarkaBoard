package com.sivaram.karkaboard.data.dto

data class StaffData(
    var uId: String = "",
    var profileImgUrl: String = "",
    var name: String = "",
    val email: String = "",
    val companyMail: String = "",
    val mobile: String = "",
    val countryCode: String = "",
    val gender: String = "",
    var roleId: String = ""
)