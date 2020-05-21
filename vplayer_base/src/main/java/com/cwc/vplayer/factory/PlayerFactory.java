package com.cwc.vplayer.factory;

import com.cwc.vplayer.player.base.player.IPlayerManager;
import com.cwc.vplayer.player.impl.IjkPlayerManager;
import com.cwc.vplayer.player.impl.MyMediaPlayer;

/**
 * 播放内核工厂
 */
public class PlayerFactory {

    private static Class<? extends IPlayerManager> sPlayerManager;

    public static void setPlayManager(Class<? extends IPlayerManager> playManager) {
        sPlayerManager = playManager;
    }

    public static IPlayerManager getPlayManager() {
        if (sPlayerManager == null) {
            sPlayerManager = MyMediaPlayer.class;
        }
        try {
            return sPlayerManager.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
