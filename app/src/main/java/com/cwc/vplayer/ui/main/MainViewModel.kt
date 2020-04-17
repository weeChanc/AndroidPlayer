package com.cwc.vplayer.ui.main

import android.app.Application
import android.content.ContentResolver
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.cwc.vplayer.App
import com.cwc.vplayer.entity.VideoCategory
import com.cwc.vplayer.entity.VideoFile
import com.cwc.vplayer.entity.db.AppDataBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(app: Application) : AndroidViewModel(app) {
    val mainTitle = MutableLiveData<String>()
    val displayFragment = MutableLiveData<Fragment>()
    val videos = MutableLiveData<List<VideoFile>>()
    val categories =  MutableLiveData<List<VideoCategory>>()

    private val mContentResolver: ContentResolver = App.app.contentResolver
    private val formatter = SimpleDateFormat.getDateInstance()


    init {
        GlobalScope.launch {
            val contentProviderData = scanVideoFromContentProvider()
            val contentProviderCategories = transfromVideoFileToCategory(contentProviderData).map {
                File(it).let { file ->
                    val videoList =
                        file.listFiles().map { VideoFile.createFromFile(it) }.filterNotNull()
                    return@let VideoCategory(
                        file.absolutePath,
                        file.name,
                        videoList.size,
                        null,
                        videoList
                    )
                }
            }
            videos.postValue(contentProviderData)
            categories.postValue(contentProviderCategories)
            val combineData  = contentProviderData.toHashSet().apply { addAll(AppDataBase.INSTANCE.appDao().loadAllVideoFile().toMutableList()) }.toMutableList().sorted()
            val combineCategories =contentProviderCategories.toHashSet().apply { addAll(AppDataBase.INSTANCE.appDao().loadAllCategory().toMutableList()) }.toMutableList().sorted()
            categories.postValue(combineCategories)
            videos.postValue(combineData)
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
                    location,
                    File(location).parentFile.absolutePath,
                    title,
                    initSize,
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