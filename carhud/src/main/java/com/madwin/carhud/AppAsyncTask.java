package com.madwin.carhud;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppAsyncTask extends AsyncTask<Long, String, Long> {

    List pkgAppsList;
    public static ArrayList<String> apps_and_package_name;
    public static ArrayList<String> apps_name;
    public static ArrayList<Drawable> app_icon_list;
    PackageManager pm;

    private Context mContext;

    public AppAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pm = mContext.getApplicationContext().getPackageManager();
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppsList = pm.queryIntentActivities(mainIntent, 0);
        apps_and_package_name = new ArrayList<String>();
        apps_name = new ArrayList<String>();
        app_icon_list = new ArrayList<Drawable>();
    }

    @Override
    protected Long doInBackground(Long... params) {


        for (Object aPkgAppsList : pkgAppsList) {
            String a = aPkgAppsList.toString();
            a = a.substring(21, a.indexOf("/"));
            try {
                PackageInfo p = pm.getPackageInfo(a, 0);
                apps_name.add(p.applicationInfo.loadLabel(pm).toString());
            } catch (final PackageManager.NameNotFoundException e) {
                apps_name.add("label not found");
            }

            try {
                PackageInfo p = pm.getPackageInfo(a, 0);
                a = p.applicationInfo.loadLabel(pm).toString() + "%" + a;
            } catch (final PackageManager.NameNotFoundException e) {
                a = "label not found";
            }
            apps_and_package_name.add(a);

        }

        Collections.sort(apps_name, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        Collections.sort(apps_and_package_name, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        try {
            for (Object aAppIcons : apps_and_package_name) {
                String app = aAppIcons.toString();
                String app_package = app.substring(app.indexOf("%") + 1, app.length());
                Drawable app_icon = mContext.getPackageManager().getApplicationIcon(app_package);
                app_icon_list.add(app_icon);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}