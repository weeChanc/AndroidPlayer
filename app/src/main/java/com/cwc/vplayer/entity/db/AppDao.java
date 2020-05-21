package com.cwc.vplayer.entity.db;


import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import com.cwc.vplayer.App;
import com.cwc.vplayer.entity.VideoCategory;
import com.cwc.vplayer.entity.VideoFile;

import java.util.List;

@Dao
public interface AppDao {

    @Query("SELECT * FROM video_file")
    List<VideoFile> loadAllVideoFile();

    @Query("SELECT * FROM video_category")
    List<VideoCategory> loadAllCategory();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Long insertVideoFile(VideoFile video);

    @Delete
    void deleteVideoFiles(List<VideoFile> video);

    @Update
    int updateVideoFile(VideoFile video);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertAllVideoFile(List<VideoFile> video);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertCategory(VideoCategory category);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    List<Long> insertAllCategory(List<VideoCategory> category);
}
