package com.sivaram.karkaboard.data.remote.db

import androidx.lifecycle.LiveData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState

interface DatabaseRepository {
    fun getRoles(): LiveData<List<RolesData>>
    fun getCurrentUser(): LiveData<UserData?>
    suspend fun getAllStaff(): LiveData<List<StaffData>>
    suspend fun getStaffData(uid: String): LiveData<StaffData?>
    suspend fun getStaffRole(roleId: String?): LiveData<String>
    suspend fun removeStaff(uid: String): RemoveStaffState
}