package com.madwin.carhud;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.Set;


public class MainActivity extends FragmentActivity {

    private NotificationReceiver nReceiver;
    private SpeedReceiver sReceiver;
    //private SpotifyReceiver spotReceiver;
    //private PandoraReceiver pandReceiver;

    private String TAG = "carhud";
    protected PowerManager.WakeLock mWakeLock;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /********keep screen on********************/
         final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
         this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
         this.mWakeLock.acquire();

        /*************keep screen on*************************/



        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.madwin.carhud.NOTIFICATION_LISTENER");
        registerReceiver(nReceiver,filter);
        sReceiver = new SpeedReceiver();
        IntentFilter sFilter = new IntentFilter();
        sFilter.addAction("com.madwin.carhud.SPEED_LISTENER");
        registerReceiver(sReceiver,sFilter);
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





        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        SpeedFragment sf = new SpeedFragment();
      //  ft.add(R.id.speed_frame, sf).commit();
        ft.add(R.id.map_fragment_frame, sf).commit();




    }


   /* public void buttonClick(View v) {
        Intent i2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
        if(v.getId() == R.id.music_previous){
            i2.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            //dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            sendOrderedBroadcast(i2, null);
            i2.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            sendOrderedBroadcast(i2, null);

        }
        else if(v.getId() == R.id.music_play_pause) {
            dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE));

        }
        else if(v.getId() == R.id.music_next) {
            Log.d(TAG, "Media Button Next Pressed");
            //dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
            i2.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, 87));
            //dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
            sendOrderedBroadcast(i2, null);
        }


    }*/

    @Override
    protected void onStop() {

        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        SpeedFragment sf = new SpeedFragment();
        ft.remove(sf);

        super.onStop();
    }

    @Override
    protected void onPause() {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        SpeedFragment sf = new SpeedFragment();
        ft.remove(sf);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(nReceiver);
        unregisterReceiver(sReceiver);
        //unregisterReceiver(spotReceiver);
      //  unregisterReceiver(pandReceiver);
        this.mWakeLock.release();
        super.onDestroy();

    }

    @Override
    protected void onRestart() {


        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        SpeedFragment sf = new SpeedFragment();
        //  ft.add(R.id.speed_frame, sf).commit();
        ft.add(R.id.map_fragment_frame, sf).commit();


        super.onRestart();
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

                    /*if (packageName.equals("com.iheartradio.connect") ||
                            packageName
                                    .equals("com.clearchannel.iheartradio.controller")) {

                        TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                        TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                        TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                        ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                        tvMusicArtist.setText("Artist : " + notificationTitle);
                        tvMusicTitle.setText("Title : " + notificationText);
                        tvMusicOther.setText(notificationSubText);
                        ivAlbumArt.setImageResource(R.drawable.iheartradio_default);


                    }
*/
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
                 /*   if (packageName.equals("com.spotify.mobile.android.ui")) {

                        TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                        TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                        TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                        ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                        tvMusicArtist.setText("Artist : " + tickerText);
                        tvMusicTitle.setText("Title : " + notificationTitle);
                        tvMusicOther.setText(""/*intent.getStringExtra("notification_sub_text")*//*);


                        ivAlbumArt.setImageResource(R.drawable.spotify_default);

                    }*/
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
                    Log.d(TAG, "Speed : " + extras.getFloat("CURRENT_SPEED"));
                }
            }


        }
    }


  /*  class SpotifyReceiver extends BroadcastReceiver{
//spotify
        @Override
        public void onReceive(Context context, Intent intent) {
            dumpIntent(intent);

            Bundle bundle = intent.getExtras();
            if(bundle != null) {

                TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                tvMusicArtist.setText("Artist : " +bundle.getString("artist"));
               //     bundle.
                tvMusicTitle.setText("Title : " + bundle.getString("track"));
                tvMusicOther.setText("Album : " + bundle.getString("album"));
                ivAlbumArt.setImageResource(R.drawable.spotify_default);



            }
        }
    }
*/

 /*   class PandoraReceiver extends BroadcastReceiver{
        //pandora
        @Override
        public void onReceive(Context context, Intent intent) {
            dumpIntent(intent);

            Bundle bundle = intent.getExtras();
            if(bundle != null) {

                TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                tvMusicArtist.setText("Artist : " + bundle.getString("artist"));
                //     bundle.
                tvMusicTitle.setText("Title : " + bundle.getString("track"));
                tvMusicOther.setText("Album : " + bundle.getString("album"));


                ivAlbumArt.setImageResource(R.drawable.spotify_default);



            }
        }
    }

*/
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