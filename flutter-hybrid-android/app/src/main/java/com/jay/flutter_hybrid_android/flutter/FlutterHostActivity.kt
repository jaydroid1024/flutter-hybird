package com.jay.flutter_hybrid_android.flutter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.jay.flutter_hybrid_android.MainActivity
import com.jay.flutter_hybrid_android.flutter.engine.FlutterHelper
import io.flutter.embedding.android.FlutterTextureView
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.platform.PlatformPlugin
import java.util.*

@Suppress("DEPRECATION")
class FlutterHostActivity : AppCompatActivity(), FlutterHelper.RouteCallback {

    private lateinit var flutterView: FlutterView

    private val flutterEngine: FlutterEngine = FlutterHelper.instance.getFlutterEngine()

    //记录flutter的路由栈信息
    private val routeStack: Stack<String> by lazy {
        Stack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //预先让engine进入resume状态，防止白屏过渡页
        flutterEngine.lifecycleChannel.appIsResumed()


        val route = intent.getStringExtra("route")
        Log.d("FlutterHostActivity", "route = $route")
        FlutterHelper.instance.let {
            it.addRouteCallback(this)
            it.sendMessage(SendMessage("$route"))
        }

        super.onCreate(savedInstanceState)
        configureStatusBarForFullscreenFlutterExperience()
        createFlutterView(flutterEngine)
        setContentView(flutterView)
    }

    private fun createFlutterView(engine: FlutterEngine): FlutterView {
        val flutterTextureView = FlutterTextureView(this)
        flutterView = FlutterView(applicationContext, flutterTextureView)
        flutterView.attachToFlutterEngine(engine)
        return flutterView
    }

    override fun onResume() {
        super.onResume()
        flutterEngine.lifecycleChannel.appIsResumed()
    }

    override fun onPause() {
        super.onPause()
        flutterEngine.lifecycleChannel.appIsPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        flutterView.detachFromFlutterEngine()
    }

    private fun configureStatusBarForFullscreenFlutterExperience() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = 0x00000000
        window.decorView.systemUiVisibility = PlatformPlugin.DEFAULT_SYSTEM_UI
    }

    override fun onBackPressed() {
        if (routeStack.size <= 1) {
            super.onBackPressed()
            flutterEngine.navigationChannel.popRoute()
        } else {
            flutterEngine.navigationChannel.popRoute()
        }
    }

    override fun onPush(route: String) {
        routeStack.push(route)
    }

    override fun onPop(route: String) {
        val peek = routeStack.peek()
        if (peek.equals(route)) {
            routeStack.pop()
        } else {
            throw Exception("出栈的Flutter在Native的缓存中不在栈顶，或存在跨域问题！")
        }

        if (routeStack.isEmpty() && !isFinishing) {
            finish()
        }
    }

    override fun onReplace(newRoute: String, oldRoute: String) {
        if (oldRoute == routeStack.peek()) {
            routeStack.pop()
            routeStack.push(newRoute)
        } else {
            throw Exception("出栈的Flutter在Native的缓存中不在栈顶，或存在跨域问题！")
        }
    }

    override fun onRemove(route: String) {
        // TODO("暂时没用到该方法")
    }

    override fun routeNative(nativeRoute: String, params: HashMap<String, Any>?) {
        //这里本来是要根据路由打开activity的，我就随便演示一下啦
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun getLifecycleOwner(): LifecycleOwner {
        return this
    }
}