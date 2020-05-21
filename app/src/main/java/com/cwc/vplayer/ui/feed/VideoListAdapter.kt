package com.cwc.vplayer.ui.feed

import android.util.Log
import android.util.TimeUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.bumptech.glide.Glide
import com.cwc.vplayer.R
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.entity.db.AppDataBase
import com.drakeet.multitype.ItemViewBinder
import com.drakeet.multitype.MultiTypeAdapter
import com.ss.android.buzz.feed.live.AutoPreviewCoordinator
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class VideoListAdapter : MultiTypeAdapter() {
    init {

    }
}

open class VideoFileBinder(val onClickAction: ((VideoFile) -> Unit)?) : ItemViewBinder<VideoFile, VideoFileBinder.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val videoView = view.findViewById<StandardPreviewPlayer>(R.id.video_view)
        val feedCard = view.findViewById<FeedVideoCard>(R.id.feed_video_card)
        val durationText = view.findViewById<TextView>(R.id.duration_text)
        val cover = view.findViewById<ImageView>(R.id.video_cover)
        val title = view.findViewById<TextView>(R.id.video_title)
        val moreAction = view.findViewById<View>(R.id.more_action)
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.videoView.visibility = View.GONE

    }

    override fun onBindViewHolder(holder: ViewHolder, item: VideoFile) {

        Glide.with(holder.view).load(item.path).into(holder.cover)
        holder.title.text = item.path

        if (item.isPreviewing) {
            holder.videoView.visibility = View.VISIBLE
        } else {
            holder.videoView.visibility = View.GONE
        }

        holder.durationText.text = com.cwc.vplayer.utils.TimeUtils.getTimeFormLong(item.duration)
        if (item.duration == 0L) {
            holder.durationText.visibility = View.GONE
        }

        holder.moreAction.setOnClickListener {
            onClickAction?.invoke(item)
        }

        holder.cover.setOnClickListener {
            if (!holder.videoView.isInPlayingState) {
                AutoPreviewCoordinator.pause()
                holder.feedCard.startPreview(item)
                (adapter.items.getOrNull(AutoPreviewCoordinator.lastPreviewPosition) as? VideoFile)?.isPreviewing =
                        false
                AutoPreviewCoordinator.lastPreViewItemView = holder.feedCard
                AutoPreviewCoordinator.lastPreviewPosition = holder.adapterPosition
            }
            holder.videoView.previewMode = 0
            holder.videoView.startWindowFullscreen(holder.itemView.context, false, false)
            GlobalScope.launch {
                AppDataBase.INSTANCE.appDao().updateVideoFile(item.apply {
                    item.lastPlayTimeStamp = System.currentTimeMillis()
                })
            }
        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.feed_video_card, parent, false))
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

