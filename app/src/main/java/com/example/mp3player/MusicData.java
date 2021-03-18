package com.example.mp3player;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class MusicData {
    private String id;
    private String artist;
    private String title;
    private String albumArt;
    private String duration;
    private int playCount;
    private boolean favor;

    public MusicData(String id, String artist, String title, String albumArt, String duration, int playCount, boolean favor) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.albumArt = albumArt;
        this.duration = duration;
        this.playCount = playCount;
        this.favor = favor;
    }

    public String getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbumArt() {
        return albumArt;
    }

    public String getDuration() {
        return duration;
    }

    public int getPlayCount() {
        return playCount;
    }

    public boolean isFavor() {
        return favor;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbumArt(String albumArt) {
        this.albumArt = albumArt;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public void setFavor(boolean favor) {
        this.favor = favor;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicData musicData = (MusicData) o;
        return Objects.equals(id, musicData.id);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
