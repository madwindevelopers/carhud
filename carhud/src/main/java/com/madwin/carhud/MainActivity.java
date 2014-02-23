package com.madwin.carhud;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends FragmentActivity {

    private NotificationReceiver nReceiver;
    private String TAG = "carhud";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.madwin.carhud.NOTIFICATION_LISTENER");
        registerReceiver(nReceiver,filter);

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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {


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
                        Log.d(TAG, "Package Name = " + packageName);
                        tv_package.setText(packageName);
                        tv_title.setText(notificationTitle);
                        tv_text.setText(notificationText);
                        tv_sub_text.setText(notificationSubText);
                    }

                    if (packageName.equals("com.iheartradio.connect") ||
                            packageName
                                    .equals("com.clearchannel.iheartradio.controller")) {

                        TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                        TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                        TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                        ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                        tvMusicArtist.setText(notificationTitle);
                        tvMusicTitle.setText(notificationText);
                        tvMusicOther.setText(notificationSubText);
                        ivAlbumArt.setImageBitmap(bmp);


                    }

                    if (packageName
                            .equals("com.pandora.android")) {

                        TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                        TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                        TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);

                        tvMusicTitle.setText(notificationTitle);
                        tvMusicArtist.setText(notificationText);
                        tvMusicOther.setText("");

                    }

                    if (packageName.equals("com.spotify.mobile.android.ui")) {

                        TextView tvMusicArtist = (TextView) findViewById(R.id.music_title);
                        TextView tvMusicTitle = (TextView) findViewById(R.id.music_text);
                        TextView tvMusicOther = (TextView) findViewById(R.id.music_subtext);
                        ImageView ivAlbumArt = (ImageView) findViewById(R.id.album_art);

                        tvMusicArtist.setText(tickerText);
                        tvMusicTitle.setText(notificationTitle);
                        tvMusicOther.setText(""/*intent.getStringExtra("notification_sub_text")*/);


                        ivAlbumArt.setImageBitmap(bmp);

                    }
                }

            }



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