package com.madwin.carhud.notifications;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.fragments.NotificationFragment;

/**
 * Created by Andrew on 5/28/2014.
 */
public class BaseNotificationHandler {

    private static final String TAG = "BaseNotificationHandler";
    public static void HandleNotification(StatusBarNotification sbn) {

        Log.e(TAG, "Entered BaseHandler");
        if (MainActivity.getAppContext().getPackageManager() != null && MainActivity.activityRunning) {
            String packagelabel = "";
            PackageManager pm = MainActivity.getAppContext().getPackageManager();
            Log.d(TAG, "Notification package name = " + sbn.getPackageName());
            assert pm != null;
            try {
                ApplicationInfo ai = pm.getApplicationInfo(sbn.getPackageName(), 0);
                if (ai.loadLabel(pm) != null) {
                    packagelabel = ai.loadLabel(pm).toString();
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.d(TAG, "Unable to get packagelabel in BaseNotificationHandler");
                e.printStackTrace();
            }

            Bundle extras = new Bundle();

            //extras.putString("notificationtype", "notification");
            extras.putString(NotificationFragment.PACKAGE_NAME, sbn.getPackageName());
            extras.putString(NotificationFragment.PACKAGE_LABEL, packagelabel);

            if (sbn.getNotification().extras.get(Notification.EXTRA_TITLE) != null) {
                extras.putString(NotificationFragment.TITLE, sbn.getNotification().extras.get(
                        Notification.EXTRA_TITLE).toString());
            } else {
                extras.putString(NotificationFragment.TITLE, "");
            }
            if (sbn.getNotification().extras.get(Notification.EXTRA_TEXT) != null) {
                extras.putString(NotificationFragment.TEXT, sbn.getNotification().extras.get(
                        Notification.EXTRA_TEXT).toString());
            } else {
                extras.putString(NotificationFragment.TEXT, "");
            }
            if (sbn.getNotification().extras.get(Notification.EXTRA_SUB_TEXT) != null) {
                extras.putString(NotificationFragment.SUBTEXT, sbn.getNotification().extras.get(
                        Notification.EXTRA_SUB_TEXT).toString());
            } else {
                extras.putString(NotificationFragment.SUBTEXT, "");
            }

            Intent intent = new Intent(NotificationFragment.NOTIFICATION_INTENT);
            intent.putExtras(extras);
            MainActivity.getAppContext().sendBroadcast(intent);
        }
    }
}
