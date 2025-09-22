package com.sivaram.karkaboard.data.dto

data class BatchData (
    var docId: String = "",
    var batchName: String = "",
    var description: String = "",
    var startDate: Long = 0,
    var endDate: Long = 0,
    var skills: List<String> = emptyList(),
    var createdBy: String = "", //admin id
    var isOpen: Boolean = true,
    var interviewLocation: String = "",
    var locationLink: String = "",
    var interviewDate: String = "",
)