package com.ss.android.buzz.feed.live

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwc.vplayer.R
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.ui.feed.FeedVideoCard
import com.cwc.vplayer.ui.feed.VideoFileBinder
import com.cwc.vplayer.ui.feed.VideoListAdapter
import kotlinx.android.synthetic.main.feed_video_card.view.*


object AutoPreviewCoordinator {

    var lastPreviewPosition: Int = -1
    var lastPreViewItemView: FeedVideoCard? = null

    fun handleLiveAutoPreview(recyclerView: RecyclerView, activity: Activity) {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            var startNewPreview = false
            val mid = Math.round((firstVisibleItemPosition + lastVisibleItemPosition) / 2.0).toInt()
            val searchPos = arrayOf(
                    firstVisibleItemPosition + 1, firstVisibleItemPosition
            )


            var selectCursor = -1
            var selectItemView: FeedVideoCard? = null;
            for (cursorPosition in searchPos) {
                val cursorItemView =
                        layoutManager.findViewByPosition(cursorPosition)
                                ?: continue
                val cursorViewHolder =
                        (recyclerView).getChildViewHolder(cursorItemView) as? VideoFileBinder.ViewHolder

                val cursorItem = cursorViewHolder?.adapterPosition
                if (cursorItem != null) {
                    if (lastPreviewPosition != cursorPosition) {
                        val item =
                                (recyclerView.adapter as? VideoListAdapter)?.items?.get(cursorItem) as? VideoFile
                                        ?: break

                        startNewPreview = true
                        val feedCard = (cursorItemView.findViewById(R.id.feed_video_card) as? FeedVideoCard)
                        feedCard?.startPreview(
                                item
                        )
                        selectCursor = cursorPosition
                        selectItemView = feedCard
                        break
                    } else {
                        return
                    }
                }
            }

            if (startNewPreview) {
                lastPreViewItemView?.stopPreview()
                if (lastPreviewPosition != -1) {
                    ((recyclerView.adapter as? VideoListAdapter)?.items?.getOrNull(lastPreviewPosition) as? VideoFile)?.isPreviewing =
                            false
                }
                lastPreViewItemView = selectItemView
                lastPreviewPosition = selectCursor
            }
        }
    }

    fun pause() {
        lastPreViewItemView?.stopPreview()
    }

    fun resume(recyclerView: RecyclerView, activity: Activity) {
        handleLiveAutoPreview(recyclerView, activity)
    }

    fun clear() {
        lastPreViewItemView = null
    }
}
