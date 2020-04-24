package com.rockcarry.ffplayer;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;

public final class MediaPlayer
{
    public static final int MSG_OPEN_DONE           = (('O' << 24) | ('P' << 16) | ('E' << 8) | ('N' << 0));
    public static final int MSG_OPEN_FAILED         = (('F' << 24) | ('A' << 16) | ('I' << 8) | ('L' << 0));
    public static final int MSG_PLAY_PROGRESS       = (('R' << 24) | ('U' << 16) | ('N' << 8) | (' ' << 0));
    public static final int MSG_PLAY_COMPLETED      = (('E' << 24) | ('N' << 16) | ('D' << 8) | (' ' << 0));

    public static final int PARAM_MEDIA_DURATION    = 0x1000 + 0;
    public static final int PARAM_MEDIA_POSITION    = 0x1000 + 1;
    public static final int PARAM_VIDEO_WIDTH       = 0x1000 + 2;
    public static final int PARAM_VIDEO_HEIGHT      = 0x1000 + 3;
//  public static final int PARAM_VIDEO_MODE        = 0x1000 + 4;
    public static final int PARAM_AUDIO_VOLUME      = 0x1000 + 5;
    public static final int PARAM_PLAY_SPEED        = 0x1000 + 6;
//  public static final int PARAM_VISUAL_EFFECT     = 0x1000 + 7;
    public static final int PARAM_AVSYNC_TIME_DIFF  = 0x1000 + 8;
//  public static final int PARAM_PLAYER_CALLBACK   = 0x1000 + 9;

    public MediaPlayer() {
    }

    public MediaPlayer(String url, Handler h, String params) {
        mPlayerMsgHandler = h;
        open(url, params);
    }

    protected void finalize() {
        close();
    }

    public boolean open(String url, String params) {
        nativeClose(m_hPlayer);
        m_hPlayer = nativeOpen(url, null, 0, 0, params);
        return m_hPlayer != 0 ? true : false;
    }

    public void close() {
        nativeClose(m_hPlayer);
        m_hPlayer = 0;
    }

    public void play ()                      { nativePlay (m_hPlayer);     }
    public void pause()                      { nativePause(m_hPlayer);     }
    public void seek (long ms)               { nativeSeek (m_hPlayer, ms); }
    public void setParam(int id, long value) { nativeSetParam(m_hPlayer, id, value); }
    public long getParam(int id)             { return nativeGetParam(m_hPlayer, id); }

    public void setDisplaySurface(Object surface) {
        nativeSetDisplaySurface(m_hPlayer, surface);
    }

    public void setPlayerMsgHandler(Handler h) {
        mPlayerMsgHandler = h;
    }

    public boolean initVideoSize(int rw, int rh, View v) {
        int vw = (int)getParam(MediaPlayer.PARAM_VIDEO_WIDTH ); // video width
        int vh = (int)getParam(MediaPlayer.PARAM_VIDEO_HEIGHT); // video height
        if (rw <= 0 || rh <= 0 || vw <= 0 || vh <= 0 || v == null) return false;

        int sw, sh; // scale width & height
        if (rw * vh < vw * rh) {
            sw = rw; sh = sw * vh / vw;
        } else {
            sh = rh; sw = sh * vw / vh;
        }

        ViewGroup.LayoutParams lp = v.getLayoutParams();
        lp.width  = sw;
        lp.height = sh;
        v.setLayoutParams(lp);
        return true;
    }

    //++ for player event callback
    private Handler mPlayerMsgHandler = null;
    private void internalPlayerEventCallback(int event, long param) {
        if (mPlayerMsgHandler != null) {
            Message msg = new Message();
            msg.what = event;
            msg.obj  = new Long(param);
            mPlayerMsgHandler.sendMessage(msg);
        }
    }
    //-- for player event callback

    private long m_hPlayer = 0;
    private native long nativeOpen (String url, Object surface, int w, int h, String params);
    private native void nativeClose(long hplayer);
    private native void nativePlay (long hplayer);
    private native void nativePause(long hplayer);
    private native void nativeSeek (long hplayer, long ms);
    private native void nativeSetParam(long hplayer, int id, long value);
    private native long nativeGetParam(long hplayer, int id);
    private native void nativeSetDisplaySurface(long hplayer, Object surf);

    static {
        System.loadLibrary("ffplayer_jni");
    }
};

