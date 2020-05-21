package com.cwc.vplayer.view.display;

import android.content.Context;
import android.util.AttributeSet;
import com.cwc.vplayer.controller.VideoManager;
import com.cwc.vplayer.controller.VideoViewBridge;


public abstract class VideoPlayer extends BaseVideoPlayer {

    public VideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public VideoPlayer(Context context) {
        super(context);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*******************************下面方法为管理器和播放控件交互的方法****************************************/

    @Override
    public VideoViewBridge getVideoManager() {
        VideoManager.instance().initContext(getContext().getApplicationContext());
        return VideoManager.instance();
    }

    @Override
    protected boolean backFromFull(Context context) {
        return VideoManager.backFromWindowFull(context);
    }

    @Override
    protected void releaseVideos() {
        VideoManager.releaseAllVideos();
    }

    @Override
    protected int getFullId() {
        return VideoManager.FULLSCREEN_ID;
    }

    @Override
    protected int getSmallId() {
        return VideoManager.SMALL_ID;
    }

}