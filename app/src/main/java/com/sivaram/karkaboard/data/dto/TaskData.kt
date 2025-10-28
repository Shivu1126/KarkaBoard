package com.sivaram.karkaboard.data.dto

data class TaskData(
    var taskId: String = "",
    var batchId: String = "",
    var facultyId: String = "",
    var title: String = "",
    var description: String = "",
    var question: List<String> = emptyList(),
    var tags: List<String> = emptyList(),
    var assignedDate: Long = 0,
    var dueDate: Long = 0,
    var totalSubmission: Int = 0
)