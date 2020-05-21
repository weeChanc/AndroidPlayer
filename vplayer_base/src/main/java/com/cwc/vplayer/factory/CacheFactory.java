package com.cwc.vplayer.factory;

import android.content.Context;

import com.cwc.vplayer.player.base.cache.ICacheManager;

import java.io.File;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * 缓存到本地服务工厂
 */
public class CacheFactory {

    private static Class<? extends ICacheManager> sICacheManager;

    public static void setCacheManager(Class<? extends ICacheManager> cacheManager) {
        sICacheManager = cacheManager;
    }

    public static ICacheManager getCacheManager() {
        if (sICacheManager == null) {
            // TODO: chenweicheng 看播开播拆分 2020/4/2
            sICacheManager = Cache.class;
        }
        try {
            return sICacheManager.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    class Cache implements ICacheManager {

        @Override
        public void doCacheLogic(Context context, IMediaPlayer mediaPlayer, String url, Map<String, String> header, File cachePath) {

        }

        @Override
        public void clearCache(Context context, File cachePath, String url) {

        }

        @Override
        public void release() {

        }

        @Override
        public boolean hadCached() {
            return false;
        }

        @Override
        public boolean cachePreview(Context context, File cacheDir, String url) {
            return false;
        }

        @Override
        public void setCacheAvailableListener(ICacheAvailableListener cacheAvailableListener) {

        }
    }
}
