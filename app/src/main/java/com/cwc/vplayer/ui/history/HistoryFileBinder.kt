package com.cwc.vplayer.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import com.cwc.vplayer.R
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.ui.feed.VideoFileBinder

class HistoryFileBinder(onClickAction: ((VideoFile) -> Unit)?) : VideoFileBinder(onClickAction) {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.feed_history_video_card, parent, false))
    }
}