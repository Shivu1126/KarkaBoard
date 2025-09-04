package com.sivaram.karkaboard.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sivaram.karkaboard.data.dto.UserData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
): ViewModel()
{
    private val _userData = MutableLiveData<UserData>()
    val userData: LiveData<UserData> = _userData

    fun getCurrentUser() {
        databaseRepository.getCurrentUser().observeForever {
            _userData.value = it
        }
    }
}