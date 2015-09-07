package com.madwin.carhud.notifications;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

public class BaseNotificationHandler {

    public static CHNotification HandleNotification(StatusBarNotification sbn) {

        CHNotification chn = new CHNotification(sbn.getPackageName()); {
            if (sbn.getNotification().extras.get(Notification.EXTRA_TITLE) != null) {

                chn.setTitle(sbn.getNotification().extras.get(
                        Notification.EXTRA_TITLE).toString());
            } else {
                chn.setTitle("");
            }
            if (sbn.getNotification().extras.get(Notification.EXTRA_TEXT) != null) {
                chn.setText(sbn.getNotification().extras.get(
                        Notification.EXTRA_TEXT).toString());
            } else {
                chn.setTitle("");
            }
            if (sbn.getNotification().extras.get(Notification.EXTRA_SUB_TEXT) != null) {
                chn.setSubtext(sbn.getNotification().extras.get(
                        Notification.EXTRA_SUB_TEXT).toString());
            } else {
                chn.setSubtext("");
            }
        }
        return chn;

    }

}
