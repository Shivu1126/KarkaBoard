package com.sivaram.karkaboard.ui.auth.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.managestaffs.state.RemoveStaffState

class FakeDbRepo: DatabaseRepository {
    override fun getRoles(): LiveData<List<RolesData>> {
        return MutableLiveData(emptyList())
    }

    override fun getCurrentUser(): LiveData<UserData?> {
        return MutableLiveData(null)
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