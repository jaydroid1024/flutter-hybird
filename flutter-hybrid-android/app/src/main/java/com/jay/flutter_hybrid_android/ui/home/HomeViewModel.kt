package com.jay.flutter_hybrid_android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "跳转到集成了FlutterView 的 FlutterHostActivity"
    }
    val text: LiveData<String> = _text
}