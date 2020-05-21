package com.cwc.vplayer.ui.like

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import com.cwc.vplayer.ui.feed.VideoListViewModel
import com.cwc.vplayer.ui.main.MainViewModel
import com.cwc.vplayer.utils.observe

class LikeViewModel(app: Application) : VideoListViewModel(app) {

    override fun init(mainViewModel: MainViewModel, dir: String?, lifecycleOwner: LifecycleOwner) {
        mainViewModel.videos.observe(lifecycleOwner) {
            videoFiles.value = it.filter {
                it.isLike
            }
        }
    }
}