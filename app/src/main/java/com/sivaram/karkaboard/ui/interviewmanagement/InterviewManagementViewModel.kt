package com.sivaram.karkaboard.ui.interviewmanagement

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.AppliedStudentData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.interviewmanagement.state.SelectState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewManagementViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _allBatchesData = MutableLiveData<List<BatchData>>()
    val allBatchesData: LiveData<List<BatchData>> = _allBatchesData

    private val _appliedStudentData = MutableLiveData<List<AppliedStudentData>>(emptyList())
    val appliedStudentData: MutableLiveData<List<AppliedStudentData>> = _appliedStudentData

    private val _selectState = MutableStateFlow<Map<String,SelectState>>(emptyMap())
    val selectState: StateFlow<Map<String,SelectState>> = _selectState

    fun getAllBatches(){
        viewModelScope.launch {
            databaseRepository.getAllBatches().observeForever { data ->
                _allBatchesData.value = data
            }
        }
    }

    fun getAppliedStudentDetail(batchId: String){
        viewModelScope.launch {
            databaseRepository.getAppliedStudentDetail(batchId).observeForever {
                _appliedStudentData.value = it
            }
        }
    }

    fun moveToNextProcess(applicationId: String, processId: Int){
        viewModelScope.launch {
            _selectState.update { it + (applicationId to SelectState.Loading) }
            val result = databaseRepository.moveToNextProcess(applicationId, processId)
            _selectState.update { it + (applicationId to result) }
        }
    }

    fun resetSelectState(applicationId: String){
        _selectState.update { it + (applicationId to SelectState.Idle) }
    }
}