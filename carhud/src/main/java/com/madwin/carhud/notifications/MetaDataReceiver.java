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

        MediaFragment mf = MainActivity.getMediaFragment();

        if (intent != null) {
            Bundle extras = intent.getExtras();

            if (extras != null) {

                if (!extras.getString("track", "").equals("") ||
                        !extras.getString("artist", "").equals("") ||
                        !extras.getString("album", "").equals("")) {
                    mf.setCurrentApplicationPackage("com.google.android.music");
                    mf.setMediaTrack(extras.getString("track", ""));
                    mf.setMediaArtist(extras.getString("artist", ""));
                    mf.setMediaAlbum(extras.getString("album", ""));
                }
            }
        }
    }
}
