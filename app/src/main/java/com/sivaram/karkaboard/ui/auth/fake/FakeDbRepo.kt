package com.sivaram.karkaboard.ui.auth.fake

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository

class FakeDbRepo: DatabaseRepository {
    override fun getRoles(): LiveData<List<RolesData>> {
        return MutableLiveData(emptyList())
    }

    override fun getCurrentUser(): LiveData<UserData?> {
        return MutableLiveData(null)
    }

}