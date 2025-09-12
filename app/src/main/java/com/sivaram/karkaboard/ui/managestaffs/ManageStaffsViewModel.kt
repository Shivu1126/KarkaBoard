package com.sivaram.karkaboard.ui.managestaffs

import androidx.lifecycle.ViewModel
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ManageStaffsViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
): ViewModel(){

}