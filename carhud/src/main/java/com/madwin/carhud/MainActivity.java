package com.madwin.carhud;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.madwin.carhud.carmaps.GMapV2Direction;
import com.madwin.carhud.carmaps.MyLocation;
import com.madwin.carhud.fragments.AppListDialogFragment;
import com.madwin.carhud.fragments.MediaDialogFragment;
import com.madwin.carhud.fragments.NavigationDialogFragment;
import com.madwin.carhud.fragments.RefreshRouteFragment;
import com.madwin.carhud.fragments.SpeedFragment;
import com.madwin.carhud.notifications.MetaDataReceiver;
import com.madwin.carhud.notifications.NLService;

import org.w3c.dom.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements NavigationDialogFragment.Communicator, MediaDialogFragment.Communicator, View.OnClickListener{

    private static Context context;

    private NotificationReceiver nReceiver;
    private SpeedReceiver sReceiver;
    private LongClickReceiver longClickReceiver;
    private MetaDataReceiver metaDataReceiver;
    private String TAG = "carhud";

    /*** Nav Bar ****/

    private String[] mNavBarTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    LatLng fromPosition;
    LatLng toPosition;
    LatLng currentLocation;
    LatLng longClickLocation;
    FragmentManager fm;
    FragmentTransaction ft;
    GMapV2Direction md;
    GoogleMap mMap;

    //public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    //public static final String CMDSTOP = "stop";
    public static final String CMDPLAY = "play";

    TextView notif_tv_package;
    TextView notif_tv_title;
    TextView notif_tv_text;
    TextView notif_tv_sub_text;
    ImageView notif_im_app_icon;

    TextView tvMusicArtist;
    TextView tvMusicTitle;
    TextView tvMusicOther;
    ImageView ivAlbumArt;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMap = ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.mv)).getMap();
        mMap.getUiSettings().setCompassEnabled(true);

        // Keep screen on
        Window w = this.getWindow(); // in Activity's onCreate() for instance
        w.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // End Keep Screen on

        /**************** Nav Bar Setup******************************************/
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

/****************End Nav Bar Setup******************************************/

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.madwin.carhud.NOTIFICATION_LISTENER");
        registerReceiver(nReceiver,filter);
        sReceiver = new SpeedReceiver();
        IntentFilter sFilter = new IntentFilter();
        sFilter.addAction("com.madwin.carhud.SPEED_LISTENER");
        registerReceiver(sReceiver,sFilter);
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
        registerReceiver(longClickReceiver,longClickFilter);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        SpeedFragment sf = new SpeedFragment();
        RefreshRouteFragment rrf = new RefreshRouteFragment();
        ft.add(R.id.map_fragment_frame, rrf);
        ft.add(R.id.map_fragment_frame, sf).commit();


        /*Declaring views in notification fragment*/
        notif_tv_package = (TextView) findViewById(R.id.nt_package);
        notif_tv_title = (TextView) findViewById(R.id.nt_title);
        notif_tv_text = (TextView) findViewById(R.id.nt_text);
        notif_tv_sub_text = (TextView) findViewById(R.id.nt_subtext);
        notif_im_app_icon = (ImageView) findViewById(R.id.notification_app_icon);
        notif_im_app_icon.setImageDrawable(getResources()
                    .getDrawable(android.R.drawable.sym_def_app_icon));

        /*Declaring views in media fragment*/
        tvMusicArtist = (TextView) findViewById(R.id.music_text);
        tvMusicTitle = (TextView) findViewById(R.id.music_title);
        tvMusicOther = (TextView) findViewById(R.id.music_subtext);
        ivAlbumArt = (ImageView) findViewById(R.id.album_art);
        ivAlbumArt.setImageDrawable(getResources()
                .getDrawable(android.R.drawable.ic_media_play));

        MainActivity.context = getApplicationContext();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "MainActivity stopped");
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
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "MainActivity resumed");
        if (getIntent() != null) {
            Intent intent = getIntent();
            fromPosition = new LatLng(intent.getDoubleExtra("from_latitude", 0), intent.getDoubleExtra("from_longitude", 0));
            toPosition = new LatLng(intent.getDoubleExtra("to_latitude", 0), intent.getDoubleExtra("to_longitude", 0));
            md = new GMapV2Direction();
            mMap = ((SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mv)).getMap();
            new showRoute().execute();
        }
    }

    @Override
    public void onDialogMessage(String message) {
        if (message.equals("navigate_with_maps")) {
            mMap.clear();
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    //Got the location!
                    fromPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    toPosition = longClickLocation;
                    new showRoute().execute();
                    String navURL = "http://maps.google.com/maps?daddr="
                            + longClickLocation.latitude + "," + longClickLocation.longitude;
                    Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navURL));
                    startActivity(navIntent);
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);

        }
        if (message.endsWith("Yes Clicked")) {
            mMap.clear();
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    //Got the location!
                    fromPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    toPosition = longClickLocation;
                    new showRoute().execute();
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);

        }
        if (message.equals("PREVIOUS_CLICKED")) {mSendMediaControl(CMDPREVIOUS);}
        if (message.equals("PAUSE_CLICKED")) {mSendMediaControl(CMDPAUSE);}
        if (message.equals("PLAY_CLICKED")) {mSendMediaControl(CMDPLAY);}
        if (message.equals("NEXT_CLICKED")) {mSendMediaControl(CMDNEXT);}
        if (message.equals("APP_SELECTOR")) {
                AppListDialogFragment appListDialogFragment = new AppListDialogFragment();
                appListDialogFragment.show(getFragmentManager(), "AppListDialog");
            }
    }

    private void mSendMediaControl (String string) {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if(mAudioManager.isMusicActive() && !string.equals(CMDPLAY)) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME , string);
            MainActivity.this.sendBroadcast(i);
        }
        if(string.equals(CMDPLAY)) {
            Intent i = new Intent(SERVICECMD);
            i.putExtra(CMDNAME , CMDPLAY);
            MainActivity.this.sendBroadcast(i);
        }
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle extras = intent.getExtras();

            Drawable app_icon = null;
            try {
                app_icon = getPackageManager().getApplicationIcon(intent.getStringExtra("packagename"));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (extras.getString("notificationtype").equals("notification")) {
                notif_im_app_icon.setImageDrawable(app_icon);
                notif_tv_package.setText(extras.getString("packagelabel"));
                notif_tv_title.setText(extras.getString("notificationtitle"));
                notif_tv_text.setText(extras.getString("notificationtext"));
                notif_tv_sub_text.setText(extras.getString("notificationsubtext"));
            }

            if (extras.getString("notificationtype").equals("music")) {
                tvMusicArtist.setText(extras.getString("songartist"));
                tvMusicTitle.setText(extras.getString("songtitle"));
                tvMusicOther.setText(extras.getString("songalbum"));
                ivAlbumArt.setImageDrawable(app_icon);
            }
        }
    }

    class SpeedReceiver extends BroadcastReceiver{

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
    class LocationReceiver extends BroadcastReceiver{

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


/*******************Options Menu *****************************************************/

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

                return true;
            case R.id.applications:

                return true;
            case R.id.about:
                startActivity(new Intent(this,AboutActivity.class));
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
                return ;
            case 1:
                Intent intent = new Intent(this, AddressActivity.class);
                startActivity(intent);

                return ;
            case 2:
                mUpdateRoute();
                mDrawerLayout.closeDrawer(mDrawerList);

                return ;
            case 3:
                mMap.clear();
                mDrawerLayout.closeDrawer(mDrawerList);
                return ;
            case 4:
                AppListDialogFragment appListDialogFragment = new AppListDialogFragment();
                appListDialogFragment.show(getFragmentManager(), "AppListDialog");
                return ;
            case 5:
                startActivity(new Intent(this,AboutActivity.class));

                return ;
            case 6:
                finish();
                return ;
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

    private class showRoute extends AsyncTask<Void, Void, Document> {

        Document doc;
        PolylineOptions rectLine;

        @Override
        protected Document doInBackground(Void... params) {
            doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);

            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            rectLine = new PolylineOptions().width(7).color(Color.RED);

            for (LatLng aDirectionPoint : directionPoint) {
                rectLine.add(aDirectionPoint);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document result) {
            mMap.addPolyline(rectLine);
        }

    }
    private String getAddress(LatLng latLng) throws IOException {
        Double tempLatitude = latLng.latitude;
        Double tempLongitude = latLng.longitude;
        Log.d(TAG, "Retrieving address for " + tempLatitude + ", " + tempLongitude);
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        addresses = geocoder.getFromLocation(tempLatitude, tempLongitude, 1);
        if(addresses.size() > 0) {
            return addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getAddressLine(1);
        }
        return null;
    }

    class LongClickReceiver extends BroadcastReceiver{
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
        if(view.getId() == R.id.media)
        {
            MediaDialogFragment mediaDialogFragment = new MediaDialogFragment();
            mediaDialogFragment.show(getFragmentManager(), "MediaDialog");
        }
        if(view.getId() == R.id.refresh_button) {
            mUpdateRoute();
        }
    }

    private void mUpdateRoute() {
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){

            @Override
            public void gotLocation(Location location){
                //Got the location!
                if (location.getLatitude() != 0 || location.getLongitude() != 0) {
                    fromPosition = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    md = new GMapV2Direction();
                    mMap = ((SupportMapFragment)getSupportFragmentManager()
                            .findFragmentById(R.id.mv)).getMap();
                    new showRoute().execute();
                }
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(this, locationResult);

    }

    public static Context getAppContext() {
        return MainActivity.context;
    }


    /*******************Options Menu *****************************************************/
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
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
	}
}