package com.sivaram.karkaboard.ui.managebatches

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.managebatches.repo.ManageBatchesRepo
import com.sivaram.karkaboard.ui.managebatches.state.EndBatchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageBatchesViewModel @Inject constructor(
    private val manageBatchesRepo: ManageBatchesRepo,
    private val databaseRepository: DatabaseRepository
): ViewModel(){
    private val _allBatchesData = MutableLiveData<List<BatchData>>()
    val allBatchesData: LiveData<List<BatchData>> = _allBatchesData

    private val _endBatchState = MutableStateFlow<Map<String, EndBatchState>>(emptyMap())
    val endBatchState: StateFlow<Map<String, EndBatchState>> = _endBatchState

    fun getAllBatches(){
        viewModelScope.launch {
            databaseRepository.getAllBatches().observeForever { data ->
                _allBatchesData.value = data
            }
        }
    }

    fun closeBatchPortal(batchId: String, onResult: (Boolean) -> Unit){
        viewModelScope.launch {
            manageBatchesRepo.closeBatchPortal(batchId, onResult)
        }
    }

    fun endBatch(batchId: String){
        viewModelScope.launch {
            _endBatchState.update { it + (batchId to EndBatchState.Loading) }
            val result = manageBatchesRepo.endBatch(batchId)
            _endBatchState.update { it + (batchId to result) }
        }
    }

    fun resetEndBatchState(batchId: String){
        _endBatchState.update { it + (batchId to EndBatchState.Idle) }
    }
}