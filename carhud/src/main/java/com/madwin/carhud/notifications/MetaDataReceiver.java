package com.madwin.carhud.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.madwin.carhud.MainActivity;

public class MetaDataReceiver extends BroadcastReceiver {
    private static final String TAG = "MetaDataReceiver";

    public MetaDataReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extra2 = new Bundle();

        if (intent != null) {
            Bundle extras = intent.getExtras();

            if (extras != null) {

                extra2.putString("notificationtype", "music");
                extra2.putString("packagename", "com.google.android.music");
                extra2.putString("packagelabel", "Play Music");
                extra2.putString("songtitle", extras.getString("track", "no track"));
                extra2.putString("songartist", extras.getString("artist", "no artist"));
                extra2.putString("songalbum", extras.getString("album", "no album"));
                extra2.putLong("albumId", extras.getLong("albumId"));

                Intent i = new Intent("com.madwin.carhud.NOTIFICATION_LISTENER");
                i.putExtras(extra2);
                MainActivity.getAppContext().sendBroadcast(i);
            }
        }
    }
}
