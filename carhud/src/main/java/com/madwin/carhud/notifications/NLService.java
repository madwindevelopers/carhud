package com.madwin.carhud.notifications;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

public class NLService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
       // Log.i(TAG, "onNotificationPosted");
       // Log.i(TAG, "Notification package name = " + sbn.getPackageName());

        if (!mExcludedApps(sbn)) {
                switch (sbn.getPackageName()) {
                    case "com.pandora.android":
                        PandoraHandler.HandlePandora(sbn);
                        return;
                    case "com.google.android.apps.maps":
                        MapsHandler.HandleMaps(sbn);
                        return;
                    case "com.spotify.music":
                        SpotifyHandler.HandleSpotify(sbn);
                        return;
                    default:
                    //    Log.d(TAG, "entering nlservice base handler");
                        BaseNotificationHandler.HandleNotification(sbn);
            }
        }

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }

    public Boolean mExcludedApps(StatusBarNotification sbn2) {
     /*   Log.e(TAG, "mExcludeApps value = " +
                (sbn2.getPackageName().equals("com.google.android.music") ||
                sbn2.getPackageName().equals("com.quoord.tapatalkHD") ||
                sbn2.getPackageName().equals("com.aws.android.elite")));*/
        return sbn2.getPackageName().equals("com.google.android.music") ||
                sbn2.getPackageName().equals("com.quoord.tapatalkHD") ||
                sbn2.getPackageName().equals("com.aws.android.elite") ||
                sbn2.getPackageName().equals("com.android.systemui");
    }

}
