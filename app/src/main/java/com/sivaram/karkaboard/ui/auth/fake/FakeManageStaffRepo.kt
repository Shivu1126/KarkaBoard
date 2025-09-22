package com.sivaram.karkaboard.ui.auth.fake

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.repo.ManageStaffRepo
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState

class FakeManageStaffRepo: ManageStaffRepo  {

    override suspend fun createStaff(
        staffData: UserData,
        email: String,
        rolesData: RolesData,
        password: String,
        context: Context
    ): AddStaffState {
        return AddStaffState.Idle
    }

    override suspend fun checkStaffExist(
        email: String,
        rolesData: RolesData,
        onResult: (String, Boolean) -> Unit
    ) {
        onResult("",false)
    }
    override suspend fun getAllStaff(): LiveData<List<StaffData>> {
        return MutableLiveData(emptyList())
    }

    override suspend fun getStaffData(uid: String): LiveData<StaffData?> {
        return MutableLiveData(StaffData())
    }

    override suspend fun getStaffRole(roleId: String?): LiveData<String> {
        return MutableLiveData("")
    }

    override suspend fun removeStaff(uid: String): RemoveStaffState {
        return RemoveStaffState.Success("Removed Successfully")
    }
}