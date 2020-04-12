package com.cwc.vplayer.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cwc.vplayer.entity.VideoCategory
import com.cwc.vplayer.entity.VideoFile
import java.io.File

class CategoryViewModel(app: Application) : AndroidViewModel(app) {
    val categoryList = MutableLiveData<List<VideoCategory>>()


    /**
     * 获取所有文件的parent的路径，并去重，从而得到文件夹名字
     */
    fun transfromVideoFileToCategory(videoFiles: List<VideoFile>): HashSet<String> {
        val categories = HashSet<String>();
        videoFiles.map {
            val file = File(it.path)
            val parentFile = file.parentFile
            if (file.exists() && parentFile.exists()) {
                return@map parentFile.absolutePath
            } else {
                return@map null
            }
        }.filterNotNull().forEach {
            categories.add(
                it
            )
        }
        return categories
    }
}

