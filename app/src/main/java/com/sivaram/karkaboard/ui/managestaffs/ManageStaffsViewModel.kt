package com.sivaram.karkaboard.ui.managestaffs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.ui.managestaffs.repo.ManageStaffRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageStaffsViewModel @Inject constructor(
    private val manageStaffRepo: ManageStaffRepo
): ViewModel(){
    private val _allStaffData = MutableLiveData<List<StaffData>>(emptyList())
    val allStaffData: LiveData<List<StaffData>> = _allStaffData

    fun getAllStaff(){
        viewModelScope.launch {
            manageStaffRepo.getAllStaff().observeForever { data ->
                _allStaffData.value = data
            }
        }
    }
}