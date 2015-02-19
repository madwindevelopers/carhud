package com.madwin.carhud.notifications;

import android.app.Notification;
import android.os.Handler;
import android.os.Message;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.fragments.NotificationFragment;

public class BaseNotificationHandler extends Handler {

    public static void HandleNotification(StatusBarNotification sbn) {

        NotificationFragment nf = MainActivity.getNotificationFragment();

        nf.setCurrentApplicationPackage(sbn.getPackageName());

        if (sbn.getNotification().extras.get(Notification.EXTRA_TITLE) != null) {
            nf.setNotificationTitle(sbn.getNotification().extras.get(
                    Notification.EXTRA_TITLE).toString());
        } else {
            nf.setNotificationTitle("");
        }
        if (sbn.getNotification().extras.get(Notification.EXTRA_TEXT) != null) {
            nf.setNotificationText(sbn.getNotification().extras.get(
                    Notification.EXTRA_TEXT).toString());
        } else {
            nf.setNotificationText("");
        }
        if (sbn.getNotification().extras.get(Notification.EXTRA_SUB_TEXT) != null) {
            nf.setNotificationSubText(sbn.getNotification().extras.get(
                    Notification.EXTRA_SUB_TEXT).toString());
        } else {
            nf.setNotificationSubText("");
        }
    }

    @Override
    public void handleMessage(Message msg) {
        HandleNotification((StatusBarNotification) msg.obj);
    }
}
