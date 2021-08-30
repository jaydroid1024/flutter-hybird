package com.jay.flutter_hybrid_android.flutter.engine

import android.content.Context
import android.os.Looper
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.jay.flutter_hybrid_android.flutter.ReceivedMessage
import com.jay.flutter_hybrid_android.flutter.SendMessage
import com.jay.flutter_hybrid_android.flutter.bridge.FlutterBridge
import com.jay.flutter_hybrid_android.utils.GsonUtils
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.BasicMessageChannel
import java.util.*

open class FlutterHelper private constructor() {

    private fun readResolve(): Any {
        //防止单例对象在反序列化时重新生成对象
        return instance
    }

    companion object {

        //FlutterEngine缓存的key
        const val FLUTTER_ENGINE = "flutter_engine"

        //BasicMessageChannel的key
        const val FLUTTER_MESSAGE_CHANNEL = "message_channel"

        //flutter初始化成功的消息
        private const val FLUTTER_ENGINE_INIT_FINISH = "flutter_engine_init_finish"

        //DCL单例
        val instance: FlutterHelper by lazy { FlutterHelper() }

    }

    private var messageChannel: BasicMessageChannel<String>? = null

    // 初始化状态回调
    private var initCallback: ((status: Int) -> Unit)? = null

    private val routeCallbackStack: Stack<RouteCallback> by lazy { Stack() }

    // basicMessageChannel消息处理器
    private val messageHandler = BasicMessageChannel.MessageHandler<String> { message, reply ->
        if (FLUTTER_ENGINE_INIT_FINISH == message) {
            initCallback?.run { this(2) }
        }
        //处理其他交互信息
        else {
            try {
                var received = GsonUtils.fromJson(message, ReceivedMessage::class.java)
                received?.let { parseMessage(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 预加载时利用空闲线程，不损失首页性能
     *
     * @param context 上下文
     */
    fun preloadFlutterEngine(context: Context) {
        Looper.myQueue().addIdleHandler {
            initFlutterEngine(context)
            true
        }
    }

    /**
     * 初始化FlutterEngine
     * @param context 上下文
     */
    @Synchronized
    fun initFlutterEngine(context: Context): FlutterEngine {
        if (!FlutterEngineCache.getInstance().contains(FLUTTER_ENGINE)) {

            // Instantiate a FlutterEngine.
            val engine = FlutterEngine(context.applicationContext)
            // Configure an initial route.
//            engine.navigationChannel.setInitialRoute("your/route/here")

            //Channel 注册要紧跟引擎初始化之后，否则会有在dart中调用 Channel 因为还未初始化完成而导致的时序问题
            FlutterBridge.init(engine)

            // Start executing Dart code to pre-warm the FlutterEngine.
            engine.dartExecutor.executeDartEntrypoint(
                DartExecutor.DartEntrypoint.createDefault()
            )
            // Cache the FlutterEngine to be used by FlutterActivity or FlutterFragment.
            FlutterEngineCache.getInstance().put(FLUTTER_ENGINE, engine)
            return engine
        } else {
            return getFlutterEngine()
        }
    }

    fun isInitFinish(): Boolean {
        return FlutterEngineCache.getInstance().get(FLUTTER_ENGINE) != null
    }

    fun getFlutterEngine(): FlutterEngine {
        return if (isInitFinish()) FlutterEngineCache.getInstance().get(FLUTTER_ENGINE)!!
        else throw Exception("请先初始化 FlutterEngine！")
    }

    fun getFlutterEngine(context: Context): FlutterEngine {
        return if (isInitFinish()) FlutterEngineCache.getInstance().get(FLUTTER_ENGINE)!!
        else initFlutterEngine(context)
    }


    fun release() {
        FlutterEngineCache.getInstance().get(FLUTTER_ENGINE)?.run { destroy() }
        FlutterEngineCache.getInstance().remove(FLUTTER_ENGINE)
        messageChannel = null
        routeCallbackStack.clear()
    }

    fun sendMessage(message: SendMessage) {
        messageChannel?.run { send(GsonUtils.toJson(message)) }
    }


    //添加route监听
    fun addRouteCallback(callback: RouteCallback) {
        routeCallbackStack.push(callback)
        //onDestroy时移除监听
        callback.getLifecycleOwner().lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                routeCallbackStack.pop()
            }
        })
    }

    private fun parseMessage(message: ReceivedMessage) {
        if (routeCallbackStack.isEmpty()) {
            return
        }

        val peek = routeCallbackStack.peek()
        when (message.operateType) {
            //打开native
            0 -> {
                val nativeRoute = message.nativeRoute!!
                peek.routeNative(nativeRoute, message.nativeParams)
            }
            //Flutter栈更新
            1 -> {
                when (message.flutterType) {
                    //入栈
                    0 -> {
                        peek.onPush(message.flutterCurrentRoute!!)
                    }
                    //出栈
                    1 -> {
                        peek.onPop(message.flutterCurrentRoute!!)
                    }
                    //替换
                    2 -> {
                        peek.onReplace(message.flutterCurrentRoute!!, message.flutterPreRoute!!)
                    }
                    //移除
                    3 -> {

                    }
                }
            }
        }
    }

    /**
     * flutter路由变化回调
     */
    interface RouteCallback {

        fun onPush(route: String)

        fun onPop(route: String)

        fun onReplace(newRoute: String, oldRoute: String)

        fun onRemove(route: String)

        fun routeNative(nativeRoute: String, params: HashMap<String, Any>? = null)

        fun getLifecycleOwner(): LifecycleOwner
    }

}