package com.madwin.carhud.notifications;

import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;

public class NLService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (!mExcludedApps(sbn)) {

            Message msg = new Message();
            msg.obj = sbn;
                switch (sbn.getPackageName()) {
                    case "com.pandora.android":
                        MainActivity.getPandoraHandler().sendMessage(msg);
                        return;
                    case "com.google.android.apps.maps":
                        MainActivity.getMapsHandler().sendMessage(msg);
                        return;
                    case "com.spotify.music":
                        MainActivity.getSpotifyHandler().sendMessage(msg);
                        return;
                    default:
                        MainActivity.getBaseNotificationHandler().sendMessage(msg);
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
