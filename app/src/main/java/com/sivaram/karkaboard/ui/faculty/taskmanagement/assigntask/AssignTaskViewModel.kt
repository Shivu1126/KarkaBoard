package com.sivaram.karkaboard.ui.faculty.taskmanagement.assigntask

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.TaskData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.faculty.taskmanagement.state.AssignTaskState
import com.sivaram.karkaboard.ui.faculty.taskmanagement.state.ValidationState
import com.sivaram.karkaboard.utils.UtilityFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignTaskViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel(){

    private val _validationState = MutableLiveData<ValidationState>(ValidationState.Idle)
    val validationState: MutableLiveData<ValidationState> = _validationState

    private val _batchData = MutableLiveData<List<BatchData>>()
    val batchData: MutableLiveData<List<BatchData>> = _batchData

    private val _assignTaskState = MutableStateFlow<AssignTaskState>(AssignTaskState.Idle)
    val assignTaskState: StateFlow<AssignTaskState> = _assignTaskState

    fun getAvailableBatches(){
        viewModelScope.launch {
            databaseRepository.getAvailableBatches().observeForever { data ->
                _batchData.value = data
            }
        }
    }

    fun assignTask(taskData: TaskData){
        viewModelScope.launch {
            _assignTaskState.value = AssignTaskState.Loading
            _assignTaskState.value = databaseRepository.assignTask(taskData)
        }
    }

    fun validateInputs(taskObj: TaskData){
        Log.d("TAG", "validateInputs: $taskObj")
        if(taskObj.title.trim().isEmpty())
        {
            Log.d("TAG", "validateInputs: ${taskObj.title}")
            _validationState.value = ValidationState.Error("Please enter task title")
        }
        else if(taskObj.description.trim().isEmpty())
            _validationState.value = ValidationState.Error("Please enter task description")
        else if(taskObj.questions.isEmpty())
            _validationState.value = ValidationState.Error("Please add one or more questions")
        else if(taskObj.tags.isEmpty())
            _validationState.value = ValidationState.Error("Please add one or more tags")
        else if(taskObj.dueDate < UtilityFunctions.getCurrentTimeInMillis())
            _validationState.value = ValidationState.Error("Please select a valid due date")
        else if(taskObj.batchId.isEmpty())
            _validationState.value = ValidationState.Error("Please select a batch")
        else
            _validationState.value = ValidationState.Success
    }

    fun resetAssignTaskState(){
        _assignTaskState.value = AssignTaskState.Idle
    }
}