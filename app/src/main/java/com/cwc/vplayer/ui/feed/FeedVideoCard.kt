package com.cwc.vplayer.ui.feed

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.cwc.vplayer.R
import com.cwc.vplayer.controller.SimpleVideoAllCallBack
import com.cwc.vplayer.controller.VideoManager
import com.cwc.vplayer.controller.VideoOptionBuilder
import com.cwc.vplayer.entity.VideoFile

class FeedVideoCard @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val videoView: StandardPreviewPlayer by lazy {
        findViewById<StandardPreviewPlayer>(R.id.video_view)
    }
    val cover: ImageView by lazy {
        findViewById<ImageView>(R.id.video_cover)
    }
    val title: TextView by lazy {
        findViewById<TextView>(R.id.video_title)
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.video_card, this, true)
    }

    fun startPreview(item: VideoFile) {
        //设置全屏按键功能
        item.isPreviewing = true
        videoView.visibility = View.VISIBLE
        videoView.fullscreenButton
                .setOnClickListener {
                    videoView.startWindowFullscreen(context, false, false)
                }

        //设置返回键
        videoView.backButton.visibility = View.GONE
        videoView.hideAllWidget()
        VideoOptionBuilder()
                .setIsTouchWiget(false)
                .apply {
                    if (item.path.startsWith("/")) {
                        setUrl("file://" + item.path)
                    } else {
                        setUrl(item.path)
                    }
                }
                .setVideoTitle(item.title)
                .setCacheWithPlay(false)
                .setRotateViewAuto(true)
                .setPlayTag("Preview")
                .setShowFullAnimation(false)
                .setNeedLockFull(false)
                .setVideoAllCallBack(object : SimpleVideoAllCallBack() {
                    override fun onPrepared(url: String?, vararg objects: Any?) {
                        super.onPrepared(url, *objects)
                    }

                    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                        super.onQuitFullscreen(url, *objects)
                        VideoManager.instance().setNeedMute(true);
                        videoView.previewMode = 1;
                    }

                    override fun onEnterFullscreen(url: String?, vararg objects: Any?) {
                        super.onEnterFullscreen(url, *objects)
                        VideoManager.instance().setNeedMute(false);
                    }

                }).build(videoView);

        videoView.previewMode = 1;
        videoView.startPlayLogic()
    }

    fun stopPreview() {
        videoView.visibility = View.GONE
    }

}
