package com.madwin.carhud.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.fragments.MediaFragment;

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

                if (!extras.getString("track", "").equals("") ||
                        !extras.getString("artist", "").equals("") ||
                        !extras.getString("album", "").equals("")) {
                    extra2.putString(MediaFragment.PACKAGE_NAME, "com.google.android.music");
                    extra2.putString(MediaFragment.PACKAGE_LABEL, "Play Music");
                    extra2.putString(MediaFragment.SONG_NAME, extras.getString("track", ""));
                    extra2.putString(MediaFragment.ARTIST, extras.getString("artist", ""));
                    extra2.putString(MediaFragment.ALBUM, extras.getString("album", ""));

                    Intent i = new Intent(MediaFragment.MEDIA_INTENT);
                    i.putExtras(extra2);
                    MainActivity.getAppContext().sendBroadcast(i);
                }
            }
        }
    }
}
