package com.sivaram.karkaboard.data.dto

import com.sivaram.karkaboard.data.dto.enums.SubmissionStatus

data class TaskSubmissionData(
    var submissionId: String = "",
    var taskId: String = "",
    var studentId: String = "",
    var gitLink: String = "",
    var fileUrls: List<String> = emptyList(),
    var submittedDate: Long = 0,
    var facultyReview: String = "",
    var reviewRating: Int = 0,
    var reviewedDate: Long = 0,
    var status: SubmissionStatus = SubmissionStatus.PENDING,
)