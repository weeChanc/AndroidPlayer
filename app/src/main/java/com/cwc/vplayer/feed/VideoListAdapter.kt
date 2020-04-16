package com.cwc.vplayer.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cwc.vplayer.R
import com.cwc.vplayer.entity.VideoFile
import com.drakeet.multitype.ItemViewBinder
import com.drakeet.multitype.MultiTypeAdapter
import com.ss.android.buzz.feed.live.AutoPreviewCoordinator


class VideoListAdapter : MultiTypeAdapter() {
    init {
        register(VideoFile::class.java, VideoFileBinder())
        register(String::class.java, FeedCategoryBinder())
    }
}

class VideoFileBinder : ItemViewBinder<VideoFile, VideoFileBinder.ViewHolder>() {

    class ViewHolder(val view: FeedVideoCard) : RecyclerView.ViewHolder(view) {
        val videoView = view.findViewById<StandardPreviewPlayer>(R.id.video_view)
        val cover = view.findViewById<ImageView>(R.id.video_cover)
        val title = view.findViewById<TextView>(R.id.video_title)
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

        holder.cover.setOnClickListener {
            if (!holder.videoView.isInPlayingState) {
                AutoPreviewCoordinator.pause()
                holder.view.startPreview(item)
                (adapter.items.getOrNull(AutoPreviewCoordinator.lastPreviewPosition) as? VideoFile)?.isPreviewing =
                    false
                AutoPreviewCoordinator.lastPreViewItemView = holder.view
                AutoPreviewCoordinator.lastPreviewPosition = holder.adapterPosition
            }
            holder.videoView.previewMode = 0
//            holder.cover.setImageBitmap(holder.videoView.renderProxy.initCover())
            holder.videoView.startWindowFullscreen(holder.itemView.context, false, false)

        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(FeedVideoCard(parent.context))
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

