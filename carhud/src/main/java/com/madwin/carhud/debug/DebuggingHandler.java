package com.madwin.carhud.debug;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public class DebuggingHandler {

    public static void dumpIntent(Intent i) {
        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e("carhud", "******Dumping Intent start********");
            while (it.hasNext()) {
                String key = it.next();
                Log.e("carhud", "[" + key + " = " + bundle.get(key) + "]");
            }
            Log.e("carhud", "******Dumping intent ended*******");
        }
    }

    public static void dumpSbn(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;
        Log.d("carhud", "***************Notification*****************" +
                "\nTickerText : " + sbn.getNotification().tickerText +
                "\nLargeIcon : " + sbn.getNotification().largeIcon +
                "\nIcon : " + sbn.getNotification().icon +
                "\nIconLevel : " + sbn.getNotification().iconLevel +
                "\nPACKAGE_NAME : " + sbn.getPackageName() +
                "\nEXTRA_INFO_TEXT : " + extras.getCharSequence(Notification.EXTRA_INFO_TEXT) +
                "\nEXTRA_LARGE_ICON : " + extras.getInt(Notification.EXTRA_LARGE_ICON) +
                "\nEXTRA_LARGE_ICON_BIG : " + extras.getInt(Notification.EXTRA_LARGE_ICON_BIG) +
                "\nEXTRA_PEOPLE : " + extras.getCharSequence(Notification.EXTRA_PEOPLE) +
                "\nEXTRA_PICTURE : " + extras.getInt(Notification.EXTRA_PICTURE) +
                "\nEXTRA_PROGRESS : " + extras.getInt(Notification.EXTRA_PROGRESS) +
                "\nEXTRA_SMALL_ICON : " + extras.getInt(Notification.EXTRA_SMALL_ICON) +
                "\nEXTRA_SUB_TEXT : " + extras.getCharSequence(Notification.EXTRA_SUB_TEXT) +
                "\nEXTRA_SUMMARY_TEXT : " + extras.getCharSequence(Notification.EXTRA_SUMMARY_TEXT) +
                "\nEXTRA_TEXT : " + extras.getCharSequence(Notification.EXTRA_TEXT) +
                "\nEXTRA_TEXT_LINES : " + extras.getCharSequence(Notification.EXTRA_TEXT_LINES) +
                "\nEXTRA_TITLE : " + extras.getCharSequence(Notification.EXTRA_TITLE) +
                "\nEXTRA_TITLE_BIG : " + extras.getCharSequence(Notification.EXTRA_TITLE_BIG));
    }

}
