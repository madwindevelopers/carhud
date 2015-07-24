package com.madwin.carhud.notifications;

import android.os.Message;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.madwin.carhud.MainActivity;

import java.util.ArrayList;

public class NLService extends NotificationListenerService {

    private String TAG = this.getClass().getSimpleName();
    private static ArrayList<CHNotifListener> chNotifListeners =
            new ArrayList<CHNotifListener>();


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if (MainActivity.isRunning()) {

            if (!mExcludedApps(sbn)) {

                Message msg = new Message();
                msg.obj = sbn;
                switch (sbn.getPackageName()) {
                    case "com.pandora.android":
                        fireMusic(PandoraHandler.HandlePandora(sbn));
                        return;
                    case "com.google.android.apps.maps":
                        fireNotification(MapsHandler.HandleMap(sbn));
                        return;
                    case "com.spotify.music":
                        fireMusic(SpotifyHandler.HandleSpotify(sbn));
                        return;
                    default:
                        fireNotification(BaseNotificationHandler.HandleNotification(sbn));
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}

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

    public static void addNotifListener(CHNotifListener chnl) {
        chNotifListeners.add(chnl);
    }

    public interface CHNotifListener {
        void onNotificationPosted(CHNotification chNotification);
        void onMusicPosted(CHMusic chMusic);
    }

    public static void fireMusic(CHMusic chMusic) {
        for (CHNotifListener chn : chNotifListeners) {
            chn.onMusicPosted(chMusic);
        }
    }
    private void fireNotification(CHNotification chNotification) {
        for (CHNotifListener chn : chNotifListeners) {
            chn.onNotificationPosted(chNotification);
        }
    }
}
