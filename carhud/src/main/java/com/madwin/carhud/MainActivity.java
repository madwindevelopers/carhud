package com.madwin.carhud;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
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
import com.madwin.carhud.notifications.MetaDataReceiver;
import com.madwin.carhud.notifications.NLService;
import com.madwin.carhud.utils.DisplayUtils;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements
        MediaDialogFragment.Communicator, View.OnClickListener{

    private static Context context;
    private static Boolean activityRunning = false;

    private static MapFragment mapFragment;
    private static NotificationFragment notificationFragment;
    private static MediaFragment mediaFragment;

    private static AppAsyncTask appListTask;

    private MetaDataReceiver metaDataReceiver;
    private String TAG = "carhud";

    /**
     * Nav Bar ***
     */

    private String[] mNavBarTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    //public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previo1us";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    //public static final String CMDSTOP = "stop";
    public static final String CMDPLAY = "play";

    private RelativeLayout main_layout;
    private RelativeLayout map_fragment_layout;
    private RelativeLayout notification_fragment_layout;
    private RelativeLayout media_fragment_layout;
    private int portrait_height, portrait_width, landscape_height, landscape_width;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSetupDrawer();

        mSetupReceivers();

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
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            portrait_height = landscape_width = main_layout.getWidth();
            portrait_width = landscape_height = main_layout.getHeight();
        } else {
            portrait_height = landscape_width = main_layout.getHeight();
            portrait_width = landscape_height = main_layout.getWidth();
        }
    }

    /*
     *  Setup layout to dynamically adjust for portrait or landscape.  In portrait, all three
     *  views will be aligned vertically.  In landscape, notifications and media views will be on
     *  the left and the map will be on the right.
     */
    private void mSetupLayout() {
        RelativeLayout.LayoutParams mapParams = null,
                notificationParams = null, controlsParams = null;


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mapParams = new RelativeLayout.LayoutParams(
                    (int) (landscape_width * 0.6), landscape_height);
            notificationParams = new RelativeLayout.LayoutParams(
                    (int) (0.4 * landscape_width), (int) (0.67 * landscape_height));
            controlsParams = new RelativeLayout.LayoutParams(
                    (int) (0.4 * landscape_width), (int) (0.33 * landscape_height));

            // Map Landscape Parameters
            mapParams.addRule(RelativeLayout.END_OF, notification_fragment_layout.getId());
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            mapParams.setMargins((int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8),
                    (int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8));

            // Notification Landscape Parameters
            notificationParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            notificationParams.setMargins((int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8),
                    0, (int) DisplayUtils.convertDpToPixel(8));

            // Media Landscape Parameters
            controlsParams.addRule(RelativeLayout.BELOW, notification_fragment_layout.getId());
            controlsParams.setMargins((int) DisplayUtils.convertDpToPixel(8), 0,
                    0, (int) DisplayUtils.convertDpToPixel(8));

        } else if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {

            // Map Portrait Parameters
            mapParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (int) (0.58 * portrait_height));
            mapParams.setMargins(0, (int) DisplayUtils.convertDpToPixel(8),
                    0, (int) DisplayUtils.convertDpToPixel(8));
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_START);

            // Notification Portrait Parameters
            notificationParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (int) (0.22 * portrait_height));
            notificationParams.setMargins(0, 0,
                    0, (int) DisplayUtils.convertDpToPixel(8));
            notificationParams.addRule(RelativeLayout.BELOW, map_fragment_layout.getId());

            // Media Portrait Parameters
            controlsParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (int) (0.2 * portrait_height));
            controlsParams.setMargins(0, 0,
                    0, (int) DisplayUtils.convertDpToPixel(8));
            controlsParams.addRule(RelativeLayout.BELOW, notification_fragment_layout.getId());
            controlsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }

        map_fragment_layout.setLayoutParams(mapParams);
        notification_fragment_layout.setLayoutParams(notificationParams);
        media_fragment_layout.setLayoutParams(controlsParams);
    }

    private void mSetupReceivers() {
        metaDataReceiver = new MetaDataReceiver();
        IntentFilter mDFilter = new IntentFilter();
        mDFilter.addAction("com.android.music.metachanged");
        mDFilter.addAction("com.android.music.playstatechanged");
        mDFilter.addAction("com.android.music.playbackcomplete");
        mDFilter.addAction("com.android.music.queuechanged");
        mDFilter.addAction("fm.last.android.metachanged");
        mDFilter.addAction("com.musixmatch.android.lyrify.metachanged");
        mDFilter.addAction("gonemad.dashclock.music.metachanged");
        mDFilter.addAction("com.sonyericsson.music.metachanged");
        registerReceiver(metaDataReceiver, mDFilter);
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
        unregisterReceiver(metaDataReceiver);
        finish();
        activityRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapFragment.startLocationListener();
        mGetLayoutDimensions();
        mSetupLayout();
        Log.e(TAG, "MainActivity resumed");

        activityRunning = true;
    }

    @Override
    public void onDialogMessage(String message) {
        if (message.equals("PREVIOUS_CLICKED")) {
            mSendMediaControl(CMDPREVIOUS);
        }
        if (message.equals("PAUSE_CLICKED")) {
            mSendMediaControl(CMDPAUSE);
        }
        if (message.equals("PLAY_CLICKED")) {
            mSendMediaControl(CMDPLAY);
        }
        if (message.equals("NEXT_CLICKED")) {
            mSendMediaControl(CMDNEXT);
        }
        if (message.equals("APP_SELECTOR")) {
            AppListDialogFragment appListDialogFragment = new AppListDialogFragment();
            appListDialogFragment.show(getFragmentManager(), "AppListDialog");
        }
    }

    private void mSendMediaControl(String string) {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (mAudioManager.isMusicActive() && !string.equals(CMDPLAY)) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, string);
            MainActivity.this.sendBroadcast(i);
        }
        if (string.equals(CMDPLAY)) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME, CMDPLAY);
            MainActivity.this.sendBroadcast(i);
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
            case R.id.enter_address:
                selectMenuItem(2);
                return true;
            case R.id.update_route:
                selectMenuItem(3);
                return true;
            case R.id.clear_directions:
                clearMap();
                selectMenuItem(4);
                return true;
            case R.id.applications:
                selectMenuItem(5);
                return true;
            case R.id.settings:
                selectMenuItem(6);
                return true;
            case R.id.about:
                selectMenuItem(7);
                return true;
            case R.id.exit:
                selectMenuItem(8);
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
                if (!NetworkUtil.getConnectivityStatusString(getAppContext())
                        .equals("Not connected to Internet")) {
                    Intent intent = new Intent(this, AddressActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No data connection", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 3:
                if (!NetworkUtil.getConnectivityStatusString(getAppContext())
                        .equals("Not connected to Internet")) {
                    mUpdateRoute();
                } else {
                    Toast.makeText(this, "No data connection", Toast.LENGTH_LONG).show();
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 4:
                clearMap();
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 5:
                AppListDialogFragment appListDialogFragment = new AppListDialogFragment();
                appListDialogFragment.show(getFragmentManager(), "AppListDialog");
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 6:
                Intent intent2 = new Intent(this, PreferencesActivity.class);
                startActivity(intent2);
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 7:
                startActivity(new Intent(this, AboutActivity.class));
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 8:
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
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NLService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.media) {
            MediaDialogFragment mediaDialogFragment = new MediaDialogFragment();
            mediaDialogFragment.show(getFragmentManager(), "MediaDialog");
        }
    }

    private void mUpdateRoute() {
        mapFragment.updateRoute();
    }

    private void clearMap() {
        mapFragment.clearMap();
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
