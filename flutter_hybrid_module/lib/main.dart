import 'package:flutter/material.dart';

import 'app_default.dart';
import 'app_navigator.dart';

void main() {
  var defaultApp = MyDefaultApp();
  runApp(defaultApp);
}

@pragma('vm:entry-point')
void navigator() {
  var navigatorTestApp = MyNavigatorTestApp();
  runApp(navigatorTestApp);
}
