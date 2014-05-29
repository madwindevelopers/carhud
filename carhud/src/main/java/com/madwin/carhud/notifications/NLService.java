package com.madwin.carhud.notifications;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NLService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i(TAG, "onNotificationPosted");
        Log.i(TAG, "Notification package name = " + sbn.getPackageName());

        if (!mExcludedApps(sbn)) {
            if (true) {
                switch (sbn.getPackageName()) {
                    case "com.pandora.android":
                        PandoraHandler.HandlePandora(sbn);
                        return;
                    case "com.google.android.apps.maps":
                        MapsHandler.HandleMaps(sbn);
                        return;
                    case "com.spotify.mobile.android.ui":
                        SpotifyHandler.HandleSpotify(sbn);
                }
            } else {
                Log.d(TAG, "entering nlservice base handler");
                BaseNotificationHandler.HandleNotification(sbn);
            }
        }

/*
        Intent i = new  Intent("com.madwin.carhud.NOTIFICATION_LISTENER");

        Notification mNotification = sbn.getNotification();
        if (mNotification!=null){
            Bundle extras = mNotification.extras;

            i.putExtra("notification_package", sbn.getPackageName());
            i.putExtra("notification_tickerText", sbn.getNotification().tickerText);
            Bitmap bmp = sbn.getNotification().largeIcon;
            if (bmp != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                i.putExtra("notification_largeIcon", byteArray);
            }
            i.putExtras(mNotification.extras);

            sendBroadcast(i);



            Log.d(TAG, "***************Notification*****************" +
                    "\nTickerText : " + sbn.getNotification().tickerText +
                    "\nLargeIcon : " + sbn.getNotification().largeIcon +
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
                    "\nEXTRA_TITLE_BIG : " + extras.getCharSequence(Notification.EXTRA_TITLE_BIG)
            );
        }
*/
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    public Boolean mExcludedApps(StatusBarNotification sbn2) {
        Log.e(TAG, "mExcludeApps value = " +
                (sbn2.getPackageName().equals("com.google.android.music") ||
                sbn2.getPackageName().equals("com.quoord.tapatalkHD") ||
                sbn2.getPackageName().equals("com.aws.android.elite")));
        return sbn2.getPackageName().equals("com.google.android.music") ||
                sbn2.getPackageName().equals("com.quoord.tapatalkHD") ||
                sbn2.getPackageName().equals("com.aws.android.elite");
    }

}
