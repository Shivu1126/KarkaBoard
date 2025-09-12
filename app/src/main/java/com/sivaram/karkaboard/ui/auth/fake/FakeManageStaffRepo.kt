package com.sivaram.karkaboard.ui.auth.fake

import android.content.Context
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.ui.managestaffs.repo.ManageStaffRepo
import com.sivaram.karkaboard.ui.managestaffs.state.AddStaffState

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
}