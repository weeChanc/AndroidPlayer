package com.cwc.vplayer.ui.history;

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwc.vplayer.R
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.ui.feed.FeedCategoryBinder
import com.cwc.vplayer.ui.feed.VideoFileBinder
import com.cwc.vplayer.ui.feed.VideoListAdapter
import com.drakeet.multitype.ItemViewBinder

class HistoryItemBinder : ItemViewBinder<HisotryItem, HistoryItemBinder.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recylerview)
        val empty_view = view.findViewById<View>(R.id.empty_text)
    }

    override fun onBindViewHolder(holder: ViewHolder, item: HisotryItem) {
        if (!item.historyVideo.isEmpty()) {
            holder.empty_view.visibility = View.GONE
        }
        val adapter = VideoListAdapter();
        adapter.register(VideoFile::class.java, HistoryFileBinder(null))
        adapter.register(String::class.java, FeedCategoryBinder())
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        holder.recyclerView.adapter = adapter;
        adapter.items = item.historyVideo.sortedWith(object : Comparator<VideoFile> {
            override fun compare(o1: VideoFile, o2: VideoFile): Int {
                return -o1.lastPlayTimeStamp.compareTo(o2.lastPlayTimeStamp)
            }
        })
        adapter.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(
                inflater.inflate(
                        R.layout.history_item,
                        parent,
                        false
                )
        )
    }
}