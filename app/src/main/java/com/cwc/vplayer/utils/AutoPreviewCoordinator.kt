package com.ss.android.buzz.feed.live

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cwc.vplayer.feed.VideoFileBinder


class AutoPreviewCoordinator(
) {

    private var lastPreviewPosition: Int = -1
//    private var lastPreViewItemView: IBuzzLiveCardContract.IView? = null

    fun handleLiveAutoPreview(recyclerView: RecyclerView, activity: Activity?) {

        fun getLivePreviewHolder(adapterPosition: Int): VideoFileBinder.ViewHolder? =
            recyclerView.layoutManager?.findViewByPosition(adapterPosition)?.let {
                recyclerView.getChildViewHolder(
                    it
                ) as? VideoFileBinder.ViewHolder
            }

        fun stopPreviewInAdapterPosition(position: Int) {
            getLivePreviewHolder(position)?.let {
                it.stopPreview()
            }
        }

        fun startPreview(
            holder: VideoFileBinder.ViewHolder?
        ) {
            holder?.startPreview(activity)
        }

        fun checkAutoPreviewToggleAndDoAction(
            recyclerView: RecyclerView,
            action: (LinearLayoutManager) -> Unit
        ) {
            (recyclerView.layoutManager as? LinearLayoutManager)?.apply {
                action(this)
            }
        }

        // 支持开关
        checkAutoPreviewToggleAndDoAction(recyclerView) { layoutManager ->
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            var startNewPreview = false
            for (cursorPosition in firstVisibleItemPosition..lastVisibleItemPosition) {
                val cursorItemView = layoutManager.findViewByPosition(cursorPosition) ?: continue
                val cursorViewHolder =
                    recyclerView.getChildViewHolder(cursorItemView) as? VideoFileBinder.ViewHolder
                        ?: continue

                if (cursorViewHolder.canPreview(recyclerView.bottom)) {
                    // HIT show live strategy!

                    val previewingPosition = lastPreviewPosition

                    if (previewingPosition != cursorPosition) {
//                        lastPreViewItemView?.stopPreView()
                        startPreview(cursorViewHolder)
                    } else {
                        startPreview(cursorViewHolder)
                    }
                    startNewPreview = true
//                    lastPreViewItemView = cursorItemView as? IBuzzLiveCardContract.IView
                    lastPreviewPosition = cursorPosition
                    break
                }
            }
            if (!startNewPreview) {
//                lastPreViewItemView?.stopPreView()
//                lastPreViewItemView = null
            }
        }
    }

    fun pause(recyclerView: RecyclerView) {
        (recyclerView.layoutManager as? LinearLayoutManager)?.let { layoutManager ->
            layoutManager.findViewByPosition(lastPreviewPosition)
                ?.let { itemView ->
                    (recyclerView.getChildViewHolder(itemView) as? VideoFileBinder.ViewHolder)?.stopPreview()
                }
        }
    }

    fun resume(recyclerView: RecyclerView, activity: Activity?) {
        handleLiveAutoPreview(recyclerView, activity)
    }
}
