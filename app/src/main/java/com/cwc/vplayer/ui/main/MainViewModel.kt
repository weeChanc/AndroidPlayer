package com.cwc.vplayer.ui.main

import android.app.Application
import android.content.ContentResolver
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
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
    val categories = MutableLiveData<List<VideoCategory>>()

    private val mContentResolver: ContentResolver = App.app.contentResolver
    private val formatter = SimpleDateFormat.getDateInstance()


    init {
        init()
    }

    fun init() {
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

            AppDataBase.INSTANCE.appDao().insertAllCategory(contentProviderCategories)


            // 删除在文件系统中不存在的文件
            var dbVideos = AppDataBase.INSTANCE.appDao().loadAllVideoFile()
            AppDataBase.INSTANCE.appDao()
                    .deleteVideoFiles(dbVideos.filter { !it.path.startsWith("/") && !File(it.path).exists() })

            dbVideos = dbVideos.filter { !it.path.startsWith("/") || File(it.path).exists() }
            // find exists
            val combineData =
                    dbVideos.toHashSet().apply { addAll(contentProviderData) }.toMutableList().sorted()
            AppDataBase.INSTANCE.appDao().insertAllVideoFile(combineData)
            val combineCategories = contentProviderCategories.toHashSet()
                    .apply { addAll(AppDataBase.INSTANCE.appDao().loadAllCategory().toMutableList()) }
                    .toMutableList().sorted()

            //test
            val set = contentProviderCategories.toHashSet()
            set.addAll(AppDataBase.INSTANCE.appDao().loadAllCategory().toMutableList())

            videos.postValue(combineData)
            categories.postValue(combineCategories)
        }

        mContentResolver.registerContentObserver(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                true,
                object :
                        ContentObserver(Handler(Looper.getMainLooper())) {
                    override fun onChange(selfChange: Boolean, uri: Uri?) {
                        super.onChange(selfChange, uri)
                        if (uri == null) return
                        val cursor = mContentResolver.query(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                arrayOf(
                                        MediaStore.Video.VideoColumns.DATA,
                                        MediaStore.Video.VideoColumns.TITLE,
                                        MediaStore.Video.VideoColumns.SIZE,
                                        MediaStore.Video.VideoColumns._ID,
                                        MediaStore.Video.VideoColumns.DURATION
                                ),
                                null, null, MediaStore.Video.VideoColumns.DATE_MODIFIED + "  desc"
                        )
                        if (cursor != null && cursor.moveToFirst() && cursor.count > 0) {
                            val location =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA))
                            val title =
                                    cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.TITLE))
                            val initSize =
                                    java.lang.Long.parseLong(
                                            cursor.getString(cursor.getColumnIndex(MediaStore.Video.VideoColumns.SIZE))
                                                    ?: "0"
                                    )
                            val duration =
                                    cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
                            val info = VideoFile(
                                    location,
                                    File(location).parentFile.absolutePath,
                                    title,
                                    duration.toLong(),
                                    initSize,
                                    File(location).lastModified()
                            )
                            Log.e("weechan", info.toString())
                            cursor.close()
                            addVideoFile(info)
                        }

                        Log.e("weechan", "$selfChange  $uri")
                    }
                })
    }

    fun updateVideo(videoFile: VideoFile): Boolean {

        val cur = videos.value
        if (cur != null) {
            cur.get(cur.indexOf(videoFile)).let {
                if (File(it.path).renameTo(File(videoFile.path))) {
                    it.title = videoFile.title
                    it.lastPlayTimeStamp = videoFile.lastPlayTimeStamp
                    it.lastModify = videoFile.lastModify
                    it.path = videoFile.path
                    GlobalScope.launch {
                        AppDataBase.INSTANCE.appDao().updateVideoFile((videoFile))
                    }
                    videos.value = cur
                    return true
                }
            }
        }
        videos.value = videos.value
        return false
    }

    fun addVideoFile(videoFile: VideoFile) {
        if (videos.value?.contains(videoFile) == true) {
            return
        }

        videos.value = videos.value?.toMutableList()?.apply {
            add(videoFile)
        }

        val category = findCategory(videoFile.categoryPath)
        if (category != null) {
            category.count = category.count + 1
        }

        GlobalScope.launch { AppDataBase.INSTANCE.appDao().insertVideoFile(videoFile) }

    }

    fun deleteVideoFile(videoFile: VideoFile) {
        val cur = videos.value?.toMutableList()
        if (cur != null) {
            cur.removeAt(cur.indexOf(videoFile))
            GlobalScope.launch {
                AppDataBase.INSTANCE.appDao().deleteVideoFiles(arrayListOf(videoFile))
                File(videoFile.path).delete()
            }
            val category = findCategory(videoFile.categoryPath)
            if (category != null) {
                category.count = category.count - 1
            }
        }
        categories.value = categories.value
        videos.value = cur
    }

    fun findCategory(categoryId: String): VideoCategory? {
        return categories.value?.find { it.path.equals(categoryId) }
    }


    private fun scanVideoFromContentProvider(): MutableList<VideoFile> {
        val list = mutableListOf<VideoFile>()
        val cursor = mContentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                        MediaStore.Video.VideoColumns.DATA,
                        MediaStore.Video.VideoColumns.TITLE,
                        MediaStore.Video.VideoColumns.SIZE,
                        MediaStore.Video.VideoColumns._ID,
                        MediaStore.Video.VideoColumns.DURATION
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
                val duration =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Video.VideoColumns.DURATION))
                val info = VideoFile(
                        location,
                        File(location).parentFile.absolutePath,
                        title,
                        duration.toLong(),
                        initSize,
                        File(location).lastModified()
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