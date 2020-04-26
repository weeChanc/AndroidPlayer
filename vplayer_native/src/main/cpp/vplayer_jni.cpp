// ����ͷ�ļ�
#include "vplayer_jni.h"
#include "vplayer.h"
#include "adev.h"
#include "vdev.h"

// this function defined in libavcodec/jni.h
extern "C" int av_jni_set_java_vm(void *vm, void *log_ctx);

/*
 * Class:     com_rockcarry_ffplayer_MediaPlayer
 * Method:    nativeOpen
 * Signature: (Ljava/lang/String;Ljava/lang/Object;IILjava/lang/String;)J
 */
static jlong JNICALL nativeOpen(JNIEnv *env, jobject obj, jstring url, jobject jsurface, jint w, jint h, jstring params)
{
    DO_USE_VAR(obj);
    DO_USE_VAR(jsurface);
    DO_USE_VAR(w);
    DO_USE_VAR(h);

    PLAYER_INIT_PARAMS playerparams;
    memset(&playerparams, 0, sizeof(playerparams));
    if (params != NULL) {
        char *strparams = (char*)env->GetStringUTFChars(params, NULL);
        player_load_params(&playerparams, strparams);
        env->ReleaseStringUTFChars(params, strparams);
    }

    const char *strurl = env->GetStringUTFChars(url, NULL);
    playerparams.vdev_render_type = VDEV_RENDER_TYPE_ANDROID;
    jlong hplayer = (jlong)player_open((char*)strurl, obj, &playerparams);
    env->ReleaseStringUTFChars(url, strurl);
    return hplayer;
}

/*
 * Class:     com_rockcarry_ffplayer_MediaPlayer
 * Method:    nativeClose
 * Signature: (J)V
 */
static void nativeClose(JNIEnv *env, jobject obj, jlong hplayer)
{
    DO_USE_VAR(env);
    DO_USE_VAR(obj);
    player_close((void*)hplayer);
}

/*
 * Class:     com_rockcarry_ffplayer_MediaPlayer
 * Method:    nativePlay
 * Signature: (J)V
 */
static void JNICALL nativePlay(JNIEnv *env, jobject obj, jlong hplayer)
{
    DO_USE_VAR(env);
    DO_USE_VAR(obj);
    player_play((void*)hplayer);
}

/*
 * Class:     com_rockcarry_ffplayer_MediaPlayer
 * Method:    nativePause
 * Signature: (J)V
 */
static void JNICALL nativePause(JNIEnv *env, jobject obj, jlong hplayer)
{
    DO_USE_VAR(env);
    DO_USE_VAR(obj);
    player_pause((void*)hplayer);
}

/*
 * Class:     com_rockcarry_ffplayer_MediaPlayer
 * Method:    nativeSeek
 * Signature: (JJ)V
 */
static void JNICALL nativeSeek(JNIEnv *env, jobject obj, jlong hplayer, jlong ms)
{
    DO_USE_VAR(env);
    DO_USE_VAR(obj);
    player_seek((void*)hplayer, ms, SEEK_PRECISELY);
}

/*
 * Class:     com_rockcarry_ffplayer_MediaPlayer
 * Method:    nativeSetParam
 * Signature: (JIJ)V
 */
static void JNICALL nativeSetParam(JNIEnv *env, jobject obj, jlong hplayer, jint id, jlong value)
{
    DO_USE_VAR(env);
    DO_USE_VAR(obj);
    player_setparam((void*)hplayer, id, &value);
}

/*
 * Class:     com_rockcarry_ffplayer_MediaPlayer
 * Method:    nativeGetParam
 * Signature: (JI)J
 */
static jlong JNICALL nativeGetParam(JNIEnv *env, jobject obj, jlong hplayer, jint id)
{
    DO_USE_VAR(env);
    DO_USE_VAR(obj);
    jlong value = 0;
    player_getparam((void*)hplayer, id, &value);
    return value;
}

/*
 * Class:     com_rockcarry_ffplayer_MediaPlayer
 * Method:    nativeSetDisplaySurface
 * Signature: (JLjava/lang/Object;)V
 */
static void JNICALL nativeSetDisplaySurface(JNIEnv *env, jobject obj, jlong hplayer, jobject surface)
{
    DO_USE_VAR(obj);

    void *vdev = NULL;
    player_getparam((void*)hplayer, PARAM_VDEV_GET_CONTEXT, &vdev);
    vdev_android_setwindow(vdev, surface);
}


//++ jni register ++//
static JavaVM* g_jvm = NULL;

static const JNINativeMethod g_methods[] = {
    { "nativeOpen"              , "(Ljava/lang/String;Ljava/lang/Object;IILjava/lang/String;)J", (void*)nativeOpen },
    { "nativeClose"             , "(J)V"  , (void*)nativeClose    },
    { "nativePlay"              , "(J)V"  , (void*)nativePlay     },
    { "nativePause"             , "(J)V"  , (void*)nativePause    },
    { "nativeSeek"              , "(JJ)V" , (void*)nativeSeek     },
    { "nativeSetParam"          , "(JIJ)V", (void*)nativeSetParam },
    { "nativeGetParam"          , "(JI)J" , (void*)nativeGetParam },
    { "nativeSetDisplaySurface" , "(JLjava/lang/Object;)V", (void*)nativeSetDisplaySurface },
};

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
    DO_USE_VAR(reserved);

    JNIEnv* env = NULL;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK || !env) {
        __android_log_print(ANDROID_LOG_ERROR, "ffplayer_jni", "ERROR: GetEnv failed\n");
        return -1;
    }

    jclass cls = env->FindClass("com/rockcarry/ffplayer/MediaPlayer");
    int ret = env->RegisterNatives(cls, g_methods, sizeof(g_methods)/sizeof(g_methods[0]));
    if (ret != JNI_OK) {
        __android_log_print(ANDROID_LOG_ERROR, "ffplayer_jni", "ERROR: failed to register native methods !\n");
        return -1;
    }

    // for g_jvm
    g_jvm = vm;
    av_jni_set_java_vm(vm, NULL);
    return JNI_VERSION_1_4;
}

JNIEXPORT JavaVM* get_jni_jvm(void)
{
    return g_jvm;
}

JNIEXPORT JNIEnv* get_jni_env(void)
{
    JNIEnv *env;
    int status;
    if (NULL == g_jvm) {
        __android_log_print(ANDROID_LOG_ERROR, "ffplayer_jni", "g_jvm == NULL !\n");
        return NULL;
    }
    status = g_jvm->GetEnv((void **)&env, JNI_VERSION_1_4);
    if (status != JNI_OK) {
//      __android_log_print(ANDROID_LOG_DEBUG, "ffplayer_jni", "failed to get JNI environment assuming native thread !\n");
        status = g_jvm->AttachCurrentThread(&env, NULL);
        if (status != JNI_OK) {
            __android_log_print(ANDROID_LOG_ERROR, "ffplayer_jni", "failed to attach current thread !\n");
            return NULL;
        }
    }
    return env;
}
//-- jni register --//

