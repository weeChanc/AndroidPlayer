cmake_minimum_required(VERSION 3.4.1)

include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/include)
set(jnilibs "${CMAKE_SOURCE_DIR}/src/main/jniLibs")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${jnilibs}/${ANDROID_ABI})


add_library( # Sets the name of the library.
        ffplayer_jni

        # Sets the library as a shared library.
        SHARED
        src/main/cpp/adev.h
        src/main/cpp/adev-android.cpp
        src/main/cpp/adev-cmn.cpp
        src/main/cpp/vplayer.cpp
        src/main/cpp/vplayer.h
        src/main/cpp/render.cpp
        src/main/cpp/render.h
        src/main/cpp/pktqueue.cpp
        src/main/cpp/pktqueue.h
        src/main/cpp/vdev.h
        src/main/cpp/vdev-android.cpp
        src/main/cpp/vdev-cmn.cpp
        src/main/cpp/vplayer_jni.cpp
        src/main/cpp/vplayer_jni.h
        # Provides a relative path to your source file(s).
        src/main/cpp/native-lib.cpp

        src/main/cpp/player.cpp
        )

# 添加 FFmpeg 的 8 个函数库和 yuvlib 库
add_library(avutil SHARED IMPORTED )
set_target_properties(avutil PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libavutil.so")


add_library(swresample SHARED IMPORTED )
set_target_properties(swresample PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libswresample.so")


add_library(avcodec SHARED IMPORTED )
set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libavcodec.so")


add_library(avformat SHARED IMPORTED )
set_target_properties(avformat PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libavformat.so")


add_library(swscale SHARED IMPORTED )
set_target_properties(swscale PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libswscale.so")


add_library(postproc SHARED IMPORTED )
set_target_properties(postproc PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libpostproc.so")


add_library(avfilter SHARED IMPORTED )
set_target_properties(avfilter PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libavfilter.so")


add_library(avdevice SHARED IMPORTED )
set_target_properties(avdevice PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libavdevice.so")

add_library(yuv SHARED IMPORTED )
set_target_properties(yuv PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libyuv.so")


#add_library(yuv SHARED IMPORTED )
#set_target_properties(yuv PROPERTIES IMPORTED_LOCATION "${jnilibs}/${ANDROID_ABI}/libyuv.so")


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log )


#找到 Android 系统 Window 绘制相关的库
find_library(
        android-lib
        android
)


target_link_libraries( ffplayer_jni
        ${log-lib}
        ${android-lib}
        avutil
        swresample
        avcodec
        avformat
        swscale
        postproc
        avfilter
        avdevice
        yuv)