package com.sivaram.karkaboard.ui.managestaffs.repo

import android.content.Context
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState
import org.json.JSONObject

interface ManageStaffRepo {
    suspend fun createStaff(staffData: UserData, email: String, password: String, context: Context): AddStaffState
    suspend fun checkStaffExist(email: String, domain: String, onResult: (String, Boolean) -> Unit)
//    suspend fun addStaff(staffData: UserData,email: String, password: String, mobile: String): JSONObject
}