package com.ss.android.buzz.feed.live

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.feed.FeedVideoCard
import com.cwc.vplayer.feed.VideoFileBinder
import com.cwc.vplayer.feed.VideoListAdapter


class AutoPreviewCoordinator {

    private var lastPreviewPosition: Int = -1
    private var lastPreViewItemView: FeedVideoCard? = null

    fun handleLiveAutoPreview(recyclerView: RecyclerView, activity: Activity) {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            var startNewPreview = false
            val mid = (firstVisibleItemPosition + lastVisibleItemPosition) / 2
            val searchPos = arrayOf(
                (mid), mid - 1, mid + 1
            )


            var selectCursor = -1
            var selectItemView: FeedVideoCard? = null;
            for (cursorPosition in searchPos) {
                val cursorItemView =
                    layoutManager.findViewByPosition(cursorPosition) as? FeedVideoCard
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
                        cursorItemView.startPreview(
                            activity,
                            item
                        )
                        selectCursor = cursorPosition
                        selectItemView = cursorItemView
                        break
                    }
                }
            }

            if (startNewPreview) {
                lastPreViewItemView?.stopPreview()
                if(lastPreviewPosition != -1){
                    ((recyclerView.adapter as? VideoListAdapter)?.items?.get(lastPreviewPosition) as? VideoFile)?.isPreviewing =
                        false
                }
                lastPreViewItemView = selectItemView
                lastPreviewPosition = selectCursor
            }
        }
    }

    fun pause(recyclerView: RecyclerView) {
        (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
            layoutManager.findViewByPosition(lastPreviewPosition)
                ?.let { itemView ->
                    (itemView as? FeedVideoCard)?.stopPreview()
                }
        }
    }

    fun resume(recyclerView: RecyclerView, activity: Activity) {
        handleLiveAutoPreview(recyclerView, activity)
    }
}
