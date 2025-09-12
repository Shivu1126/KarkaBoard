package com.sivaram.karkaboard.ui.managestaffs.addstaff

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.auth.state.ValidationState
import com.sivaram.karkaboard.ui.managestaffs.repo.ManageStaffRepo
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddStaffViewModel @Inject constructor(
    private val manageStaffRepo: ManageStaffRepo,
    private val databaseRepository: DatabaseRepository
): ViewModel() {
    private val _addStaffState = MutableStateFlow<AddStaffState>(AddStaffState.Idle)
    var addStaffState: StateFlow<AddStaffState> = _addStaffState

    private val _rolesList = MutableLiveData<List<RolesData>>(emptyList())
    val rolesList: LiveData<List<RolesData>> = _rolesList

    private val _validationState = MutableLiveData<ValidationState>()
    val validationState: LiveData<ValidationState> = _validationState

    fun createStaff(userData: UserData, personalEmail: String, rolesData: RolesData, password: String, context: Context) {
        viewModelScope.launch {
            _addStaffState.value = AddStaffState.Loading
            _addStaffState.value = manageStaffRepo.createStaff(userData, personalEmail, rolesData, password, context)
        }
    }

    fun checkStaffExist(email: String, rolesData: RolesData, onResult: (String, Boolean) -> Unit) {
        viewModelScope.launch {
            manageStaffRepo.checkStaffExist(email, rolesData, onResult)
        }
    }

    fun getRolesList(){
        viewModelScope.launch {
            databaseRepository.getRoles().observeForever { data ->
                _rolesList.value = data
            }
        }
    }

    fun validateInputs(name: String, email: String){
        if(name.isEmpty() || name.length<4){
            _validationState.value = ValidationState.Error("Please enter name with minimum 4 characters")
            return
        }
        if(email.isEmpty() || !validateEmail(email)){
            _validationState.value = ValidationState.Error("Please enter valid email")
            return
        }
        _validationState.value = ValidationState.Success
    }

    fun resetAddStaffState() {
        _addStaffState.value = AddStaffState.Idle
    }

    private fun validateEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }
}