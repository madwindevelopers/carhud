package com.madwin.carhud.notifications;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;

/**
 * Created by Andrew on 5/28/2014.
 */
public class MapsHandler {

    public static void HandleMaps(StatusBarNotification sbn) {

        Bundle extras = new Bundle();

        extras.putString("notificationtype", "notification");
        extras.putString("packagename", "com.google.android.apps.maps");
        extras.putString("packagelabel", "Maps");
        extras.putString("notificationtitle", sbn.getNotification().extras.get(Notification.EXTRA_TITLE).toString());
        extras.putString("notificationtext", sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString());

        Intent intent = new Intent("com.madwin.carhud.NOTIFICATION_LISTENER");
        intent.putExtras(extras);
        MainActivity.getAppContext().sendBroadcast(intent);
    }

}
