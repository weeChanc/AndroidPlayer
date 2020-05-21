package com.cwc.vplayer.player.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.rockcarry.ffplayer.MediaPlayer;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

import static com.rockcarry.ffplayer.MediaPlayer.PARAM_MEDIA_POSITION;

public class MyMediaPlayerWrapper implements IMediaPlayer {
    private static final int MSG_UPDATE_PROGRESS = 1;
    private static final int MSG_HIDE_BUTTONS = 2;
    private static final int MSG_INIT_VIDEO_SIZE = 3;

    com.rockcarry.ffplayer.MediaPlayer player = new MediaPlayer();

    String url = null;
    private OnPreparedListener onPreparedListener;

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        Log.e("test","setdisplay");
        player.setDisplaySurface(surfaceHolder.getSurface());
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        this.url = uri.toString();
        Log.e("test","setDataSource");
//        player.open(uri.toString(),"video_hwaccel=1;video_rotate=0");
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> map) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
//        player.open(uri.toString(),"video_hwaccel=1;video_rotate=0");
        Log.e("test","setDataSource");
         url = uri.toString();
    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor) throws IOException, IllegalArgumentException, IllegalStateException {
        throw new RuntimeException("not support");
    }

    @Override
    public void setDataSource(String s) throws IllegalArgumentException, SecurityException, IllegalStateException {
        Log.e("test","setDataSource");
        url = s;
    }

    @Override
    public String getDataSource() {
        throw new RuntimeException("not support");
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void prepareAsync() throws IllegalStateException {
        Log.e("test","prepare async");

        player.open(url, "video_hwaccel=1;video_rotate=0");
        player.setPlayerMsgHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MediaPlayer.MSG_OPEN_DONE: {
                        Log.e("cwc", "open done change");
//                        mSeek.setMax((int) mPlayer.getParam(MediaPlayer.PARAM_MEDIA_DURATION));
                        onPreparedListener.onPrepared(MyMediaPlayerWrapper.this);


                    }
                    break;
                    case MediaPlayer.MSG_OPEN_FAILED: {
                        Log.e("cwc", "open failed");
//                        String str = String.format(getString(R.string.open_video_failed), mURL);
//                        Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
                    }
                    break;
                    case MediaPlayer.MSG_PLAY_COMPLETED: {
                        Log.e("cwc", "open completed");
//                        if (!mIsLive) finish();
                    }
                    break;
                }
            }
        });
        // do nothing
    }

    boolean isPlaying = false;

    @Override
    public void start() throws IllegalStateException {
        player.play();
        isPlaying = true;
    }

    @Override
    public void stop() throws IllegalStateException {
        isPlaying = false;
        player.pause();
    }

    @Override
    public void pause() throws IllegalStateException {
        isPlaying = false;
        player.pause();
    }

    @Override
    public void setScreenOnWhilePlaying(boolean b) {

    }

    @Override
    public int getVideoWidth() {
        return (int) player.getParam(MediaPlayer.PARAM_VIDEO_WIDTH);
    }

    @Override
    public int getVideoHeight() {
        return (int) player.getParam(MediaPlayer.PARAM_VIDEO_HEIGHT);
    }

    @Override
    public boolean isPlaying() {
        return isPlaying;
    }

    @Override
    public void seekTo(long l) throws IllegalStateException {
        player.seek(l);
    }

    @Override
    public long getCurrentPosition() {
        return player.getParam(PARAM_MEDIA_POSITION);
    }

    @Override
    public long getDuration() {
        return player.getParam(MediaPlayer.PARAM_MEDIA_DURATION);
    }

    @Override
    public void release() {
        player.close();
    }

    @Override
    public void reset() {
    }

    @Override
    public void setVolume(float v, float v1) {

    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public MediaInfo getMediaInfo() {
        return null;
    }

    @Override
    public void setLogEnabled(boolean b) {

    }

    @Override
    public boolean isPlayable() {
        return false;
    }

    @Override
    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        this.onPreparedListener = onPreparedListener;
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {

    }

    @Override
    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {

    }

    @Override
    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {

    }

    @Override
    public void setOnVideoSizeChangedListener(OnVideoSizeChangedListener onVideoSizeChangedListener) {

    }

    @Override
    public void setOnErrorListener(OnErrorListener onErrorListener) {

    }

    @Override
    public void setOnInfoListener(OnInfoListener onInfoListener) {

    }

    @Override
    public void setOnTimedTextListener(OnTimedTextListener onTimedTextListener) {

    }

    @Override
    public void setAudioStreamType(int i) {

    }

    @Override
    public void setKeepInBackground(boolean b) {

    }

    @Override
    public int getVideoSarNum() {
        return 0;
    }

    @Override
    public int getVideoSarDen() {
        return 0;
    }

    @Override
    public void setWakeMode(Context context, int i) {

    }

    @Override
    public void setLooping(boolean b) {

    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return new ITrackInfo[0];
    }

    @Override
    public void setSurface(Surface surface) {
        player.setDisplaySurface(surface);
    }

    @Override
    public void setDataSource(IMediaDataSource iMediaDataSource) {

    }
}
