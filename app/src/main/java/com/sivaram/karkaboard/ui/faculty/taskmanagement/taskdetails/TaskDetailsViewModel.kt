package com.sivaram.karkaboard.ui.faculty.taskmanagement.taskdetails

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.SubmissionByStatus
import com.sivaram.karkaboard.data.dto.TaskData
import com.sivaram.karkaboard.data.dto.enums.SubmissionStatus
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.faculty.taskmanagement.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {
    private val _taskData  = MutableLiveData<TaskData?>()
    val taskData: MutableLiveData<TaskData?> = _taskData

    private val _batchData = MutableLiveData<BatchData?>()
    val batchData: MutableLiveData<BatchData?> = _batchData

    private val _submissionByStatus = MutableLiveData<List<SubmissionByStatus>>()
    val submissionByStatus: MutableLiveData<List<SubmissionByStatus>> = _submissionByStatus

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: MutableStateFlow<UiState> = _uiState

    fun getTaskById(taskId: String){
        viewModelScope.launch {
            databaseRepository.getTaskById(taskId).observeForever { data ->
                _taskData.value = data
            }
        }
    }

    fun getBatchDetailsById(batchId: String){
        viewModelScope.launch {
            databaseRepository.getBatchDetailsById(batchId).observeForever { data ->
                _batchData.value = data
            }
        }
    }

    fun getStudentsByTaskStatus(taskId: String, status: SubmissionStatus){
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            databaseRepository.getStudentsByTaskStatus(taskId, status).observeForever { data ->
                if(data.isEmpty()){
                    _uiState.value = UiState.Empty
                }
                else{
                    _submissionByStatus.value = data
                    _uiState.value = UiState.Success
                }
            }
        }
    }
}