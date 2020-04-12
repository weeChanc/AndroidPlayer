package com.cwc.vplayer.entity

import java.io.File

data class VideoCategory(
    val name: String,
    val count: Int,
    val dir: File,
    val videoFile: List<VideoFile>
)