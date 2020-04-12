package com.cwc.vplayer.ui.main

import android.app.Application
import android.content.ContentResolver
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cwc.vplayer.App
import com.cwc.vplayer.entity.VideoFile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(app: Application) : AndroidViewModel(app) {
    val mainTitle = MutableLiveData<String>()
    val displayFragment = MutableLiveData<Fragment>()
    val videos = MutableLiveData<MutableList<VideoFile>>()

    private val mContentResolver: ContentResolver = App.app.contentResolver
    private val formatter = SimpleDateFormat.getDateInstance()


    init {
        GlobalScope.launch {
            val v1 = scanVideoFromContentProvider()
            val v2 = scanVideoFromFileSystem()
            videos.postValue(v1);
        }
    }

    private fun scanVideoFromFileSystem() {

    }

    private fun scanVideoFromContentProvider(): MutableList<VideoFile> {
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
                if (File(location).exists() && initSize > 100) {
                    list.add(info)
                }
            } while (cursor.moveToNext())
        }
        cursor?.close()
        return list;
    }

}