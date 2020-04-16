package com.cwc.vplayer.view.render.listener;


import java.io.File;

/**
 * 截屏保存结果
 */

public interface VideoShotSaveListener {
    void result(boolean success, File file);
}
