package com.jay.flutter_hybrid_android.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "跳转到Flutter页面"
    }
    val text: LiveData<String> = _text
}