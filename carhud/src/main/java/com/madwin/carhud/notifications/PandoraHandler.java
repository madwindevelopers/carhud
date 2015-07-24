package com.madwin.carhud.notifications;

import android.app.Notification;
import android.service.notification.StatusBarNotification;

public class PandoraHandler {

    public static CHMusic HandlePandora(StatusBarNotification sbn) {

        CHMusic chm = new CHMusic("com.pandora.android");
        chm.setTitle(sbn.getNotification().extras.get(
                Notification.EXTRA_TITLE).toString());
        chm.setArtist(sbn.getNotification().extras.get(
                Notification.EXTRA_TEXT).toString());
        chm.setAlbum("");

        return chm;
    }
}
