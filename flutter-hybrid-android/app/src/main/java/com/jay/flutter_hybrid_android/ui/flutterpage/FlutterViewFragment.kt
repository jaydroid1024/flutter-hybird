package com.jay.flutter_hybrid_android.ui.flutterpage

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.jay.flutter_hybrid_android.App
import com.jay.flutter_hybrid_android.flutter.BaseFlutterViewFragment
import com.jay.flutter_hybrid_android.flutter.bridge.FlutterBridge
import com.jay.flutter_hybrid_android.flutter.bridge.FlutterBridge.Companion.TAG
import io.flutter.plugin.common.MethodChannel

class FlutterViewFragment : BaseFlutterViewFragment() {

    private lateinit var notificationsViewModel: FlutterViewViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationsViewModel =
            ViewModelProvider(this).get(FlutterViewViewModel::class.java)

        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {
            tvInfo?.text = it
        })
        tvInfo?.setOnClickListener {

            FlutterBridge.instance.fire("refresh", "我是Android端的参数", object : MethodChannel.Result {
                /**
                 * Handles a successful result.
                 *
                 * @param result The result, possibly null. The result must be an Object type supported by the
                 * codec. For instance, if you are using [StandardMessageCodec] (default), please see
                 * its documentation on what types are supported.
                 */
                override fun success(result: Any?) {
                    Log.d(TAG, "success: $result")
                    Toast.makeText(App.app, "success: $result", Toast.LENGTH_LONG).show()
                }

                /**
                 * Handles an error result.
                 *
                 * @param errorCode An error code String.
                 * @param errorMessage A human-readable error message String, possibly null.
                 * @param errorDetails Error details, possibly null. The details must be an Object type
                 * supported by the codec. For instance, if you are using [StandardMessageCodec]
                 * (default), please see its documentation on what types are supported.
                 */
                override fun error(errorCode: String?, errorMessage: String?, errorDetails: Any?) {
                    Log.d(TAG, "error: $errorMessage")

                }

                /** Handles a call to an unimplemented method.  */
                override fun notImplemented() {
                    Log.d(TAG, "notImplemented: ")
                }

            })
        }
    }
}