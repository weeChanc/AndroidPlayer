package com.cwc.vplayer.feed

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.cwc.vplayer.base.utils.observe
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.ui.main.MainViewModel
import java.io.File

class VideoListViewModel(app: Application) : AndroidViewModel(app) {
    val videoFiles = MutableLiveData<List<VideoFile>>()

    fun init(mainViewModel : MainViewModel,dir : String?,lifecycleOwner: LifecycleOwner){
        mainViewModel.videos.observe(lifecycleOwner){
            videoFiles.value =  it.filter {
                it.categoryPath.equals(dir)
            }
        }
    }
}