package com.sivaram.karkaboard.ui.faculty.taskmanagement.taskdetails

import androidx.lifecycle.ViewModel
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

}