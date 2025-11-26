package com.sivaram.karkaboard.ui.faculty.taskmanagement

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.TaskData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.interviewmanagement.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskManagementViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _batchData = MutableLiveData<List<BatchData>>()
    val batchData: MutableLiveData<List<BatchData>> = _batchData

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private val _taskData = MutableLiveData<List<TaskData>>()
    val taskData: MutableLiveData<List<TaskData>> = _taskData

    fun getAvailableBatches(){
        viewModelScope.launch {
            databaseRepository.getAvailableBatches().observeForever { data ->
                _batchData.value = data
            }
        }
    }

    fun getAssignedTasksByFaculty(batchId: String, userId: String){
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            databaseRepository.getAssignedTasksByFaculty(batchId, userId).observeForever { data ->
                if(data.isEmpty())
                    _uiState.value = UiState.Empty
                else{
                    _uiState.value = UiState.Success
                    _taskData.value = data
                }
            }
        }
    }
}