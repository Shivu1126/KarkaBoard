package com.sivaram.karkaboard.ui.student.task

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.StudentData
import com.sivaram.karkaboard.data.dto.TaskViewData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.student.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel(){

    private val _studentData = MutableLiveData<StudentData?>(null)
    val studentData: LiveData<StudentData?> = _studentData

    private val _taskViewData = MutableLiveData<List<TaskViewData>>()
    val taskViewData: LiveData<List<TaskViewData>> = _taskViewData

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun getStudentData(uid: String){
        viewModelScope.launch {
            databaseRepository.getStudentData(uid).observeForever {data ->
                _studentData.value = data
            }
        }
    }

    fun getTaskByBatch(studentId: String, batchId: String, status: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            databaseRepository.getTaskByBatch(studentId, batchId, status).observeForever { data ->
                Log.d("getTaskByBatch", "observe triggered")
                if (data.isEmpty()) {
                    _uiState.value = UiState.Empty
                } else {
                    _uiState.value = UiState.Success
                    _taskViewData.value = data
                }
            }
        }
    }

    fun resetUiState(){
        _uiState.value = UiState.Empty
    }
}