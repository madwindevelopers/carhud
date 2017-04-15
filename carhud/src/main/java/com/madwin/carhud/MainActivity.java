package com.madwin.carhud;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.madwin.carhud.fragments.AppListDialogFragment;
import com.madwin.carhud.fragments.MapFragment;
import com.madwin.carhud.fragments.MediaDialogFragment;
import com.madwin.carhud.fragments.MediaFragment;
import com.madwin.carhud.fragments.NotificationFragment;
import com.madwin.carhud.notifications.NLService;
import com.madwin.carhud.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends FragmentActivity implements
        MediaDialogFragment.Communicator, View.OnClickListener {

    private static Context context;
    private static Boolean activityRunning = false;

    private static MapFragment mapFragment;
    private static NotificationFragment notificationFragment;
    private static MediaFragment mediaFragment;

    private static AppAsyncTask appListTask;

    private String TAG = "carhud";

    /**
     * Nav Bar ***
     */

    private String[] mNavBarTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private RelativeLayout main_layout;
    private RelativeLayout map_fragment_layout;
    private RelativeLayout notification_fragment_layout;
    private RelativeLayout media_fragment_layout;
    private int portrait_height, portrait_width, landscape_height, landscape_width;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSetupDrawer();

        // Keep screen on
        Window w = this.getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        MainActivity.context = getApplicationContext();

        map_fragment_layout = (RelativeLayout) findViewById(R.id.map_fragment_layout);
        notification_fragment_layout = (RelativeLayout) findViewById(R.id.notification_fragment_layout);
        media_fragment_layout = (RelativeLayout) findViewById(R.id.media_fragment_layout);
        main_layout = (RelativeLayout) findViewById(R.id.layout_main_test);
        mSetupLayout();
        main_layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "OnGlobalTree Listener initiated");
                main_layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mGetLayoutDimensions();
                mSetupLayout();
            }
        });
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        mapFragment = (MapFragment) getSupportFragmentManager().
                findFragmentById(R.id.map_fragment);
        notificationFragment = (NotificationFragment) getSupportFragmentManager().
                findFragmentById(R.id.notification_fragment);
        mediaFragment = (MediaFragment) getSupportFragmentManager().
                findFragmentById(R.id.media_fragment);

        return super.onCreateView(parent, name, context, attrs);
    }

    private void mGetLayoutDimensions() {
        int width = main_layout.getWidth();
        int height = main_layout.getHeight();
        if (width > height) {
            // landscape
            landscape_width = portrait_height = width;
            landscape_height = portrait_width = height;
        } else {
            landscape_height = portrait_width = width;
            landscape_width = portrait_height = height;
        }
    }

    /*
     *  Setup layout to dynamically adjust for portrait or landscape.  In portrait, all three
     *  views will be aligned vertically.  In landscape, notifications and media views will be on
     *  the left and the map will be on the right.
     */
    final static double LANDSCAPE_LEFT_RATIO = 0.6;
    final static double LANDSCAPE_TOP_RATIO = 0.67;
    final static double PORTRAIT_TOP_RATIO = 0.58;
    final static double PORTRAIT_BOTTOM_RATIO = 0.2;
    final static int LAYOUT_MARGIN_DP = 8;

    private void mSetupLayout() {
        RelativeLayout.LayoutParams mapParams = null,
                notificationParams = null, controlsParams = null;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mapParams = new RelativeLayout.LayoutParams(
                    (int) (landscape_width * LANDSCAPE_LEFT_RATIO),
                    landscape_height
            );
            notificationParams = new RelativeLayout.LayoutParams(
                    (int) (landscape_width * (1 - LANDSCAPE_LEFT_RATIO)),
                    (int) (landscape_height * LANDSCAPE_TOP_RATIO)
            );
            controlsParams = new RelativeLayout.LayoutParams(
                    (int) (landscape_width * (1 - LANDSCAPE_LEFT_RATIO)),
                    (int) (landscape_height * (1 - LANDSCAPE_TOP_RATIO))
            );

            // Map Landscape Parameters
            mapParams.addRule(RelativeLayout.END_OF, notification_fragment_layout.getId());
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            mapParams.setMargins(
                    (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP),
                    (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP),
                    (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP),
                    (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP)
            );

            // Notification Landscape Parameters
            notificationParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            notificationParams.setMargins((int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP), (int) DisplayUtils.convertDpToPixel(8),
                    0, (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP));

            // Media Landscape Parameters
            controlsParams.addRule(RelativeLayout.BELOW, notification_fragment_layout.getId());
            controlsParams.setMargins((int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP), 0,
                    0, (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP));

        } else if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {

            // Map Portrait Parameters
            mapParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (int) (portrait_height * PORTRAIT_TOP_RATIO)
            );
            mapParams.setMargins(
                    0,
                    (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP),
                    0,
                    (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP)
            );
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_START);

            // Notification Portrait Parameters
            notificationParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (int) (portrait_height * (1 - PORTRAIT_TOP_RATIO - PORTRAIT_BOTTOM_RATIO))
            );
            notificationParams.setMargins(
                    0,
                    0,
                    0,
                    (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP)
            );
            notificationParams.addRule(RelativeLayout.BELOW, map_fragment_layout.getId());

            // Media Portrait Parameters
            controlsParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (int) (portrait_height * PORTRAIT_BOTTOM_RATIO)
            );
            controlsParams.setMargins(
                    0,
                    0,
                    0,
                    (int) DisplayUtils.convertDpToPixel(LAYOUT_MARGIN_DP)
            );
            controlsParams.addRule(RelativeLayout.BELOW, notification_fragment_layout.getId());
            controlsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }

        map_fragment_layout.setLayoutParams(mapParams);
        notification_fragment_layout.setLayoutParams(notificationParams);
        media_fragment_layout.setLayoutParams(controlsParams);
    }

    private void mSetupDrawer() {

        mNavBarTitles = getResources().getStringArray(R.array.navbar_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        ArrayList<String> mNavBarTitlesList = new ArrayList<>();
        ArrayList<Drawable> mNavBarIcons = new ArrayList<>();

        TypedArray navBarIconsArray = getResources().obtainTypedArray(R.array.nav_drawer_icons_grey);
        for (int i = 0; i < mNavBarTitles.length; i++) {
            mNavBarTitlesList.add(mNavBarTitles[i]);
            mNavBarIcons.add(navBarIconsArray.getDrawable(i));
        }
        navBarIconsArray.recycle();

        // Set the adapter for the list view
        NavBarArrayAdapter navBarArrayAdapter = new NavBarArrayAdapter(this,
                mNavBarTitlesList, mNavBarIcons);
        mDrawerList.setAdapter(navBarArrayAdapter);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length && i < grantResults.length; ++i) {
            Log.d(TAG, permissions[i] + " : " + grantResults[i]);
            if ((permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    permissions[i].equals(Manifest.permission.ACCESS_COARSE_LOCATION)) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                mapFragment.enableMyLocation();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        appListTask = new AppAsyncTask(getApplicationContext());
        appListTask.execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "MainActivity stopped");

        activityRunning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "MainActivity paused");
        mapFragment.stopLocationListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "MainActivity destroyed");
//        unregisterReceiver(metaDataReceiver);
        finish();
        activityRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.startLocationListener();
        mGetLayoutDimensions();
        mSetupLayout();
        if (isNLServiceRunning())
            NLService.startMediaSessionManager();
        Log.e(TAG, "MainActivity resumed");
        activityRunning = true;
    }

    @Override
    public void onDialogMessage(String message) {
        if (message.equals("APP_SELECTOR")) {
            AppListDialogFragment appListDialogFragment = new AppListDialogFragment();
            appListDialogFragment.show(getFragmentManager(), "AppListDialog");
        }
    }

    /**
     * ****************Options Menu ****************************************************
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
            mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            mDrawerLayout.openDrawer(mDrawerList);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.activate_notifications:
                selectMenuItem(1);
                return true;
            case R.id.applications:
                selectMenuItem(2);
                return true;
            case R.id.settings:
                selectMenuItem(3);
                return true;
            case R.id.about:
                selectMenuItem(4);
                return true;
            case R.id.exit:
                selectMenuItem(5);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectMenuItem(int position) {

        switch (position) {
            case 1:
                if (!isNLServiceRunning()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast.makeText(this, "NOTIFICATION LISTENER ALREADY SET", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 2:
                AppListDialogFragment appListDialogFragment = new AppListDialogFragment();
                appListDialogFragment.show(getFragmentManager(), "AppListDialog");
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 3:
                Intent intent2 = new Intent(this, PreferencesActivity.class);
                startActivity(intent2);
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 4:
                startActivity(new Intent(this, AboutActivity.class));
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 5:
                finish();
                return;
        }

        if (position > 0)
            mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            portrait_height = landscape_width = main_layout.getWidth();
            portrait_width = landscape_height = main_layout.getHeight();
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            portrait_height = landscape_width = main_layout.getHeight();
            portrait_width = landscape_height = main_layout.getWidth();
        }
        Log.d(TAG, "On postCreate main layout height / width = " + main_layout.getHeight() + " / " + main_layout.getWidth());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
        mSetupLayout();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectMenuItem(position);
        }
    }

    private boolean isNLServiceRunning() {
        Set<String> listeners = NotificationManagerCompat.getEnabledListenerPackages(context);
        if (listeners.contains(getAppContext().getPackageName()))
                return true;
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.media) {
            MediaDialogFragment mediaDialogFragment = new MediaDialogFragment();
            mediaDialogFragment.show(getFragmentManager(), "MediaDialog");
        }
    }

    public static MapFragment getMapFragment() {
        return mapFragment;
    }
    public static NotificationFragment getNotificationFragment() {
        return notificationFragment;
    }
    public static MediaFragment getMediaFragment() {
        return mediaFragment;
    }

    public static AppAsyncTask getAppAsyncTask() { return appListTask; }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    public static boolean isRunning() {return activityRunning;}

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY );
        }
    }
}
