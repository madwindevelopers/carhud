package com.madwin.carhud.notifications;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.fragments.NotificationFragment;

public class MapsHandler {

    public static void HandleMaps(StatusBarNotification sbn) {

        Bundle extras = new Bundle();

//        extras.putString("notificationtype", "notification");
        extras.putString(NotificationFragment.PACKAGE_NAME,
                "com.google.android.apps.maps");
        extras.putString(NotificationFragment.PACKAGE_LABEL,"Maps");
        extras.putString(NotificationFragment.TITLE, sbn.getNotification().extras.
                get(Notification.EXTRA_TITLE).toString());
        extras.putString(NotificationFragment.TEXT, sbn.getNotification().extras.
                get(Notification.EXTRA_TEXT).toString());


        Intent intent = new Intent(NotificationFragment.NOTIFICATION_INTENT);
        intent.putExtras(extras);
        MainActivity.getAppContext().sendBroadcast(intent);
    }

}
