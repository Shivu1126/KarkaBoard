package com.sivaram.karkaboard.data.dto

data class ApplicationData(
    var batchId: String = "",
    var studentId: String = "",
    var processId: Int = 0,
    var docId: String = "",
    var appliedAt: Long = 0,
    var feedback: String = "",
    var performanceRating: Int = 0
)