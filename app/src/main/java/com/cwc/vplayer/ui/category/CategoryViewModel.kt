package com.cwc.vplayer.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cwc.vplayer.entity.VideoCategory

class CategoryViewModel(app: Application) : AndroidViewModel(app) {
    val categoryList = MutableLiveData<List<VideoCategory>>()
}

