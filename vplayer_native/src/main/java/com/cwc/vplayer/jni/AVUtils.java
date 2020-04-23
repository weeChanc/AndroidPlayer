package com.cwc.vplayer.jni;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.view.Surface;

/**
 * author: haohao
 * time: 2017/12/19
 * mail: haohaochang86@gmail.com
 * desc: AVUtils
 */
public class AVUtils {
    private static final String TAG = "AVUtils";
    private static AVCallback AVCallback;
    private static AVCallback sAVCallback;
    public static void registerCallback(AVCallback callback) {
        sAVCallback = callback;
    }

    public static void onNativeCallback(){
        Log.e("GGOOOO","GOOO");
    }

    static {
        System.loadLibrary("avutil");
        System.loadLibrary("avcodec");
        System.loadLibrary("avformat");
        System.loadLibrary("avfilter");
        System.loadLibrary("avdevice");
        System.loadLibrary("swresample");
        System.loadLibrary("swscale");
        System.loadLibrary("postproc");
        System.loadLibrary("native-lib");
        System.loadLibrary("c++_shared");
        System.loadLibrary("yuv");
    }
    /**
     * 解码视频中的视频压缩数据
     * @param input_file_path 输入的视频文件路径
     * @param output_file_path 视频压缩数据解码后输出的 YUV 文件路径
     */
    public static native void videoDecode(String input_file_path, String output_file_path);

    /**
     * 显示视频视频解码后像素数据
     * @param input 输入的视频文件路径
     * @param surface 用于显示视频视频解码后的 RGBA 像素数据
     */
    public static native void videoRender(String input, Surface surface);

    /**
     * 解码视频中的音频压缩数据
     * @param input 输入的视频文件路径
     * @param output 音频压缩数据解码后输出的 PCM 文件路径
     */
    public static native void audioDecode(String input, String output);

    /**
     * 播放视频中的音频数据
     * @param input 输入的视频文件路径
     */
    public static native void audioPlay(String input);

    /**
     * 创建一个 AudioTrack 对象，用于播放音频,在 Native 层中调用。
     */
    public static AudioTrack createAudioTrack(int sampleRate, int num_channel) {
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        Log.i(TAG, "声道数:" + num_channel);
        int channelConfig;
        if (num_channel == 1) {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_MONO;
        } else if (num_channel == 2) {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
        } else {
            channelConfig = android.media.AudioFormat.CHANNEL_OUT_STEREO;
        }

        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, channelConfig, audioFormat);

        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate, channelConfig,
                audioFormat,
                bufferSize, AudioTrack.MODE_STREAM);
        return audioTrack;
    }

    public interface AVCallback {
        void onFinish();
    }
}
