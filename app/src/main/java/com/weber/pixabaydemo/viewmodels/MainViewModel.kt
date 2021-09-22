package com.weber.pixabaydemo.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class MainViewModel : ViewModel() {
    var mLoading: MutableLiveData<Int> = MutableLiveData()
}