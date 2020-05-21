package com.cwc.vplayer.entity.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.cwc.vplayer.App;
import com.cwc.vplayer.entity.VideoCategory;
import com.cwc.vplayer.entity.VideoFile;
import com.cwc.vplayer.entity.db.AppDao;

@Database(entities = {VideoCategory.class, VideoFile.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {
    public abstract AppDao appDao();

    public static AppDataBase INSTANCE = Room.databaseBuilder(App.app, AppDataBase.class, "app_data_base").fallbackToDestructiveMigration().build();
}