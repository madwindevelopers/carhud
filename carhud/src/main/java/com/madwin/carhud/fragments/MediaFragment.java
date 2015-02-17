package com.madwin.carhud.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.madwin.carhud.R;
import com.madwin.carhud.utils.RoundAppIcon;

public class MediaFragment extends Fragment {


    public static final String MEDIA_INTENT = "COM.MADWIN.CARHUD.MEDIA.INTENT";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    public static final String PACKAGE_LABEL = "PACKAGE_LABEL";
    public static final String SONG_NAME = "SONG_NAME";
    public static final String ARTIST = "ARTIST";
    public static final String ALBUM = "ALBUM";

    private String currentApplicationPackage;
    private String mediaTitle;
    private String mediaText;
    private String mediaSubText;
    private ImageView mediaIV;
    private Drawable appIcon;

    private TextView mediaTitleTV;
    private TextView mediaTextTV;
    private TextView mediaSubTextTV;

    private MediaReceiver mediaReceiver = new MediaReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(MEDIA_INTENT);
        getActivity().registerReceiver(mediaReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_fragment, container, false);

        mediaTitleTV = (TextView) view.findViewById(R.id.music_title);
        mediaTextTV = (TextView) view.findViewById(R.id.music_text);
        mediaSubTextTV = (TextView) view.findViewById(R.id.music_subtext);
        mediaIV = (ImageView) view.findViewById(R.id.album_art);
        appIcon = getActivity().getResources().getDrawable(R.drawable.ic_media_play);
        mediaIV.setImageDrawable(new RoundAppIcon(appIcon));

        return view;
    }

    public String getCurrentApplicationPackage() {
        return currentApplicationPackage;
    }

    public void setCurrentApplicationPackage(String currentApplicationPackage) {
        this.currentApplicationPackage = currentApplicationPackage;
        setCurrentApplicationIcon();
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
        mediaTitleTV.setText(this.mediaTitle);
    }

    public void setMediaText(String mediaText) {
        this.mediaText = mediaText;
        mediaTextTV.setText(this.mediaText);
    }

    public void setMediaSubText(String mediaSubText) {
        this.mediaSubText = mediaSubText;
        mediaSubTextTV.setText(this.mediaSubText);
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public void setCurrentApplicationIcon() {
        try {
            setAppIcon(getActivity().getPackageManager().getApplicationIcon(getCurrentApplicationPackage()));
            mediaIV.setImageDrawable(new RoundAppIcon(getAppIcon()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    class MediaReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!(intent.getStringExtra(PACKAGE_NAME) == null))
                setCurrentApplicationPackage(intent.getStringExtra(PACKAGE_NAME));

            if(!(intent.getStringExtra(SONG_NAME) == null))
                setMediaTitle(intent.getStringExtra(SONG_NAME));

            if(!(intent.getStringExtra(ARTIST) == null))
                setMediaText(intent.getStringExtra(ARTIST));

            if(!(intent.getStringExtra(ALBUM) == null))
                setMediaSubText(intent.getStringExtra(ALBUM));

        }
    }


}
