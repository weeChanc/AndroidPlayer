package com.cwc.vplayer.entity;

import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import com.cwc.vplayer.base.utils.MediaUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "video_file",foreignKeys =  @ForeignKey(entity =  VideoCategory.class,parentColumns = "path",childColumns = "path",onDelete = CASCADE))
public class VideoFile implements  Comparable<VideoFile> {
    @PrimaryKey
    @NotNull
    String path;
    String categoryPath;
    String title;
    long size;
    String lastModify;
    //  功能字段
    boolean isPreviewing;

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof VideoFile){
            return path.equals(obj);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public VideoFile(String path, String categoryPath, String title, long size, String lastModify) {
        this.path = path;
        this.categoryPath = categoryPath;
        this.title = title;
        this.size = size;
        this.lastModify = lastModify;
    }

    @NotNull
    public String getPath() {
        return path;
    }

    public void setPath(@NotNull String path) {
        this.path = path;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }

    public boolean isPreviewing() {
        return isPreviewing;
    }

    public void setPreviewing(boolean previewing) {
        isPreviewing = previewing;
    }

    public static  VideoFile createFromFile(File file) { {
        if (MediaUtils.INSTANCE.isVideo(file.getAbsolutePath())) {
            return new  VideoFile(
                    file.getAbsolutePath(),
                    file.getParentFile().getAbsolutePath(),
                    file.getName(),
                    file.length(),
                    String.valueOf(file.lastModified())
            );
        } else {
            return null;
        }
        }
    }

    @Override
    public int compareTo(VideoFile o) {
        return path.compareTo(o.path);
    }
}
