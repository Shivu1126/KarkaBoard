package com.sivaram.karkaboard.ui.managebatches.createnewbatch

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.auth.state.ValidationState
import com.sivaram.karkaboard.ui.managebatches.repo.ManageBatchesRepo
import com.sivaram.karkaboard.ui.managebatches.state.CreateBatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNewBatchViewModel @Inject constructor(
    private val manageBatchesRepository: ManageBatchesRepo,
    private val databaseRepository: DatabaseRepository
) : ViewModel() {

    private val _createBatchState = MutableStateFlow<CreateBatchState>(CreateBatchState.Idle)
    val createBatchState: StateFlow<CreateBatchState> = _createBatchState

    private val _validationState = MutableLiveData<ValidationState>()
    val validationState: MutableLiveData<ValidationState> = _validationState

    private val _allBatchesData = MutableLiveData<List<BatchData>>(emptyList())
    val allBatchesData: MutableLiveData<List<BatchData>> = _allBatchesData

    fun createNewBatch(batchData: BatchData) {
        viewModelScope.launch {
            _createBatchState.value = CreateBatchState.Loading
            _createBatchState.value = manageBatchesRepository.createBatch(batchData)
        }
    }

    fun validateInputs(batchData: BatchData, batchList: List<BatchData>) {
        Log.d("CreateNewBatchViewModel", "validateInputs: $batchData")
        if (batchData.batchName.trim().isEmpty()) {
            _validationState.value = ValidationState.Error("Please enter batch name")
        } else if(checkBatchName(batchData.batchName.trim(), batchList)){
            _validationState.value = ValidationState.Error("Batch name already exists")
        } else if(batchData.designation.trim().isEmpty()){
            _validationState.value = ValidationState.Error("Please enter designation")
        } else if (batchData.description.trim().isEmpty()) {
            _validationState.value = ValidationState.Error("Please enter description")
        } else if (batchData.startDate == 0L || batchData.endDate == 0L) {
            _validationState.value = ValidationState.Error("Please select batch duration date")
        } else if (batchData.skills.isEmpty()) {
            _validationState.value = ValidationState.Error("Please add one or more skills")
        } else if (batchData.interviewLocation.isEmpty()) {
            _validationState.value = ValidationState.Error("Please enter interview location")
        } else if (batchData.interviewDate == 0L) {
            _validationState.value = ValidationState.Error("Please select interview date")
        } else {
            _validationState.value = ValidationState.Success
        }
    }

    fun getAllBatches() {
        Log.d("getAllBatches","Triggered")
        viewModelScope.launch {
            databaseRepository.getAllBatches().observeForever { data ->
                Log.d("getAllBatches","Triggered2 -> $data")
                _allBatchesData.value = data
            }
        }
    }

    private fun checkBatchName(batchName: String, batchList: List<BatchData>): Boolean {
        return batchList.any { it.batchName == batchName }
    }

    fun resetCreateBatchState() {
        _createBatchState.value = CreateBatchState.Idle
    }
}