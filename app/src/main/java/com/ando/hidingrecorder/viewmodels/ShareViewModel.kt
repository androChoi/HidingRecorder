package com.ando.hidingrecorder.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ando.hidingrecorder.RecordState
import kotlinx.coroutines.flow.MutableStateFlow

class ShareViewModel : ViewModel() {
    val serviceStatus = MutableStateFlow(RecordState.None)
    val recording = MutableStateFlow(false)

}