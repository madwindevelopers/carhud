package com.madwin.carhud.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class MetaDataReceiver extends BroadcastReceiver {
    private static final String TAG = "MetaDataReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        CHMusic chm = new CHMusic("com.google.android.music");

        if (intent != null) {
            Bundle extras = intent.getExtras();

            if (extras != null) {

                if (!extras.getString("track", "").equals("") ||
                        !extras.getString("artist", "").equals("") ||
                        !extras.getString("album", "").equals("")) {

                    chm.setTitle(extras.getString("track", ""));
                    chm.setArtist(extras.getString("artist", ""));
                    chm.setAlbum(extras.getString("album", ""));
                }
            }
        }
        NLService.fireMusic(chm);
    }
}
