package com.madwin.carhud.notifications;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

public class MapsHandler {

    public static CHNotification HandleMap(StatusBarNotification sbn) {

        CHNotification chn = new CHNotification("com.google.android.apps.maps");
        chn.setTitle("Maps");
        chn.setText(sbn.getNotification().extras.get(Notification.EXTRA_TEXT).toString());
        chn.setSubtext("");

        return chn;

    }
}
