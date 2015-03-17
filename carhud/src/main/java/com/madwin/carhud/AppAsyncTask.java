package com.madwin.carhud;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppAsyncTask extends AsyncTask<Long, String, Long> {

    private final static String TAG = "com.madwin.carhud.AppAsyncTask.java";

    private List<ResolveInfo> pkgAppsList;
    private static ArrayList<String> appsAndPackageName;
    private static ArrayList<String> appsName;
    private static ArrayList<Drawable> appIconList;
    private PackageManager pm;

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
        appsAndPackageName = new ArrayList<>();
        appsName = new ArrayList<>();
        appIconList = new ArrayList<>();
    }

    @Override
    protected Long doInBackground(Long... params) {

        for (ResolveInfo aPkgAppsList : pkgAppsList) {

            String a = aPkgAppsList.activityInfo.applicationInfo.packageName;
            try {
                PackageInfo p = pm.getPackageInfo(a, 0);
                appsName.add(p.applicationInfo.loadLabel(pm).toString());
            } catch (final PackageManager.NameNotFoundException e) {
                appsName.add("label not found");
            }

            try {
                PackageInfo p = pm.getPackageInfo(a, 0);
                a = p.applicationInfo.loadLabel(pm).toString() + "%" + a;
            } catch (final PackageManager.NameNotFoundException e) {
                a = "label not found";
            }
            appsAndPackageName.add(a);

        }

        Collections.sort(appsName, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        Collections.sort(appsAndPackageName, new Comparator<String>() {
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        try {
            for (Object aAppIcons : appsAndPackageName) {

                String app = aAppIcons.toString();
                String app_package = app.substring(app.indexOf("%") + 1, app.length());
                Drawable app_icon = mContext.getPackageManager().getApplicationIcon(app_package);
                appIconList.add(app_icon);

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}