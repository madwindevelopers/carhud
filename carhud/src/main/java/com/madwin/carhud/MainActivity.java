package com.madwin.carhud;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.madwin.carhud.fragments.AppListDialogFragment;
import com.madwin.carhud.fragments.MapFragment;
import com.madwin.carhud.fragments.MediaDialogFragment;
import com.madwin.carhud.fragments.NavigationDialogFragment;
import com.madwin.carhud.notifications.MetaDataReceiver;
import com.madwin.carhud.notifications.NLService;
import com.madwin.carhud.utils.DisplayUtils;
import com.madwin.carhud.utils.RoundAppIcon;

import java.io.IOException;
import java.util.List;


public class MainActivity extends FragmentActivity implements NavigationDialogFragment.Communicator, MediaDialogFragment.Communicator, View.OnClickListener {

    private static Context context;
    public static Boolean activityRunning = false;

    private NotificationReceiver nReceiver;
    private SpeedReceiver sReceiver;
    private LongClickReceiver longClickReceiver;
    private MetaDataReceiver metaDataReceiver;
    private String TAG = "carhud";

    /**
     * Nav Bar ***
     */

    private String[] mNavBarTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    LatLng toPosition;
    LatLng currentLocation;
    LatLng longClickLocation;

    //public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    //public static final String CMDSTOP = "stop";
    public static final String CMDPLAY = "play";

    private String notificationApplication;

    TextView notification_tv_package;
    TextView notification_tv_title;
    TextView notification_tv_text;
    TextView notification_tv_sub_text;
    ImageView notification_im_app_icon;

    TextView tvMusicArtist;
    TextView tvMusicTitle;
    TextView tvMusicOther;
    ImageView ivAlbumArt;

    RelativeLayout main_layout;
    RelativeLayout map_fragment_layout;
    RelativeLayout notification_fragment_layout;
    RelativeLayout controls_fragment_layout;
    int portrait_height, portrait_width, landscape_height, landscape_width;

    public boolean SPEED_BASED_ZOOM = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSetupDrawer();

        mSetupReceivers();

        /*Declaring views in notification fragment*/
        notification_tv_package = (TextView) findViewById(R.id.nt_package);
        notification_tv_title = (TextView) findViewById(R.id.nt_title);
        notification_tv_text = (TextView) findViewById(R.id.nt_text);
        notification_tv_sub_text = (TextView) findViewById(R.id.nt_subtext);
        notification_im_app_icon = (ImageView) findViewById(R.id.notification_app_icon);
        notification_im_app_icon.setImageDrawable(new RoundAppIcon(getResources()
                .getDrawable(android.R.drawable.sym_def_app_icon)));

        /*Declaring views in media fragment*/
        tvMusicArtist = (TextView) findViewById(R.id.music_text);
        tvMusicTitle = (TextView) findViewById(R.id.music_title);
        tvMusicOther = (TextView) findViewById(R.id.music_subtext);
        ivAlbumArt = (ImageView) findViewById(R.id.album_art);
        ivAlbumArt.setImageDrawable(new RoundAppIcon(getResources()
                .getDrawable(android.R.drawable.ic_media_play)));

        // Keep screen on
        Window w = this.getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // End Keep Screen on

        MainActivity.context = getApplicationContext();


        map_fragment_layout = (RelativeLayout) findViewById(R.id.map_fragment_layout);
        notification_fragment_layout = (RelativeLayout) findViewById(R.id.notification_fragment_layout);
        controls_fragment_layout = (RelativeLayout) findViewById(R.id.media_fragment_layout);
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

    private void mGetLayoutDimensions() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            portrait_height = landscape_width = main_layout.getWidth();
            portrait_width = landscape_height = main_layout.getHeight();
        } else {
            portrait_height = landscape_width = main_layout.getHeight();
            portrait_width = landscape_height = main_layout.getWidth();
        }
    }

    private void mSetupLayout() {
        RelativeLayout.LayoutParams mapParams = null,
                notificationParams = null, controlsParams = null;


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mapParams = new RelativeLayout.LayoutParams(
                    (int) (landscape_width * 0.6), landscape_height);
            notificationParams = new RelativeLayout.LayoutParams(
                    (int) (0.4 * landscape_width), (int) (0.7 * landscape_height));
            controlsParams = new RelativeLayout.LayoutParams(
                    (int) (0.4 * landscape_width), (int) (0.3 * landscape_height));

            // Map Landscape Parameters
            mapParams.addRule(RelativeLayout.END_OF, notification_fragment_layout.getId());
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            mapParams.setMargins((int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8),
                    (int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8));

            // Notification Landscape Parameters
            notificationParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            notification_fragment_layout.getLayoutParams().height = (int) (0.67 * landscape_height);
            notification_fragment_layout.getLayoutParams().width = (int) (0.4 * landscape_width);
            notificationParams.setMargins((int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8),
                    0, (int) DisplayUtils.convertDpToPixel(8));

            // Media Landscape Parameters
            controlsParams.addRule(RelativeLayout.BELOW, notification_fragment_layout.getId());
            controls_fragment_layout.getLayoutParams().height = (int) (0.33 * landscape_height);
            controls_fragment_layout.getLayoutParams().width = (int) (0.4 * landscape_width);
            controlsParams.setMargins((int) DisplayUtils.convertDpToPixel(8), 0,
                    0, (int) DisplayUtils.convertDpToPixel(8));

        } else if (getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {

            // Map Portrait Parameters
            mapParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (int) (0.58 * portrait_height));
            mapParams.setMargins((int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8),
                    (int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8));
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            mapParams.addRule(RelativeLayout.ALIGN_PARENT_START);

            // Notification Portrait Parameters
            notificationParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (int) (0.25 * portrait_height));
            notificationParams.setMargins((int) DisplayUtils.convertDpToPixel(8), 0,
                    (int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8));
            notificationParams.addRule(RelativeLayout.BELOW, map_fragment_layout.getId());

            // Media Portrait Parameters
            controlsParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, (int) (0.17 * portrait_height));
            controlsParams.setMargins((int) DisplayUtils.convertDpToPixel(8), 0,
                    (int) DisplayUtils.convertDpToPixel(8), (int) DisplayUtils.convertDpToPixel(8));
            controlsParams.addRule(RelativeLayout.BELOW, notification_fragment_layout.getId());
            controlsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }

        map_fragment_layout.setLayoutParams(mapParams);
        notification_fragment_layout.setLayoutParams(notificationParams);
        controls_fragment_layout.setLayoutParams(controlsParams);
        animateViews();
    }

    private void mSetupReceivers() {
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.madwin.carhud.NOTIFICATION_LISTENER");
        registerReceiver(nReceiver, filter);
        sReceiver = new SpeedReceiver();
        IntentFilter sFilter = new IntentFilter();
        sFilter.addAction("com.madwin.carhud.SPEED_LISTENER");
        registerReceiver(sReceiver, sFilter);
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

        longClickReceiver = new LongClickReceiver();
        IntentFilter longClickFilter = new IntentFilter();
        longClickFilter.addAction("com.madwin.carhud.MAP_LONG_CLICK");
        registerReceiver(longClickReceiver, longClickFilter);
    }

    private void mSetupDrawer() {

        mNavBarTitles = getResources().getStringArray(R.array.navbar_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_list_item, mNavBarTitles));
        // Set the list's click listener
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
        AppAsyncTask appListTask = new AppAsyncTask(getApplicationContext());
        appListTask.execute();

        activityRunning = true;
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "MainActivity destroyed");
        unregisterReceiver(nReceiver);
        unregisterReceiver(sReceiver);
        unregisterReceiver(longClickReceiver);
        unregisterReceiver(metaDataReceiver);
        //unregisterReceiver(addressReceiver);
        finish();
        activityRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGetLayoutDimensions();
        mSetupLayout();
        Log.e(TAG, "MainActivity resumed");
        /*
         * Set preference values
         */
        SharedPreferences sp = this.getSharedPreferences(
                "com.madwin.carhud", Context.MODE_PRIVATE);
        SPEED_BASED_ZOOM = sp.getBoolean("speed_zoom_preference", true);

        activityRunning = true;
    }

    @Override
    public void onDialogMessage(String message) {
        if (message.equals("navigate_with_maps")) {
            clearMap();
            toPosition = longClickLocation;
            showRoute(toPosition);
            String navURL = "http://maps.google.com/maps?daddr="
                    + longClickLocation.latitude + "," + longClickLocation.longitude;
            Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navURL));
            startActivity(navIntent);
        }

        if (message.endsWith("Yes Clicked")) {
            clearMap();
            toPosition = longClickLocation;
            showRoute(toPosition);
        }
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

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();

            notificationApplication = intent.getStringExtra("packagename");

            Drawable app_icon = null;
            try {
                app_icon = getPackageManager().getApplicationIcon(notificationApplication);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (extras.getString("notificationtype").equals("notification")) {
                //notification_im_app_icon.setImageDrawable(app_icon);
                notification_im_app_icon.setImageDrawable(new RoundAppIcon(app_icon));
                notification_tv_package.setText(extras.getString("packagelabel"));
                notification_tv_title.setText(extras.getString("notificationtitle"));
                notification_tv_text.setText(extras.getString("notificationtext"));
                notification_tv_sub_text.setText(extras.getString("notificationsubtext"));
            }

            if (extras.getString("notificationtype").equals("music")) {
                tvMusicArtist.setText(extras.getString("songartist"));
                tvMusicTitle.setText(extras.getString("songtitle"));
                tvMusicOther.setText(extras.getString("songalbum"));
                //ivAlbumArt.setImageDrawable(app_icon);
                ivAlbumArt.setImageDrawable(new RoundAppIcon(app_icon));
            }
        }
    }

    class SpeedReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    TextView tvSpeed = (TextView) findViewById(R.id.speedometer);
                    tvSpeed.setText(String.valueOf((int) (extras.getFloat("CURRENT_SPEED") * 2.23694)) + " mph");
                }
            }
        }
    }

    class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    currentLocation = new LatLng(extras.getDouble("Latitude"), extras.getDouble("Longitude"));
                }
            }
        }
    }

    /**
     * ****************Options Menu ****************************************************
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.activate_notifications:

                return true;
            case R.id.enter_address:

                return true;
            case R.id.update_route:

                return true;
            case R.id.clear_directions:
                clearMap();

                return true;
            case R.id.applications:

                return true;
            case R.id.about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.exit:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position) {

        switch (position) {
            case 0:
                if (!isNLServiceRunning()) {
                    startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
                } else {
                    Toast.makeText(this, "NOTIFICATION LISTENER ALREADY SET!!!", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 1:
                if (!NetworkUtil.getConnectivityStatusString(getAppContext())
                        .equals("Not connected to Internet")) {
                    Intent intent = new Intent(this, AddressActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "No data connection", Toast.LENGTH_SHORT).show();
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 2:
                if (!NetworkUtil.getConnectivityStatusString(getAppContext())
                        .equals("Not connected to Internet")) {
                    mUpdateRoute();
                } else {
                    Toast.makeText(this, "No data connection", Toast.LENGTH_LONG).show();
                }
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 3:
                //mMap.clear();
                clearMap();
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 4:
                AppListDialogFragment appListDialogFragment = new AppListDialogFragment();
                appListDialogFragment.show(getFragmentManager(), "AppListDialog");
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 5:
                Intent intent2 = new Intent(this, PreferencesActivity.class);
                startActivity(intent2);
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 6:
                startActivity(new Intent(this, AboutActivity.class));
                mDrawerLayout.closeDrawer(mDrawerList);
                return;

            case 7:
                finish();
                return;
        }

        mDrawerList.setItemChecked(position, true);
        setTitle(mNavBarTitles[position]);
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

        animateViews();
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
            selectItem(position);
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


    private String getAddress(LatLng latLng) throws IOException {
        Double tempLatitude = latLng.latitude;
        Double tempLongitude = latLng.longitude;
        Log.d(TAG, "Retrieving address for " + tempLatitude + ", " + tempLongitude);
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        addresses = geocoder.getFromLocation(tempLatitude, tempLongitude, 1);
        if (addresses.size() > 0) {
            return addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);
        }
        return null;
    }

    class LongClickReceiver extends BroadcastReceiver {
        String address = "unable to retrieve address";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    longClickLocation = new LatLng(extras.getDouble("Latitude"), extras.getDouble("Longitude"));
                }
            }
            try {
                address = getAddress(longClickLocation);
                showNavigationDialog(getCurrentFocus());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void showNavigationDialog(View v) {
        NavigationDialogFragment navigationDialogFragment = new NavigationDialogFragment();
        navigationDialogFragment.show(getFragmentManager(), "NavigationDialog");
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.media) {
            MediaDialogFragment mediaDialogFragment = new MediaDialogFragment();
            mediaDialogFragment.show(getFragmentManager(), "MediaDialog");
        }
        if (view.getId() == R.id.refresh_button) {
            mUpdateRoute();
        }
        if (view.getId() == R.id.notification_app_icon) {
            Intent intent;
            PackageManager manager = getPackageManager();
            try {
                intent = manager.getLaunchIntentForPackage(notificationApplication);
                if (intent == null)
                    throw new PackageManager.NameNotFoundException();
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivity(intent);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(this, "package name not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void mUpdateRoute() {
        Intent i = new Intent(MapFragment.MAP_BROADCAST_FILTER);
        i.putExtra(MapFragment.MAP_BROADCAST_PURPOSE, MapFragment.PURPOSE_UPDATE_ROUTE);
        sendBroadcast(i);
    }

    private void clearMap() {
        Intent i = new Intent(MapFragment.MAP_BROADCAST_FILTER);
        i.putExtra(MapFragment.MAP_BROADCAST_PURPOSE, MapFragment.PURPOSE_CLEAR_MAP);
        sendBroadcast(i);

        animateViews();
    }

    private void showRoute(LatLng latLng) {
        Intent i = new Intent(MapFragment.MAP_BROADCAST_FILTER);
        i.putExtra(MapFragment.MAP_BROADCAST_PURPOSE, MapFragment.PURPOSE_SHOW_ROUTE);
        i.putExtra(MapFragment.BROADCAST_TO_POSITION, latLng);
        sendBroadcast(i);
    }

    private void animateViews() {
        Log.d(TAG, "animating Views");

        TranslateAnimation anim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, portrait_height, 0);
        anim.setDuration(800);


        View mapFragment = findViewById(R.id.map_fragment_layout);
        View notificationFragment = findViewById(R.id.notification_fragment_layout);
        View mediaFragment = findViewById(R.id.media_fragment_layout);
        mapFragment.bringToFront();
       // mapFragment.animate().translationY(portrait_height).translationY(-portrait_height).setDuration(2000).start();
        //mapFragment.animate().translationY(-portrait_height).setDuration(2000).start();
        mapFragment.startAnimation(anim);
        notificationFragment.startAnimation(anim);
        mediaFragment.startAnimation(anim);
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
