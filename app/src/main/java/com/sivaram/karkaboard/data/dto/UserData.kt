package com.sivaram.karkaboard.data.dto

data class UserData(
    var uId: String = "",
    var profileImgUrl: String = "",
    var name: String = "",
    var email: String = "",
    var mobile: String = "",
    var countryCode: String = "",
    var gender: String = "",
    var dob: String = "",
    var collegeName: String = "",
    var degree: String = "",
    var passingYear: Int = 0,
    var resumeUrl: String = "",
    var isProfileCompleted: Boolean = false,
    var isDisable: Boolean = false
)
