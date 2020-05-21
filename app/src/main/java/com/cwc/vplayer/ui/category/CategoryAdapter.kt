package com.cwc.vplayer.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.blankj.utilcode.util.ToastUtils
import com.cwc.vplayer.R
import com.cwc.vplayer.entity.VideoCategory
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.ui.feed.FeedCategoryBinder
import com.cwc.vplayer.ui.feed.VideoFileBinder
import com.cwc.vplayer.ui.feed.VideoListAdapter
import com.drakeet.multitype.ItemViewBinder
import com.drakeet.multitype.MultiTypeAdapter

class CategoryAdapter : MultiTypeAdapter() {

}

class CategoryFileBinder(val onClick: ((category: VideoCategory) -> Unit)? = null) :
        ItemViewBinder<VideoCategory, CategoryFileBinder.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.video_category)
        val extra = view.findViewById<TextView>(R.id.category_extra)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VideoCategory) {
        holder.title.text = item.name;
        holder.extra.text = "${item.count}个视频"
        holder.itemView.setOnClickListener {
            onClick?.invoke(item)
        }
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.video_category, parent, false))
    }
}

class CategoryTitleBinder(val onClick: ((category: VideoCategory) -> Unit)? = null) :
        ItemViewBinder<String, RecyclerView.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.category_title, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: String) {
    }

}