package com.sivaram.karkaboard.ui.auth.register

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sivaram.karkaboard.appconstants.OtherConstants
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OtpTimerViewModel(
    private val totalTime: Long = OtherConstants.TIMER_SECONDS
): ViewModel() {
    private var timerJob: Job? = null

    private val _timeLeft = mutableLongStateOf(totalTime)
    val timeLeft: State<Long> = _timeLeft

    private val _isRunning = mutableStateOf(false)
    val isRunning: State<Boolean> = _isRunning

    fun startTimer() {
        timerJob?.cancel() // cancel old timer if running
        _timeLeft.longValue = totalTime
        _isRunning.value = true

        timerJob = viewModelScope.launch {
            while (_timeLeft.longValue > 0) {
                delay(1000)
                _timeLeft.longValue = _timeLeft.longValue - 1
            }
            _isRunning.value = false
        }
    }

    fun resetTimer() {
        startTimer()
    }
}