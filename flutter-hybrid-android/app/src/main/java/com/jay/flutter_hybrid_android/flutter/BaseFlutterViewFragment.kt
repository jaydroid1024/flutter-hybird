package com.jay.flutter_hybrid_android.flutter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jay.flutter_hybrid_android.App
import com.jay.flutter_hybrid_android.databinding.FragmentFlutterViewBinding
import com.jay.flutter_hybrid_android.flutter.engine.FlutterHelper
import io.flutter.embedding.android.FlutterTextureView
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine

/**
 * @author jaydroid
 * @version 1.0
 * @date 2021/8/30
 */
abstract class BaseFlutterViewFragment : Fragment() {

    private val flutterEngine: FlutterEngine? =
        App.app?.let { FlutterHelper.instance.getFlutterEngine(it) }
    protected var flutterView: FlutterView? = null
    private val cached = FlutterHelper.instance.isInitFinish()
    private var _binding: FragmentFlutterViewBinding? = null
    var tvInfo: TextView? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 注册flutter/platform_views 插件以便能够处理native view
        if (!cached) {
            flutterEngine?.platformViewsController?.attach(
                activity,
                flutterEngine.renderer,
                flutterEngine.dartExecutor
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlutterViewBinding.inflate(inflater, container, false)
        val root: ViewGroup = binding.root
        tvInfo = binding.tvInfo
        //添加 FlutterView
        root.addView(createFlutterView(requireContext()))
        return root
    }


    private fun createFlutterView(context: Context): FlutterView {
        //使用FlutterTextureView来进行渲染，以规避FlutterSurfaceView压后台回来后界面被复用的问题
        val flutterTextureView = FlutterTextureView(requireActivity())
        flutterView = FlutterView(context, flutterTextureView)
        return flutterView!!
    }

    override fun onStart() {
        flutterEngine?.let { flutterView?.attachToFlutterEngine(it) }
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        //for flutter >= v1.17
        flutterEngine?.lifecycleChannel?.appIsResumed()
    }

    override fun onPause() {
        super.onPause()
        flutterEngine?.lifecycleChannel?.appIsInactive()
    }

    override fun onStop() {
        super.onStop()
        flutterEngine?.lifecycleChannel?.appIsPaused()
    }

    override fun onDetach() {
        super.onDetach()
        flutterEngine?.lifecycleChannel?.appIsDetached()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        flutterView?.detachFromFlutterEngine()
    }

}