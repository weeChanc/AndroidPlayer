package com.cwc.vplayer.utils

import android.content.ContentResolver
import android.media.ThumbnailUtils
import android.provider.MediaStore
import com.cwc.vplayer.App
import com.cwc.vplayer.entity.VideoFile

import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

/**
 * Created by weechan on 18-3-24.
 */

/**
 * Created by steve on 17-11-22.
 */

object MediaRepository {


    private val mContentResolver: ContentResolver = App.app.contentResolver
    private val formatter = SimpleDateFormat.getDateInstance()
    var videos: MutableList<VideoFile>? = null

    init {
        thread {
            getVideo()
        }
    }

    fun getVideo(): List<VideoFile> {
        if (videos != null) return videos?.filter { File(it.path).exists() } ?: emptyList()
        val list = mutableListOf<VideoFile>()
        val cursor = mContentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                MediaStore.Video.VideoColumns.DATA, MediaStore.Video.VideoColumns.TITLE,
                MediaStore.Video.VideoColumns.SIZE, MediaStore.Video.VideoColumns._ID
            ),
            null, null, MediaStore.Video.VideoColumns.DATE_MODIFIED + "  desc"
        )

        if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
            do {
                val location =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                val title =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE))
                val initSize =
                    java.lang.Long.parseLong(
                        cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE))
                            ?: "0"
                    )
                val info = VideoFile(
                    title,
                    initSize,
                    location,
                    formatter.format(Date(File(location).lastModified()))
                )
                if (File(location).exists() &&  initSize > 100) {
                    list.add(info)
                }
            } while (cursor.moveToNext())
        }
        cursor?.close()

        this.videos = list
        return list
    }


}


