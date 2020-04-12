package com.cwc.vplayer.entity

import com.cwc.vplayer.base.utils.MediaUtils
import java.io.File

data class VideoFile(

    //数据字段
    val title: String,
    val size: Long,
    val path: String,
    val lastModify: String,
    //  功能字段

    var isPreviewing: Boolean = false
) {

    companion object {
        fun createFromFile(file: File): VideoFile? {
            if (MediaUtils.isVideo(file.absolutePath)) {
                return VideoFile(
                    file.name,
                    file.length(),
                    file.absolutePath,
                    file.lastModified().toString()
                )
            } else {
                return null
            }
        }
    }
}
