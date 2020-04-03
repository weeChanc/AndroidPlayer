package com.cwc.vplayer.feed

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cwc.vplayer.R
import com.cwc.vplayer.controller.GSYSampleCallBack
import com.cwc.vplayer.controller.GSYVideoManager
import com.cwc.vplayer.controller.GSYVideoOptionBuilder
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.view.display.StandardGSYVideoPlayer
import com.drakeet.multitype.ItemViewBinder
import com.drakeet.multitype.MultiTypeAdapter


class FeedListAdapter : MultiTypeAdapter() {

    init {
        register(VideoFile::class.java, VideoFileBinder())
        register(String::class.java, FeedCategoryBinder())
    }
}

class VideoFileBinder : ItemViewBinder<VideoFile, VideoFileBinder.ViewHolder>() {

    companion object {
        var lastPlayingViewHolder: ViewHolder? = null
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val videoView = view.findViewById<StandardGSYVideoPlayer>(R.id.video_view)
        val cover = view.findViewById<ImageView>(R.id.video_cover)
        val title = view.findViewById<TextView>(R.id.video_title)

        fun canPreview(bottom:Int): Boolean{
            return true
        }

        fun stopPreview(){

        }

        fun startPreview(ctx : Context?){

        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        if (holder == lastPlayingViewHolder) {
            GSYVideoManager.instance()?.curPlayerManager?.release()
            lastPlayingViewHolder?.videoView?.visibility = View.GONE
        }
        super.onViewDetachedFromWindow(holder)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VideoFile) {

        Glide.with(holder.view).load(item.path).into(holder.cover)
        holder.title.text = item.path

        holder.cover.setOnClickListener {
            if (lastPlayingViewHolder != null) {
                GSYVideoManager.instance()?.curPlayerManager?.release()
                lastPlayingViewHolder?.videoView?.visibility = View.GONE
            }
            lastPlayingViewHolder = holder
            holder.videoView.visibility = View.VISIBLE
            //设置全屏按键功能
            holder.videoView.fullscreenButton
                .setOnClickListener {
                    holder.videoView.startWindowFullscreen(holder.itemView.context, false, false)
                }

            //设置返回键
            holder.videoView.backButton.visibility = View.GONE

            GSYVideoOptionBuilder().setIsTouchWiget(false)
                .setThumbImageView(ImageView(holder.cover.context).apply {
                    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    Glide.with(holder.view).load(item.path).into(this)
                })
                .setUrl("file://" + item.path)
                .setVideoTitle(item.title)
                .setCacheWithPlay(false)
                .setRotateViewAuto(true)
                .setLockLand(true)
                .setPlayTag("VideoFileBinder")
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setVideoAllCallBack(object : GSYSampleCallBack() {
                    override fun onPrepared(url: String?, vararg objects: Any?) {
                        super.onPrepared(url, *objects)
                    }

                    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
                        super.onQuitFullscreen(url, *objects)
                        GSYVideoManager.instance().setNeedMute(true);
                    }

                    override fun onEnterFullscreen(url: String?, vararg objects: Any?) {
                        super.onEnterFullscreen(url, *objects)
                        GSYVideoManager.instance().setNeedMute(false);
                    }

                }).build(holder.videoView);
        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.video_card, parent, false))
    }
}

class FeedCategoryBinder : ItemViewBinder<String, FeedCategoryBinder.ViewHolder>() {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val category = view.findViewById<TextView>(R.id.video_category)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: String) {
        holder.category.text = item
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.video_category, null))
    }

}

