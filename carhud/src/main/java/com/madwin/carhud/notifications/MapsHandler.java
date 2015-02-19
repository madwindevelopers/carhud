package com.madwin.carhud.notifications;

import android.app.Notification;
import android.os.Handler;
import android.os.Message;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.fragments.NotificationFragment;

public class MapsHandler extends Handler {

    public static void HandleMaps(StatusBarNotification sbn) {

        NotificationFragment nf = MainActivity.getNotificationFragment();

        nf.setCurrentApplication("com.google.android.apps.maps");
        nf.setNotificationTitle("Maps");
        nf.setNotificationText(sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString());

    }

    @Override
    public void handleMessage(Message msg) {
        HandleMaps((StatusBarNotification) msg.obj);


    }
}
