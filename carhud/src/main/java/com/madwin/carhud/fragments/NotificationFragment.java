package com.madwin.carhud.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class NotificationFragment extends Fragment implements View.OnClickListener{

    public static final String NOTIFICATION_INTENT = "COM.MADWIN.CARHUD.NOTIFICATION.INTENT";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    public static final String PACKAGE_LABEL = "PACKAGE_LABEL";
    public static final String TITLE = "TITLE";
    public static final String TEXT = "TEXT";
    public static final String SUBTEXT = "SUBTEXT";

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

    private NotificationReceiver notificationReceiver =
            new NotificationReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(NOTIFICATION_INTENT);
        getActivity().registerReceiver(notificationReceiver, filter);
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

    public void onClick(View view) {
        if (view.getId() == R.id.notification_app_icon) {
            openApplication();
        }
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (!(intent.getStringExtra(PACKAGE_NAME) == null))
                setCurrentApplication(intent.getStringExtra(PACKAGE_NAME));

            if (!(intent.getStringExtra(TITLE) == null))
                setNotificationTitle(intent.getStringExtra(TITLE));

            if (!(intent.getStringExtra(TEXT) == null))
                setNotificationText(intent.getStringExtra(TEXT));

            if (!(intent.getStringExtra(SUBTEXT) == null))
                setNotificationSubText(intent.getStringExtra(SUBTEXT));

        }
    }
}

