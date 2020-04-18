package com.cwc.vplayer.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

@Entity(tableName = "video_category")
public class VideoCategory implements Comparable<VideoCategory> {
    // 数据库字段
    @PrimaryKey
    @NotNull
    String path;
    String name;
    int count;
    String cover;

    // 业务字段
    @Ignore
    List<VideoFile> videoFile;

    @NotNull
    public String getPath() {
        return path;
    }

    public void setPath(@NotNull String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public List<VideoFile> getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(List<VideoFile> videoFile) {
        this.videoFile = videoFile;
    }

    public VideoCategory() {
    }

    public VideoCategory(@NotNull String path, String name, int count, String cover, List<VideoFile> videoFile) {
        this.path = path;
        this.name = name;
        this.count = count;
        this.cover = cover;
        this.videoFile = videoFile;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof VideoCategory){
            return path.equals(((VideoCategory) obj).path);
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public int compareTo(VideoCategory o) {
       return path.compareTo(o.path);
    }

    @NonNull
    @Override
    public String toString() {
        return "VideoCategory: path" + path;
    }
}
