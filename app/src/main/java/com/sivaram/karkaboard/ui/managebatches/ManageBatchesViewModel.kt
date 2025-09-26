package com.sivaram.karkaboard.ui.managebatches

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.managebatches.repo.ManageBatchesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageBatchesViewModel @Inject constructor(
    private val manageBatchesRepo: ManageBatchesRepo,
    private val databaseRepository: DatabaseRepository
): ViewModel(){
    private val _allBatchesData = MutableLiveData<List<BatchData>>()
    val allBatchesData: LiveData<List<BatchData>> = _allBatchesData

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
}