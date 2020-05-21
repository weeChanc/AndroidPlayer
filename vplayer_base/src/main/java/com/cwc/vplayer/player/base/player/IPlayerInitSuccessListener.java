package com.cwc.vplayer.player.base.player;

import com.cwc.vplayer.player.base.model.VideoModel;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 播放器初始化成果回调
 */
public interface IPlayerInitSuccessListener {
    void onPlayerInitSuccess(IMediaPlayer player, VideoModel model);
}
