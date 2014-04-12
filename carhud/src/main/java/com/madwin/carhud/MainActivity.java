package com.madwin.carhud;

import org.w3c.dom.Document;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class MainActivity extends FragmentActivity implements NavigationDialogFragment.Communicator{

    private NotificationReceiver nReceiver;
    private SpeedReceiver sReceiver;
    private LongClickReceiver longClickReceiver;
    private MetaDataReceiver metaDataReceiver;
    //private SpotifyReceiver spotReceiver;
    //private PandoraReceiver pandReceiver;
    private String TAG = "carhud";
    protected PowerManager.WakeLock mWakeLock;

    /*** Nav Bar ****/

    private String[] mNavBarTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    LatLng fromPosition;
    LatLng toPosition;
    LatLng currentLocation;
    LatLng longClickLocation;
    FragmentManager fm;
    FragmentTransaction ft;

    GMapV2Direction md;
    GoogleMap mMap;



    /*** Nav Bar ****/



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMap = ((SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.mv)).getMap();
        mMap.getUiSettings().setCompassEnabled(true);

        /********keep screen on********************/
         final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
         this.mWakeLock.acquire();

        /*************keep screen on*************************/

        /**************** Nav Bar Setup******************************************/
        mNavBarTitles = getResources().getStringArray(R.array.navbar_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
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
                getActionBar().setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

/**************** Nav Bar Setup******************************************/




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
        registerReceiver(metaDataReceiver,mDFilter);
        /*spotReceiver = new SpotifyReceiver();
        IntentFilter spotFilter = new IntentFilter();
        spotFilter.addAction("com.spotify.mobile.android.metadatachanged");
        registerReceiver(spotReceiver, spotFilter);*/
       /* pandReceiver = new PandoraReceiver();
        IntentFilter pandFilter = new IntentFilter();
        pandFilter.addAction("gonemad.dashclock.music.metachanged");
        pandFilter.addAction("com.android.music.metachanged");
        pandFilter.addAction("com.amazon.mp3.album");
        pandFilter.addAction("com.amazon.mp3.metachanged");
        pandFilter.addAction("com.amazon.mp3.track");
        pandFilter.addAction("com.android.music.metachanged");
        pandFilter.addAction("com.android.music.metachanged");
        pandFilter.addAction("com.android.music.metachanged");
        pandFilter.addAction("com.android.music.metachanged");
        pandFilter.addAction("com.android.music.metachanged");
        registerReceiver(pandReceiver,pandFilter);*/

        longClickReceiver = new LongClickReceiver();
        IntentFilter longClickFilter = new IntentFilter();
        longClickFilter.addAction("com.madwin.carhud.MAP_LONG_CLICK");
        registerReceiver(longClickReceiver,longClickFilter);

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        SpeedFragment sf = new SpeedFragment();
      //  ft.add(R.id.speed_frame, sf).commit();
        ft.add(R.id.map_fragment_frame, sf).commit();
    }


    @Override
    protected void onStop() {
        Log.e(TAG, "MainActivity stopped");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "MainActivity paused");

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "MainActivity destroyed");
        unregisterReceiver(nReceiver);
        unregisterReceiver(sReceiver);
        unregisterReceiver(longClickReceiver);
        //unregisterReceiver(spotReceiver);
      //  unregisterReceiver(pandReceiver);
        unregisterReceiver(metaDataReceiver);
        this.mWakeLock.release();
        finish();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
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

        super.onResume();
    }

    @Override
    public void onDialogMessage(String message) {
        if (message.equals("Yes Clicked")) {
            Toast.makeText(this, "Retrieving Route", Toast.LENGTH_SHORT);
            mMap.clear();
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                @Override
                public void gotLocation(Location location){
                    //Got the location!
                    fromPosition = new LatLng(location.getLatitude(), location.getLongitude());
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            toPosition = longClickLocation;
            new showRoute().execute();
        }
    }


    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            dumpIntent(intent);

            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    String packageName = extras.getString("notification_package");
                    CharSequence tickerText = extras.getCharSequence("notification_tickerText");
                    String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
                    CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
                    CharSequence notificationSubText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
                    int notificationSmallIcon = extras.getInt(Notification.EXTRA_SMALL_ICON);
                    byte[] byteArray = extras.getByteArray("notification_largeIcon");
                    Bitmap bmp = null;
                    if (byteArray != null)
                        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);



                    Log.d(TAG, "notificationSmallIcon = " + notificationSmallIcon);
                    Bitmap notificationLargeIcon = ((Bitmap) extras.getParcelable(Notification.EXTRA_LARGE_ICON));

                    if (!packageName.equals("com.iheartradio.connect") &&
                            !packageName.equals("com.clearchannel.iheartradio.controller") &&
                            !packageName.equals("com.pandora.android") &&
                            !packageName.equals("com.spotify.mobile.android.ui") &&
                            !packageName.equals("com.quoord.tapatalkHD") &&
                            !packageName.equals("com.google.android.music") &&
                            !packageName.equals("com.aws.android.elite")
                            ) {
                        TextView tv_package = (TextView) findViewById(R.id.nt_package);
                        TextView tv_title = (TextView) findViewById(R.id.nt_title);
                        TextView tv_text = (TextView) findViewById(R.id.nt_text);
                        TextView tv_sub_text = (TextView) findViewById(R.id.nt_subtext);
                        ImageView im_app_icon = (ImageView) findViewById(R.id.notification_app_icon);

                        Log.d(TAG, "Package Name = " + packageName);
                        try {
                            im_app_icon.setImageDrawable(getPackageManager().getApplicationIcon(packageName));
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        tv_package.setText(packageName);
                        tv_title.setText(notificationTitle);
                        tv_text.setText(notificationText);
                        tv_sub_text.setText(notificationSubText);
                    }

                    if (packageName
                            .equals("com.pandora.android")) {

                        TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                        TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                        TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                        ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                        tvMusicTitle.setText("Title : " + notificationTitle);
                        tvMusicArtist.setText("Artist : " + notificationText);
                        tvMusicOther.setText("");
                        ivAlbumArt.setImageResource(R.drawable.pandora_default);


                    } 
                    if (packageName.equals("com.spotify.mobile.android.ui")) {


                        Bundle bundle = intent.getExtras();
                        if(bundle != null) {

                            TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                            TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                            TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                            ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                            tvMusicArtist.setText("Artist : " +
                                    mFormatSpotifyArtist(bundle.getString("notification_tickerText")));
                            //     bundle.
                            tvMusicTitle.setText("Title : " + bundle.getString("android.title"));
                            tvMusicOther.setText("");


                            ivAlbumArt.setImageResource(R.drawable.spotify_default);



                        }

                    }

                }

            }



        }
    }

    class MetaDataReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                dumpIntent(intent);
                Bundle extras = intent.getExtras();
                if (extras != null) {

                    TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                    TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                    TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                    ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                    try {
                        ivAlbumArt.setImageDrawable(getPackageManager().getApplicationIcon("com.google.android.music"));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }

                    tvMusicArtist.setText(extras.getString("artist", "no artist"));
                    tvMusicTitle.setText(extras.getString("album", "no album"));
                    tvMusicOther.setText(extras.getString("track", "no track"));

                }
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
                    tvSpeed.setText(String.valueOf((int) (extras.getFloat("CURRENT_SPEED") * 2.23694)) + "mph");
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


    public String mFormatSpotifyArtist(String tickerText) {
        Log.e(TAG, "TickerText : " + tickerText);

        return tickerText.substring(tickerText.indexOf("—") + 2);
    }

    public static void dumpIntent(Intent i) {
        Bundle bundle = i.getExtras();
        if(bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e("carhud", "******Dumping Intent start********");
            while (it.hasNext()) {
                String key = it.next();
                Log.e("carhud", "[" + key + " = " + bundle.get(key) + "]");

            }
            Log.e("carhud", "******Dumping intent ended*******");
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
            case R.id.about:
                //showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void selectItem(int position) {

        Log.d("selectItem position variable = ", String.valueOf(position));
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
                MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){

                    @Override
                    public void gotLocation(Location location){
                        //Got the location!
                        if (location.getLatitude() != 0 || location.getLongitude() != 0) {
                            fromPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        }

                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(this, locationResult);

                mMap.clear();
                md = new GMapV2Direction();
                mMap = ((SupportMapFragment)getSupportFragmentManager()
                        .findFragmentById(R.id.mv)).getMap();
                new showRoute().execute();

                mDrawerLayout.closeDrawer(mDrawerList);

                return ;
            case 3:
                mMap.clear();
                mDrawerLayout.closeDrawer(mDrawerList);
                return ;
            case 4:

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
            Log.d(TAG, "fromPosition = " + fromPosition + " toPosition = " + toPosition);
            doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);

            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            rectLine = new PolylineOptions().width(7).color(Color.RED);

            for(int i = 0 ; i < directionPoint.size() ; i++) {
                rectLine.add(directionPoint.get(i));
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
                Log.d(TAG, "address = " + address);
                showDialog(getCurrentFocus());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void showDialog(View v) {
        NavigationDialogFragment navigationDialogFragment = new NavigationDialogFragment();
        navigationDialogFragment.show(getFragmentManager(), "NavigationDialog");
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