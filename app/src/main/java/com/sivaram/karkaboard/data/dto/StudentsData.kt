package com.sivaram.karkaboard.data.dto

data class StudentsData(
    val uId: String = "",
    val profileImgUrl: String = "",
    val name: String = "",
    val email: String = "",
    val mobile: String = "",
    val gender: String = "",
    val dob: String = "",
    val collegeName: String = "",
    val degree: String = "",
    val passingYear: Int = 0,
    val resumeUrl: String = ""
)
