package com.madwin.carhud.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.madwin.carhud.R;
import com.madwin.carhud.notifications.CHMusic;
import com.madwin.carhud.notifications.CHNotification;
import com.madwin.carhud.notifications.NLService;

public class NotificationFragment extends Fragment implements View.OnClickListener{

    private static String TAG = "NotificationFragment.class";
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

    private NotifListener notifListener = new NotifListener();

    public NotificationFragment() {
        NLService.addNotifListener(notifListener);
    }

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

        notificationIV.setOnClickListener(this);

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
            FragmentActivity f = getActivity();
            PackageManager pm = f.getPackageManager();
            Drawable appIcon = pm.getApplicationIcon(getCurrentApplicationPackage());
//            notificationIV.setImageDrawable(new RoundAppIcon(appIcon));
            notificationIV.setImageDrawable(appIcon);
        } catch (IllegalStateException | NullPointerException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
//            notificationIV.setImageDrawable(getResources().getDrawable(android.R.drawable.stat_notify_error));
        }
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        applicationNameTV.setText(this.applicationName);
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
        notificationTitleTV.setText(this.notificationTitle);
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
        notificationTextTV.setText(this.notificationText);
    }

    public void setNotificationSubText(String notificationSubText) {
        this.notificationSubText = notificationSubText;
        notificationSubTextTV.setText(notificationSubText);
    }

    public String getCurrentApplicationPackage() {
        return currentApplicationPackage;
    }

    public void setCurrentApplicationPackage(String currentApplicationPackage) {
        this.currentApplicationPackage = currentApplicationPackage;
        try {
            setApplicationName(getActivity().getPackageManager().getApplicationLabel(
                    getActivity().getPackageManager().getApplicationInfo(this.currentApplicationPackage, 0)).toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setNotificationAppIcon();

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.notification_app_icon) {
            openApplication();
        }
    }

    private class NotifListener implements NLService.CHNotifListener {

        @Override
        public void onNotificationPosted(CHNotification chNotification) {
            setApplicationName(chNotification.getAppName());
            setNotificationAppIcon();
            setNotificationTitle(chNotification.getTitle());
            setNotificationText(chNotification.getText());
            setNotificationSubText(chNotification.getSubtext());
        }

        @Override
        public void onMusicPosted(CHMusic chMusic) {}
    }
}

