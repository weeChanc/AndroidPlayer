package com.cwc.vplayer.player.base.player;


import com.cwc.vplayer.player.base.model.VideoModel;

/**
 * 播放器差异管理接口
 */

public abstract class BasePlayerManager implements IPlayerManager {

    protected IPlayerInitSuccessListener mPlayerInitSuccessListener;

    public IPlayerInitSuccessListener getPlayerPreparedSuccessListener() {
        return mPlayerInitSuccessListener;
    }

    public void setPlayerInitSuccessListener(IPlayerInitSuccessListener listener) {
        this.mPlayerInitSuccessListener = listener;
    }

    protected void initSuccess(VideoModel videoModel) {
        if (mPlayerInitSuccessListener != null) {
            mPlayerInitSuccessListener.onPlayerInitSuccess(getMediaPlayer(), videoModel);
        }
    }
}
