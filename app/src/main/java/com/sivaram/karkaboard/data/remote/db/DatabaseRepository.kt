package com.sivaram.karkaboard.data.remote.db

import androidx.lifecycle.LiveData
import com.sivaram.karkaboard.data.dto.RolesData

interface DatabaseRepository {
    fun getRoles(): LiveData<List<RolesData>>
}