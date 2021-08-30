package com.jay.flutter_hybrid_android.ui.flutterpage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FlutterViewViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "向 Flutter 端发送消息"
    }
    val text: LiveData<String> = _text
}