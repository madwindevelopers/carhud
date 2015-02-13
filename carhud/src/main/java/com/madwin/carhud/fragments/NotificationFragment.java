package com.madwin.carhud.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.madwin.carhud.R;
import com.madwin.carhud.utils.RoundAppIcon;

public class NotificationFragment extends Fragment{

    private String currentApplicationPackage = "com.madwin.carhud";
    private String applicationName;
    private String notificationTitle;
    private String notificationText;
    private String notificationSubText;
    private ImageView notificationIV;
    private Drawable appIcon;

    private TextView applicationNameTV;
    private TextView notificationTitleTV;
    private TextView notificationTextTV;
    private TextView notificationSubTextTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        Log.d("carhud", "top notification loaded");

        View view = inflater.inflate(R.layout.notification_fragment,
                container, false)
;
        applicationNameTV = (TextView) view.findViewById(R.id.nt_package);
        notificationTitleTV = (TextView) view.findViewById(R.id.nt_title);
        notificationTextTV = (TextView) view.findViewById(R.id.nt_text);
        notificationSubTextTV = (TextView) view.findViewById(R.id.nt_subtext);
        notificationIV = (ImageView) view.findViewById(R.id.notification_app_icon);
        setNotificationAppIcon();

    	return view;

	}

    public void openApplication() {
        Intent intent;
        PackageManager manager = getActivity().getPackageManager();
        try {
            intent = manager.getLaunchIntentForPackage(getCurrentApplicationPackage());
            if (intent == null)
                throw new PackageManager.NameNotFoundException();
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(getActivity(), "package name not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void setNotificationAppIcon() {
        try {
            appIcon = getActivity().getPackageManager().getApplicationIcon(getCurrentApplicationPackage());
            notificationIV.setImageDrawable(new RoundAppIcon(appIcon));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            notificationIV.setImageDrawable(getResources().getDrawable(android.R.drawable.stat_notify_error));
        }
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        applicationNameTV.setText(this.applicationName);
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
        notificationTitleTV.setText(this.notificationTitle);
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
        notificationTextTV.setText(this.notificationText);
    }

    public String getNotificationSubText() {
        return notificationSubText;
    }

    public void setNotificationSubText(String notificationSubText) {
        this.notificationSubText = notificationSubText;
        notificationSubTextTV.setText(notificationSubText);
    }

    public String getCurrentApplicationPackage() {
        return currentApplicationPackage;
    }

    public void setCurrentApplication(String currentApplicationPackage) {
        this.currentApplicationPackage = currentApplicationPackage;
        try {
            setApplicationName(getActivity().getPackageManager().getApplicationLabel(
                    getActivity().getPackageManager().getApplicationInfo(this.currentApplicationPackage, 0)).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setNotificationAppIcon();

    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }
}

