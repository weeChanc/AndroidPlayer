

#include <jni.h>

extern "C" {
JNIEXPORT jstring JNICALL Java_com_cwc_vplayer_jni_Utils_hello(JNIEnv *env, jclass thiz) {
    return env->NewStringUTF("hello jni");
}
}