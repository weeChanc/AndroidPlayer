#include <jni.h>

#ifndef _included_ffplayer_jni_
#define _included_ffplayer_jni_

JNIEXPORT JavaVM* get_jni_jvm(void);
JNIEXPORT JNIEnv* get_jni_env(void);
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM*, void*);

#endif
