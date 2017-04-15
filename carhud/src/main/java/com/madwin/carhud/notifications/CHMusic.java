package com.madwin.carhud.notifications;

import android.graphics.Bitmap;

/**
 * Created by andrew on 7/24/15.
 */
public class CHMusic {

    private String appName;
    private String title;
    private String artist;
    private String album;
    private Bitmap albumArt;

    public CHMusic(String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Bitmap getAlbumArt() { return albumArt; }

    public void setAlbumArt(Bitmap albumArt) { this.albumArt = albumArt; }

    public boolean equals(CHMusic chMusic) {
        if (
            chMusic == null ||
            this.appName == null ||
            this.title == null ||
            this.artist == null ) return false;
        return (this.appName.equals(chMusic.appName) &&
                this.title.equals(chMusic.title) &&
                this.artist.equals(chMusic.artist));
    }
}
