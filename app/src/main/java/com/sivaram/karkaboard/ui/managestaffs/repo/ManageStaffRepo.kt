package com.sivaram.karkaboard.ui.managestaffs.repo

import android.content.Context
import androidx.lifecycle.LiveData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState

interface ManageStaffRepo {
    suspend fun createStaff(userData: UserData, personalEmail: String,rolesData: RolesData, password: String, context: Context): AddStaffState
    suspend fun checkStaffExist(email: String, rolesData: RolesData, onResult: (String, Boolean) -> Unit)
//    suspend fun addStaff(staffData: UserData,email: String, password: String, mobile: String): JSONObject
    suspend fun getAllStaff(): LiveData<List<StaffData>>
    suspend fun getStaffData(uid: String): LiveData<StaffData?>
    suspend fun getStaffRole(roleId: String?): LiveData<String>
    suspend fun removeStaff(uid: String): RemoveStaffState
}