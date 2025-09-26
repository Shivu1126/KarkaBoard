package com.sivaram.karkaboard.ui.auth.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sivaram.karkaboard.data.dto.ApplicationPortalData
import com.sivaram.karkaboard.data.dto.BatchData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.StaffData
import com.sivaram.karkaboard.data.dto.StudentsData
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

    override suspend fun getAllBatches(): LiveData<List<BatchData>> {
        return MutableLiveData(emptyList())
    }

    override suspend fun getStudentData(uid: String): LiveData<StudentsData?> {
        return MutableLiveData()
    }

    override suspend fun getApplicationPortalData(studentId: String): LiveData<List<ApplicationPortalData>> {
        return MutableLiveData(emptyList())
    }
}