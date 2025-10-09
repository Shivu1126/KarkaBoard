package com.sivaram.karkaboard.ui.interviewmanagement

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.AppliedStudentData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.interviewmanagement.state.AcceptState
import com.sivaram.karkaboard.ui.interviewmanagement.state.ApplicationState
import com.sivaram.karkaboard.ui.interviewmanagement.state.DeclineState
import com.sivaram.karkaboard.ui.interviewmanagement.state.UiState
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
    val appliedStudentData: LiveData<List<AppliedStudentData>> = _appliedStudentData

    private val _acceptState = MutableStateFlow<Map<String,AcceptState>>(emptyMap())
    val acceptState: StateFlow<Map<String,AcceptState>> = _acceptState

    private val _declineState = MutableStateFlow<Map<String,DeclineState>>(emptyMap())
    val declineState: StateFlow<Map<String,DeclineState>> = _declineState

    private val _applicationState = MutableStateFlow<Map<String,ApplicationState>>(emptyMap())
    val applicationState: StateFlow<Map<String,ApplicationState>> = _applicationState

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    private var currentObserver: Observer<List<AppliedStudentData>>? = null
    private var currentLiveData: LiveData<List<AppliedStudentData>>? = null

    fun getAllBatches(){
        viewModelScope.launch {
            databaseRepository.getAllBatches().observeForever { data ->
                _allBatchesData.value = data
            }
        }
    }

    fun getAppliedStudentDetail(batchId: String, filterId: Int){
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            Log.d("UiState: ", "Loading triggered")
            //Remove old observer before creating new one
            currentObserver?.let { observer ->
                currentLiveData?.removeObserver(observer)
            }

            //Get new LiveData from repository
            val liveData = databaseRepository.getAppliedStudentDetail(batchId, filterId)
            currentLiveData = liveData

            //Create and save new observer
            currentObserver = Observer { data ->
                if(data.isEmpty()){
                    Log.d("UiState: ", "Empty triggered")
                    _uiState.value = UiState.Empty
                }else{
                    Log.d("UiState: ", "Success triggered with ${data.size} items")
                    _uiState.value = UiState.Success
                }
                _appliedStudentData.value = data

            }

            //Register the observer
            liveData.observeForever(currentObserver!!)
        }
    }

    fun shortlistForInterview(applicationId: String, processId: Int){
        viewModelScope.launch {
            _acceptState.update { it + (applicationId to AcceptState.Loading) }
            val result = databaseRepository.shortlistForInterview(applicationId, processId)
            _acceptState.update { it + (applicationId to result) }
        }
    }

    fun declineCandidateForInterview(applicationData: ApplicationData){
        viewModelScope.launch {
            _declineState.update { it + (applicationData.docId to DeclineState.Loading) }
            val result = databaseRepository.declineForInterview(applicationData)
            _declineState.update { it + (applicationData.docId to result) }
        }
    }

    fun selectedForTraining(applicationData: ApplicationData, studentDocId: String){
        viewModelScope.launch {
            _applicationState.update { it + (applicationData.docId to ApplicationState.Loading) }
            val result = databaseRepository.selectedForTraining(applicationData, studentDocId)
            _applicationState.update { it + (applicationData.docId to result) }
        }
    }

    fun rejectedFromInterview(applicationData: ApplicationData){
        viewModelScope.launch {
            _applicationState.update { it + (applicationData.docId to ApplicationState.Loading) }
            val result = databaseRepository.rejectedFromInterview(applicationData)
            _applicationState.update { it + (applicationData.docId to result) }
        }
    }

    fun resetAcceptState(applicationId: String){
        _acceptState.update { it + (applicationId to AcceptState.Idle) }
    }

    fun resetDeclineState(applicationId: String){
        _declineState.update { it + (applicationId to DeclineState.Idle) }
    }

    fun resetApplicationState(applicationId: String){
        _applicationState.update { it + (applicationId to ApplicationState.Idle) }
    }
    //Clean up when ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        currentObserver?.let { observer ->
            currentLiveData?.removeObserver(observer)
        }
    }
}