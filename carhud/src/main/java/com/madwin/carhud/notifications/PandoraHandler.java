package com.madwin.carhud.notifications;

import android.service.notification.StatusBarNotification;


public class PandoraHandler {

    static CHMusic HandlePandora(StatusBarNotification sbn, ParsedNotification parsedNotification) {

        CHMusic chm = new CHMusic("com.pandora.android");
        chm.setTitle(parsedNotification.getText(0));
        chm.setArtist(parsedNotification.getText(1));
        chm.setAlbum("");
        chm.setAlbumArt(parsedNotification.getBitmap(0));
        
        return chm;
    }

}
