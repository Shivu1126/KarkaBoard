package com.sivaram.karkaboard.data.dto

data class StaffData(
    var uId: String = "",
    var profileImgUrl: String = "",
    var name: String = "",
    var email: String = "",
    var companyMail: String = "",
    var mobile: String = "",
    var countryCode: String = "",
    var gender: String = "",
    var roleId: String = "",
    var isDisable: Boolean = false
)