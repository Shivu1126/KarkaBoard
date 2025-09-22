package com.sivaram.karkaboard.ui.managebatches.createnewbatch

import androidx.lifecycle.ViewModel
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateNewBatchViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
): ViewModel() {

}