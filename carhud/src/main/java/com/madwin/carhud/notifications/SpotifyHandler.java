package com.madwin.carhud.notifications;

import android.service.notification.StatusBarNotification;

public class SpotifyHandler {

    public static CHMusic HandleSpotify(StatusBarNotification sbn) {

        CHMusic chm = new CHMusic(sbn.getPackageName());
        chm.setTitle(mFormatSpotifyArtist(sbn.getNotification().tickerText.toString()));
        chm.setArtist(sbn.getNotification().extras.getString("android.title"));
        chm.setAlbum("");
        return chm;

    }

    private static String mFormatSpotifyArtist(String tickerText) {
        return tickerText.substring(tickerText.indexOf("—") + 2);
    }
}
