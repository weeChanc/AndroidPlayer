package com.cwc.vplayer.ui.main;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;

//import com.yang.testapp.interfaces.MediaCallback;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaCodecUtils {

    private static final String TAG = "MediaCodecUtils";
    final int TIMEOUT_USEC = 10000;   // 10 毫秒
    private boolean isPlaying = false;
    private Surface surface;

    private VideoThread videoThread;
    private AudioThread audioThread;
    private String mediaPath;
//    private MediaCallback callback;

    // 处理视频通道
    private class VideoThread extends Thread {

        private boolean isVideoOver = false;
        int frameIndex = 0;

        @Override
        public void run() {
            try {
                MediaExtractor videoExtractor = new MediaExtractor();
                MediaCodec mediaCodec = null;
                videoExtractor.setDataSource(mediaPath);
                // 获得视频所在的 轨道
                int trackIndex = getMediaTrackIndex(videoExtractor, "video/");
                if (trackIndex >=0) {
                    MediaFormat format = videoExtractor.getTrackFormat(trackIndex);
                    // 指定解码后的帧格式
                    format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatRGBFlexible);

                    String mimeType = format.getString(MediaFormat.KEY_MIME);
                    int width = format.getInteger(MediaFormat.KEY_WIDTH);
                    int height = format.getInteger(MediaFormat.KEY_HEIGHT);
                    // duration 是微秒 1 毫秒 = 1000微秒
                    long duration = format.getLong(MediaFormat.KEY_DURATION);
//                    if (callback != null) {
////                        callback.getMediaBaseMsg(width, height, duration);
//                    }

                    // 切换到视频信道
                    videoExtractor.selectTrack(trackIndex);
                    // 创将解码视频的MediaCodec，解码器
                    mediaCodec = MediaCodec.createDecoderByType(mimeType);
                    // 配置绑定 surface
                    mediaCodec.configure(format, surface, null, 0);
                }

                if (mediaCodec == null) {
                    Log.v(TAG, "MediaCodec null");
                    return;
                }
                mediaCodec.start();

                // 开始循环，一直到视频资源结束
                MediaCodec.BufferInfo videoBufferInfo = new MediaCodec.BufferInfo();
                ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();  // 用来存放媒体文件的数据
                ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();  // 解码后的数据
                long startMs = System.currentTimeMillis();  // 开始的时间
                // 当前Thread 没有被中断
                while (!Thread.interrupted()) {
                    if (!isPlaying) {
                        continue;
                    }

                    if (!isVideoOver) {
                        // 视频没有结束  提取一个单位的视频资源放到 解码器(mediaCodec) 缓冲区中
                        isVideoOver = putBufferToMediaCodec(videoExtractor, mediaCodec, inputBuffers);
                    }

                    // 返回一个被成功解码的buffer的 index 或者是一个信息  同时更新 videoBufferInfo 的数据
                    int outputBufferIndex = mediaCodec.dequeueOutputBuffer(videoBufferInfo, TIMEOUT_USEC);
                    switch (outputBufferIndex) {
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            Log.v(TAG, "format changed");
                            break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            Log.v(TAG, "超时");
                            break;
                        case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                            //outputBuffers = videoCodec.getOutputBuffers();
                            Log.v(TAG, "output buffers changed");
                            break;
                        default:
                            //直接渲染到Surface时使用不到outputBuffer
//                            ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                            //延时操作
                            //如果缓冲区里的可展示时间>当前视频播放的总时间，就休眠一下 展示当前的帧，
                            sleepRender(videoBufferInfo, startMs);

                            //渲染为true就会渲染到surface   configure() 设置的surface
                            mediaCodec.releaseOutputBuffer(outputBufferIndex, true);
                            frameIndex ++;
                            Log.v(TAG, "frameIndex   " + frameIndex);

                            break;
                    }

                    if ((videoBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                        Log.v(TAG, "buffer stream end");
                        break;
                    }
                }

                mediaCodec.stop();
                mediaCodec.release();
                videoExtractor.release();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isSemiPlanarYUV(int colorFormat) {
        switch (colorFormat) {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                return false;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                return true;
            default:
                throw new RuntimeException("unknown format " + colorFormat);
        }
    }

    // 处理音频通道
    private class AudioThread extends Thread {

        private int audioInputBufferSize;

        private AudioTrack audioTrack;

        @Override
        public void run() {
            MediaExtractor audioExtractor = new MediaExtractor();
            MediaCodec audioCodec = null;
            try {
                audioExtractor.setDataSource(mediaPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < audioExtractor.getTrackCount(); i++) {
                MediaFormat mediaFormat = audioExtractor.getTrackFormat(i);
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    audioExtractor.selectTrack(i);
                    int audioChannels = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                    int audioSampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE);
                    int minBufferSize = AudioTrack.getMinBufferSize(audioSampleRate,
                            (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                            AudioFormat.ENCODING_PCM_16BIT);
                    int maxInputSize = mediaFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE);
                    audioInputBufferSize = minBufferSize > 0 ? minBufferSize * 4 : maxInputSize;
                    int frameSizeInBytes = audioChannels * 2;
                    audioInputBufferSize = (audioInputBufferSize / frameSizeInBytes) * frameSizeInBytes;
                    audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            audioSampleRate,
                            (audioChannels == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO),
                            AudioFormat.ENCODING_PCM_16BIT,
                            audioInputBufferSize,
                            AudioTrack.MODE_STREAM);
                    audioTrack.play();
                    Log.v(TAG, "audio play");
                    //
                    try {
                        audioCodec = MediaCodec.createDecoderByType(mime);
                        audioCodec.configure(mediaFormat, null, null, 0);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            if (audioCodec == null) {
                Log.v(TAG, "audio decoder null");
                return;
            }
            audioCodec.start();
            //
            final ByteBuffer[] buffers = audioCodec.getOutputBuffers();
            int sz = buffers[0].capacity();
            if (sz <= 0)
                sz = audioInputBufferSize;
            byte[] mAudioOutTempBuf = new byte[sz];

            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer[] inputBuffers = audioCodec.getInputBuffers();
            ByteBuffer[] outputBuffers = audioCodec.getOutputBuffers();
            boolean isAudioEOS = false;
            long startMs = System.currentTimeMillis();

            while (!Thread.interrupted()) {
                if (!isPlaying) {
                    continue;
                }
                if (!isAudioEOS) {
                    isAudioEOS = putBufferToMediaCodec(audioExtractor, audioCodec, inputBuffers);
                }
                //
                int outputBufferIndex = audioCodec.dequeueOutputBuffer(audioBufferInfo, TIMEOUT_USEC);
                switch (outputBufferIndex) {
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        Log.v(TAG, "format changed");
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        Log.v(TAG, "超时");
                        break;
                    case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
                        outputBuffers = audioCodec.getOutputBuffers();
                        Log.v(TAG, "output buffers changed");
                        break;
                    default:
                        ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                        //延时操作
                        //如果缓冲区里的可展示时间>当前视频播放的进度，就休眠一下
                        sleepRender(audioBufferInfo, startMs);
                        if (audioBufferInfo.size > 0) {
                            if (mAudioOutTempBuf.length < audioBufferInfo.size) {
                                mAudioOutTempBuf = new byte[audioBufferInfo.size];
                            }
                            outputBuffer.position(0);
                            outputBuffer.get(mAudioOutTempBuf, 0, audioBufferInfo.size);
                            outputBuffer.clear();
                            if (audioTrack != null)
                                audioTrack.write(mAudioOutTempBuf, 0, audioBufferInfo.size);
                        }
                        //
                        audioCodec.releaseOutputBuffer(outputBufferIndex, false);
                        break;
                }

                if ((audioBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    Log.v(TAG, "buffer stream end");
                    break;
                }
            }//end while
            audioCodec.stop();
            audioCodec.release();
            audioExtractor.release();
            audioTrack.stop();
            audioTrack.release();
        }
    }

    public void  prepareMediaFile(String path, Surface layout) {
        this.mediaPath = path;
//        this.callback = callback;
        this.surface = layout;
    }

    public void playMediaFile() {
        isPlaying = true;
        if (videoThread == null) {
            videoThread = new VideoThread();
            videoThread.start();
        }
        if (audioThread == null) {
            audioThread = new AudioThread();
//            audioThread.start();
        }
    }

    public void stopMediaFile() {
        isPlaying = false;
    }

    //获取指定类型媒体文件所在轨道
    private int getMediaTrackIndex(MediaExtractor videoExtractor, String MEDIA_TYPE) {
        int trackIndex = -1;
        // 获得轨道数量
        int trackNum = videoExtractor.getTrackCount();
        for (int i = 0; i < trackNum; i++) {
            MediaFormat mediaFormat = videoExtractor.getTrackFormat(i);
            String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith(MEDIA_TYPE)) {
                trackIndex = i;
                break;
            }
        }
        return trackIndex;
    }


    /**
     * 将缓冲区传递至解码器
     * 如果到了文件末尾，返回true;否则返回false
     */
    private boolean putBufferToMediaCodec(MediaExtractor extractor, MediaCodec decoder, ByteBuffer[] inputBuffers) {
        boolean isMediaEOS = false;
        // 解码器  要填充有效数据的输入缓冲区的索引 —————— 此id的缓冲区可以被使用
        int inputBufferIndex = decoder.dequeueInputBuffer(TIMEOUT_USEC);
        if (inputBufferIndex >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
            // MediaExtractor读取媒体文件的数据，存储到缓冲区中。并返回大小。结束为-1
            int sampleSize = extractor.readSampleData(inputBuffer, 0);
            if (sampleSize < 0) {
                decoder.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                isMediaEOS = true;
                Log.v(TAG, "media eos");
            } else {
                // 在输入缓冲区添加数据之后，把它告诉 MediaCodec （解码）
                decoder.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                // MediaExtractor 准备下一个 单位的数据
                boolean ad = extractor.advance();
                if (!ad) {
                    isMediaEOS = false;
                }
            }
        } else {
            // 缓冲区不可用
        }
        return isMediaEOS;
    }

    private void sleepRender(MediaCodec.BufferInfo audioBufferInfo, long startMs) {
        // 这里的时间是 毫秒  presentationTimeUs 的时间是累加的 以微秒进行一帧一帧的累加
        // audioBufferInfo 是改变的
        while (audioBufferInfo.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs) {
            try {
                // 10 毫秒
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
