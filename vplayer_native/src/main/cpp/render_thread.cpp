
#include "thread"

extern "C" {
#include <libyuv/convert_argb.h>
#include <jni.h>
#include <string.h>
#include <android/log.h>
#include <stdio.h>
#include <libavutil/time.h>
//编码
#include "include/libavcodec/avcodec.h"
//封装格式处理
#include "include/libavformat/avformat.h"
//像素处理
#include "include/libswscale/swscale.h"
//使用这两个 Window 相关的头文件
// 需要在 CMake 脚本中引入 android 库
#include <android/native_window_jni.h>
#include <android/native_window.h>

}
//中文字符串转换

// Consumer Productor 模型

extern "C" {

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"weechan",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"weechan",FORMAT,##__VA_ARGS__);

void startRead() {

}

void startReadThread() {
    std::thread thread(&startRead);

}
}
