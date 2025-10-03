package com.sivaram.karkaboard.ui.applicationportal

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.ApplicationData
import com.sivaram.karkaboard.data.dto.ApplicationPortalData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.applicationportal.repo.ApplicationPortalRepo
import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationPortalViewModel @Inject constructor(
    private val applicationPortalRepo: ApplicationPortalRepo,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _applicationPortalData = MutableLiveData<List<ApplicationPortalData>>(emptyList())
    val applicationPortalData: MutableLiveData<List<ApplicationPortalData>> = _applicationPortalData

    private val _applyState = MutableStateFlow<Map<String,ApplyState>>(emptyMap())
    val applyState: StateFlow<Map<String,ApplyState>> = _applyState

    private val _applicationData = MutableLiveData<ApplicationData>()
    val applicationData: MutableLiveData<ApplicationData> = _applicationData

    fun getApplicationPortalData(studentId: String){
        viewModelScope.launch {
            databaseRepository.getApplicationPortalData(studentId).observeForever { data ->
                _applicationPortalData.value = data
            }
        }
    }

    fun applyForTraining(batchId: String, studentId: String){
        viewModelScope.launch {
            _applyState.update { it + (batchId to ApplyState.Loading) }
            val result = applicationPortalRepo.applyForTraining(batchId, studentId)
            _applyState.update { it + (batchId to result) }
        }
    }

    fun getApplicationData(batchId: String, studentId: String){
        viewModelScope.launch {
            applicationPortalRepo.getApplicationData(batchId, studentId).observeForever {
                _applicationData.value = it
            }
        }
    }

    fun resetApplyState(batchId: String){
        _applyState.update { it + (batchId to ApplyState.Idle) }
    }
}