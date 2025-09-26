package com.sivaram.karkaboard.data.dto

data class BatchData (
    var docId: String = "",
    var batchName: String = "",
    var designation: String = "",
    var description: String = "",
    var startDate: Long = 0,
    var endDate: Long = 0,
    var skills: List<String> = emptyList(),
    var createdBy: String = "", //admin id
    var createdAt: Long = 0,
    var isOpen: Boolean = true,
    var interviewLocation: String = "",
    var interviewDate: Long = 0,
    var appliedCount: Int = 0,
    var selectedCount: Int = 0,
    var rejectedCount: Int = 0,
)