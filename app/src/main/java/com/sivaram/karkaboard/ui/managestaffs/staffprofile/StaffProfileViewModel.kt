package com.sivaram.karkaboard.ui.managestaffs.staffprofile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.ui.managestaffs.repo.ManageStaffRepo
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StaffProfileViewModel @Inject constructor(
    private val manageStaffRepository: ManageStaffRepo
): ViewModel(){

    private val _staffData = MutableLiveData<StaffData?>()
    val staffData: MutableLiveData<StaffData?> = _staffData

    private val _staffRole = MutableLiveData<String>()
    val staffRole: MutableLiveData<String> = _staffRole

    private val _removeStaffState = MutableStateFlow<RemoveStaffState>(RemoveStaffState.Idle)
    val removeStaffState: MutableStateFlow<RemoveStaffState> = _removeStaffState

    fun getStaffProfileData(staffId: String){
        viewModelScope.launch {
            manageStaffRepository.getStaffData(staffId).observeForever { data ->
                _staffData.value = data
            }
        }
    }
    fun getStaffRole(roleId: String?) {
        viewModelScope.launch {
            manageStaffRepository.getStaffRole(roleId).observeForever { role ->
                _staffRole.value = role
            }
        }
    }
    fun removeStaff(staffId: String){
        viewModelScope.launch {
            _removeStaffState.value = RemoveStaffState.Loading
            _removeStaffState.value = manageStaffRepository.removeStaff(staffId)
        }
    }

    fun resetRemoveStaffState(){
        _removeStaffState.value = RemoveStaffState.Idle
    }
}