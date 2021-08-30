import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';

///Flutter通信框架
class FlutterBridge {
  //名字和 Native 端一一对应
  static const String FLUTTER_MESSAGE_CHANNEL = 'message_channel';
  static FlutterBridge _instance = new FlutterBridge._();
  MethodChannel _bridge = const MethodChannel(FLUTTER_MESSAGE_CHANNEL);
  var _listeners = {};
  var header;

  //私有构造
  FlutterBridge._() {
    _bridge.setMethodCallHandler((MethodCall call) async {
      String method = call.method;
      //如果已经注册
      if (_listeners[method] != null) {
        return _listeners[method](call);
      }
    });
  }

  static FlutterBridge getInstance() {
    return _instance;
  }

  register(String method, Function(MethodCall) callBack) {
    _listeners[method] = callBack;
  }

  unRegister(String method) {
    _listeners.remove(method);
  }

  goToNative(Map prams) async {
    var message = await _bridge.invokeMethod("goToNative", prams);
    Fluttertoast.showToast(msg: "argument: ${message.toString()}");
  }

  onBack(Map prams) {
    _bridge.invokeMethod("onBack", prams);
  }

  Future<Map<String, String>> getHeaderParams() async {
    Map header = await _bridge.invokeMethod('getHeaderParams', {});
    return this.header = Map<String, String>.from(header);
  }

  MethodChannel bridge() {
    return _bridge;
  }
}
