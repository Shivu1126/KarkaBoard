package com.sivaram.karkaboard.data.remote.db

import androidx.lifecycle.LiveData
import com.sivaram.karkaboard.data.dto.RolesData
import com.sivaram.karkaboard.data.dto.UserData

interface DatabaseRepository {
    fun getRoles(): LiveData<List<RolesData>>
    fun getCurrentUser(): LiveData<UserData?>
}