package com.sivaram.karkaboard.ui.student.task.tasksubmission

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.TaskData
import com.sivaram.karkaboard.data.dto.TaskSubmissionData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.student.state.SubmitTaskState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskSubmissionViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel(){

    private val _taskData  = MutableLiveData<TaskData?>()
    val taskData: MutableLiveData<TaskData?> = _taskData

    private val _taskSubmissionData = MutableLiveData<TaskSubmissionData?>(TaskSubmissionData())
    val taskSubmissionData: MutableLiveData<TaskSubmissionData?> = _taskSubmissionData

    private val _facultyData = MutableLiveData<StaffData?>()
    val facultyData: MutableLiveData<StaffData?> = _facultyData

    private val _submitTaskState = MutableStateFlow<SubmitTaskState>(SubmitTaskState.Idle)
    val submitTaskState: MutableStateFlow<SubmitTaskState> = _submitTaskState

    fun getTaskById(taskId: String){
        viewModelScope.launch {
            databaseRepository.getTaskById(taskId).observeForever { data ->
                _taskData.value = data
            }
        }
    }

    fun getTaskSubmissionById(taskSubmissionId: String) {
        viewModelScope.launch {
            if(taskSubmissionId.isNotEmpty()) {
                databaseRepository.getTaskSubmissionById(taskSubmissionId).observeForever { data ->
                    _taskSubmissionData.value = data
                }
            }
        }
    }

    fun getFacultyById(facultyId: String) {
        viewModelScope.launch {
            databaseRepository.getFacultyById(facultyId).observeForever { data ->
                _facultyData.value = data
            }
        }
    }

    fun updateSubmissionData(taskSubmissionData: TaskSubmissionData) {
        _submitTaskState.value = SubmitTaskState.Loading
        viewModelScope.launch {
            _submitTaskState.value = databaseRepository.updateSubmissionData(taskSubmissionData)
        }
    }

    fun resetSubmitTaskState(){
        _submitTaskState.value = SubmitTaskState.Idle
    }
}