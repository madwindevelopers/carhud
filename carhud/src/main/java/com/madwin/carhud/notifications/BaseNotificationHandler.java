package com.madwin.carhud.notifications;

import android.app.Notification;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.debug.DebuggingHandler;

/**
 * Created by Andrew on 5/28/2014.
 */
public class BaseNotificationHandler {

    private static final String TAG = "BaseNotificationHandler";
    public static void HandleNotification(StatusBarNotification sbn) {

        Log.e(TAG, "Entered BaseHandler");

        String packagelabel = "";
        PackageManager pm = MainActivity.getAppContext().getPackageManager();
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

        extras.putString("notificationtype", "notification");
        extras.putString("packagename", sbn.getPackageName());
        extras.putString("packagelabel", packagelabel);
        extras.putString("notificationtitle", sbn.getNotification().extras.get(
                Notification.EXTRA_TITLE).toString());
        extras.putString("notificationtext", sbn.getNotification().extras.get(
                Notification.EXTRA_TEXT).toString());
        extras.putString("notificationsubtext", sbn.getNotification().extras.get(
                Notification.EXTRA_SUB_TEXT).toString());

        Intent intent = new Intent("com.madwin.carhud.NOTIFICATION_LISTENER");
        intent.putExtras(extras);
        MainActivity.getAppContext().sendBroadcast(intent);
    }
}