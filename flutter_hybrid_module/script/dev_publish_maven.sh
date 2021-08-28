#!/usr/bin/env sh

flutter pub get
cd ..
./gradlew build

cd .android

./gradlew \
  -I=../script/flutter_aar_upload.gradle \
  -Pmaven-url=https://packages.aliyun.com/maven/repository/2129792-snapshot-ijqMYZ/ \
  -Pmaven-user=611c80bd7920cba1bd4cc0e4 \
  -Pmaven-pwd=sjWP3g-qaQll \
  -Pis-plugin=false \
  -PbuildNumber=1.0.0 \
  -Ptarget-platform=android-arm,android-arm64,android-x64 assembleAarProfile



# 执行：sh dev_publish_maven.sh
#
#参数说明
#-Pmaven-url 可以是本地路径，也可为远程地址
#示例：
#maven-url=file:///Users/xxx/StudioProjects/xxx/build/host
#maven-url=http://192.168.1.100:8081/repository/maven-releases/
#
#maven-user 与 maven-pwd 为maven远程用户及密码，若发布到本地无需设置
#
#-PbuildNumber=1.0.0 为版本号
#
#最后 assembleAarRelease 为打包类型，assembleAarDebug, assembleAarProfile