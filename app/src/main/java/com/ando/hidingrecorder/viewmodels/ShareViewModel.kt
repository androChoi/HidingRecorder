package com.ando.hidingrecorder.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ando.hidingrecorder.RService

class ShareViewModel : ViewModel() {
    val serviceStatus = NonNullMutableLiveData(RService.None)
    val recording = NonNullMutableLiveData(false)
}


class NonNullMutableLiveData<T : Any>(defaultValue: T) : MutableLiveData<T>() {

    init {
        value = defaultValue
    }

    override fun getValue(): T {
        return super.getValue()!!
    }

    override fun setValue(value: T) {
        super.setValue(value)
    }
}