package com.madwin.carhud.notifications;

import android.os.Handler;
import android.os.Message;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.fragments.MediaFragment;

public class SpotifyHandler extends Handler{

    public static void HandleSpotify(StatusBarNotification sbn) {

        MediaFragment mf = MainActivity.getMediaFragment();

        mf.setCurrentApplicationPackage(sbn.getPackageName());
        mf.setMediaTrack(mFormatSpotifyArtist(sbn.getNotification().tickerText.toString()));
        mf.setMediaArtist(sbn.getNotification().extras.getString("android.title"));
        mf.setMediaAlbum("");

    }

    public static String mFormatSpotifyArtist(String tickerText) {
        return tickerText.substring(tickerText.indexOf("—") + 2);
    }

    @Override
    public void handleMessage(Message msg) {
        HandleSpotify((StatusBarNotification) msg.obj);
    }
}
