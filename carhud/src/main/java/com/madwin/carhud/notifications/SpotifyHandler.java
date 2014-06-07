package com.madwin.carhud.notifications;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.madwin.carhud.MainActivity;

/**
 * Created by Andrew on 5/28/2014.
 */
public class SpotifyHandler {

    private static final String TAG = "SpotifyHandler";

    public static void HandleSpotify(StatusBarNotification sbn) {

        Log.d(TAG, "Spotify Handler initiated");

        Bundle extra = sbn.getNotification().extras;

        String packagelabel = "";
        PackageManager pm = MainActivity.getAppContext().getPackageManager();
        assert pm != null;
        try {
            ApplicationInfo ai = pm.getApplicationInfo(sbn.getPackageName(), 0);
            if (ai.loadLabel(pm) != null) {
                packagelabel = ai.loadLabel(pm).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "Unable to get packagelabel in BaseNotificationHandler");
            e.printStackTrace();
        }

        Bundle extras = new Bundle();

        extras.putString("notificationtype", "music");
        extras.putString("packagename", sbn.getPackageName());
        extras.putString("packagelabel", packagelabel);
        extras.putString("songtitle", mFormatSpotifyArtist(
                sbn.getNotification().tickerText.toString()));
        extras.putString("songartist", sbn.getNotification().extras.getString("android.title"));
        extras.putString("songalbum", "");

        Intent intent = new Intent("com.madwin.carhud.NOTIFICATION_LISTENER");
        intent.putExtras(extras);
        MainActivity.getAppContext().sendBroadcast(intent);

    }

    public static String mFormatSpotifyArtist(String tickerText) {
        return tickerText.substring(tickerText.indexOf("—") + 2);
    }
}
