package com.sivaram.karkaboard.ui.managestaffs.repo

import android.content.Context
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState

interface ManageStaffRepo {
    suspend fun createStaff(userData: UserData, personalEmail: String,rolesData: RolesData, password: String, context: Context): AddStaffState
    suspend fun checkStaffExist(email: String, rolesData: RolesData, onResult: (String, Boolean) -> Unit)
//    suspend fun addStaff(staffData: UserData,email: String, password: String, mobile: String): JSONObject

}