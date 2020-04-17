package com.cwc.vplayer.entity.db

import androidx.room.*
import com.cwc.vplayer.App
import com.cwc.vplayer.entity.VideoCategory
import com.cwc.vplayer.entity.VideoFile

@Dao
interface AppDao{

    @Query("SELECT * FROM video_file")
    fun loadAllVideoFile(): List<VideoFile>

    @Query("SELECT * FROM video_category")
    fun loadAllCategory() : List<VideoCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideoFile(video: VideoFile) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllVideoFile(video: List<VideoFile>) : List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: VideoCategory):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCategory(category: List<VideoCategory>): List<Long>
}

@Database(entities = arrayOf(VideoCategory::class,VideoFile::class), version = 1,exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun appDao (): AppDao

    companion object{
        val INSTANCE = Room.databaseBuilder(
            App.app,AppDataBase::class.java,"app_data_base")
            .fallbackToDestructiveMigration()
            .build()
    }

}