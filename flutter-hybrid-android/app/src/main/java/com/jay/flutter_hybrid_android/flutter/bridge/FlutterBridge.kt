package com.jay.flutter_hybrid_android.flutter.bridge

import android.util.Log
import android.widget.Toast
import com.jay.flutter_hybrid_android.App
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler

/**
 * Flutter和Native通信桥接
 */
class FlutterBridge private constructor() : MethodCallHandler,
    IBridge<Any?, MethodChannel.Result?> {

    //每个
    private var methodChannels = mutableListOf<MethodChannel>()

    companion object {

        const val TAG = "FlutterBridge"

        private const val FLUTTER_MESSAGE_CHANNEL = "message_channel"

        //DCL单例
        val instance: FlutterBridge by lazy { FlutterBridge() }

        @JvmStatic
        fun init(flutterEngine: FlutterEngine): FlutterBridge {
            val methodChannel = MethodChannel(flutterEngine.dartExecutor, FLUTTER_MESSAGE_CHANNEL)
            methodChannel.setMethodCallHandler(instance)
            //因多FlutterEngine后每个FlutterEngine需要单独注册一个MethodChannel，所以用集合将所有的MethodChannel保存起来
            instance.apply { methodChannels.add(methodChannel) }
            return instance
        }
    }

    /**
     * Native 向 Flutter 发送
     *
     * @param method 方法名
     * @param arguments 参数
     */
    fun fire(method: String, arguments: Any?) {
        methodChannels.forEach {
            it.invokeMethod(method, arguments)
        }
    }

    /**
     * Native 向 Flutter 发送
     *
     * @param method 方法名
     * @param arguments 参数
     * @param callback 回调
     */
    fun fire(method: String, arguments: Any?, callback: MethodChannel.Result?) {
        methodChannels.forEach {
            it.invokeMethod(method, arguments, callback)
        }
    }

    /**
     * Flutter 端返回的消息
     *
     * @param call
     * @param result
     */
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) { //处理来自Dart的方法调用
        when (call.method) {
            "onBack" -> onBack(call.arguments)
            "getHeaderParams" -> getHeaderParams(result)
            "goToNative" -> goToNative(call.arguments, result)
            else -> result.notImplemented()
        }
    }

    override fun onBack(p: Any?) {
//        if (ActivityManager.instance.getTopActivity(true) is HiFlutterActivity) {
//            (ActivityManager.instance.getTopActivity(true) as HiFlutterActivity).onBackPressed()
//        }
    }

    override fun goToNative(p: Any?, result: MethodChannel.Result) {
        if (p is Map<*, *>) {
            val action = p["action"]
            Log.d(TAG, "action:$action")
            Toast.makeText(App.app, "action:$action", Toast.LENGTH_LONG).show()
            if (action == "goToDetail") {
                val goodsId = p["goodsId"]
//                ARouter.getInstance().build("/detail/main").withString(
//                    "goodsId",
//                    goodsId as String?
//                ).navigation()
            } else if (action == "goToLogin") {
//                ARouter.getInstance().build("/account/login").navigation()
            }
        }
        result.success("Native 跳转成功了")
    }

    override fun getHeaderParams(callback: MethodChannel.Result?) {
        // boarding-pass 与 auth-token传递
        callback!!.success(
            mapOf(
                "boarding-pass" to "pass_",
                "auth-token" to "token_"
            )
        )
    }
}