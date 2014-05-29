package com.madwin.carhud.notifications;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;

/**
 * Created by Andrew on 5/28/2014.
 */
public class PandoraHandler {

    public static void HandlePandora(StatusBarNotification sbn) {
        Bundle extras = new Bundle();

        extras.putString("notificationtype", "music");
        extras.putString("packagename", "com.pandora.android");
        extras.putString("packagelabel", "Pandora");
        extras.putString("songtitle", sbn.getNotification().extras.get(Notification.EXTRA_TITLE).toString());
        extras.putString("songartist", sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString());
        extras.putString("songalbum", "");

        Intent intent = new Intent("com.madwin.carhud.NOTIFICATION_LISTENER");
        intent.putExtras(extras);
        MainActivity.getAppContext().sendBroadcast(intent);
    }
}
