package com.jay.flutter_hybrid_android.flutter.bridge

import io.flutter.plugin.common.MethodChannel

/**
 * Flutter和Native通信桥接
 * @param P 参数
 * @param Callback 回调
 */
interface IBridge<P, Callback> {
    //返回上一页 Native & Flutter
    fun onBack(p: P?)

    //跳转到 Native 页面
    fun goToNative(p: P, result: MethodChannel.Result)

    //
    fun getHeaderParams(callback: Callback)

}