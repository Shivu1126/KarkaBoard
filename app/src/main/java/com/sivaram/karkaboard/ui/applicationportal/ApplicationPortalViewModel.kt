package com.sivaram.karkaboard.ui.applicationportal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sivaram.karkaboard.data.dto.ApplicationPortalData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.applicationportal.repo.ApplicationPortalRepo
import com.sivaram.karkaboard.ui.applicationportal.state.ApplyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ApplicationPortalViewModel @Inject constructor(
    private val applicationPortalRepo: ApplicationPortalRepo,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _applicationPortalData = MutableLiveData<List<ApplicationPortalData>>(emptyList())
    val applicationPortalData: MutableLiveData<List<ApplicationPortalData>> = _applicationPortalData

    private val _applyState = MutableStateFlow<ApplyState>(ApplyState.Idle)
    val applyState: StateFlow<ApplyState> = _applyState

    fun getApplicationPortalData(studentId: String){
        viewModelScope.launch {
            databaseRepository.getApplicationPortalData(studentId).observeForever { data ->
                _applicationPortalData.value = data
            }
        }
    }

    fun applyForTraining(batchId: String, studentId: String){
        viewModelScope.launch {
            _applyState.value = ApplyState.Loading
            _applyState.value = applicationPortalRepo.applyForTraining(batchId, studentId)
        }
    }
}