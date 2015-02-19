package com.madwin.carhud.notifications;

import android.app.Notification;
import android.os.Handler;
import android.os.Message;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.fragments.MediaFragment;

public class PandoraHandler extends Handler {

    public static void HandlePandora(StatusBarNotification sbn) {

        MediaFragment mf = MainActivity.getMediaFragment();

        mf.setCurrentApplicationPackage("com.pandora.android");
        mf.setMediaTrack(sbn.getNotification().extras.get(
                Notification.EXTRA_TITLE).toString());
        mf.setMediaArtist(sbn.getNotification().extras.get(
                Notification.EXTRA_TEXT).toString());

    }

    @Override
    public void handleMessage(Message msg) {
        HandlePandora((StatusBarNotification) msg.obj);
    }
}
