package com.jay.flutter_hybrid_android

import android.app.Application
import com.jay.flutter_hybrid_android.flutter.engine.FlutterHelper

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/8/28
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        app = this
        //预加载 Flutter Engine
        FlutterHelper.instance.preloadFlutterEngine(this)
    }

    companion object {
        var app: Application? = null
    }

}
