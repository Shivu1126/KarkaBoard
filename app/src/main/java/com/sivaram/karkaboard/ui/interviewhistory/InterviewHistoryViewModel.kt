package com.sivaram.karkaboard.ui.interviewhistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.data.dto.InterviewHistoryData
import com.sivaram.karkaboard.data.remote.db.DatabaseRepository
import com.sivaram.karkaboard.ui.interviewhistory.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InterviewHistoryViewModel @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : ViewModel(){

    private val _interviewHistoryData = MutableLiveData<List<InterviewHistoryData>>()
    val interviewHistoryData: LiveData<List<InterviewHistoryData>> = _interviewHistoryData

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    fun getInterviewHistory(studentId: String){
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            databaseRepository.getInterviewHistory(studentId).observeForever { data ->
                if(data.isEmpty()){
                    _uiState.value = UiState.Empty
                }else{
                    _uiState.value = UiState.Success
                    _interviewHistoryData.value = data
                }
            }
        }
    }
}