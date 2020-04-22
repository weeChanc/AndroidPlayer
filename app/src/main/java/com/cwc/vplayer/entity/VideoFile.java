package com.cwc.vplayer.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.cwc.vplayer.base.utils.MediaUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "video_file", foreignKeys = @ForeignKey(entity = VideoCategory.class, parentColumns = "path", childColumns = "categoryPath", onDelete = CASCADE))
public class VideoFile implements Comparable<VideoFile> {
    @PrimaryKey
    @NotNull
    String path;
    String categoryPath;
    String title;
    long duration;
    long size;
    long lastModify;
    long seek = 0;
    long lastPlayTimeStamp = 0;
    boolean like = false;
    //  功能字段
    @Ignore
    boolean isPreviewing;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof VideoFile) {
            return path.equals(((VideoFile) obj).path);
        } else {
            return false;
        }
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public VideoFile(@NotNull String path, String categoryPath, String title, long duration, long size, long lastModify) {
        this.path = path;
        this.categoryPath = categoryPath;
        this.title = title;
        this.duration = duration;
        this.size = size;
        this.lastModify = lastModify;
    }

    public long getSeek() {
        return seek;
    }

    public void setSeek(long seek) {
        this.seek = seek;
    }

    public long getLastPlayTimeStamp() {
        return lastPlayTimeStamp;
    }

    public void setLastPlayTimeStamp(long lastPlayTimeStamp) {
        this.lastPlayTimeStamp = lastPlayTimeStamp;
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

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    public boolean isPreviewing() {
        return isPreviewing;
    }

    public void setPreviewing(boolean previewing) {
        isPreviewing = previewing;
    }

    public static VideoFile createFromFile(File file) {
        {
            if (MediaUtils.INSTANCE.isVideo(file.getAbsolutePath())) {
                return new VideoFile(
                        file.getAbsolutePath(),
                        file.getParentFile().getAbsolutePath(),
                        file.getName(),
                        0,
                        file.length(),
                        file.lastModified()
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

    @NonNull
    @Override
    public String toString() {
        return "VideoFile: Path:" + path + ", lastPlayTimeStamp:" + lastPlayTimeStamp + "\n";
    }
}
