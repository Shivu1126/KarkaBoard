package com.sivaram.karkaboard.ui.managestaffs.addstaff

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.sivaram.karkaboard.appconstants.DbConstants
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.repo.ManageStaffRepo
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AddStaffViewModel @Inject constructor(
    private val manageStaffRepo: ManageStaffRepo
): ViewModel() {
    private val _addStaffState = MutableStateFlow<AddStaffState>(AddStaffState.Idle)
    var addStaffState: StateFlow<AddStaffState> = _addStaffState

    fun createStaff(staffData: UserData, email: String, password: String, context: Context) {
        viewModelScope.launch {
            _addStaffState.value = AddStaffState.Loading
            _addStaffState.value = manageStaffRepo.createStaff(staffData, email, password, context)
        }
    }

    fun checkStaffExist(email: String, domain: String, onResult: (String, Boolean) -> Unit) {
//        return try{
//            val emailDocs = firestore.collection(DbConstants.USER_TABLE)
//                .whereEqualTo("email", email)
//                .get()
//                .await()
//        }
//        catch (e: Exception){
//            onResult(e.localizedMessage ?: "Check failed", false)
//        }
    }

    fun resetAddStaffState() {
        _addStaffState.value = AddStaffState.Idle
    }
}