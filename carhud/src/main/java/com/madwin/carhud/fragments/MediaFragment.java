package com.madwin.carhud.fragments;

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

    private String currentApplicationPackage;
    private String applicationName;
    private String mediaTitle;
    private String mediaText;
    private String mediaSubText;
    private ImageView mediaIV;
    private Drawable appIcon;

    private TextView mediaTitleTV;
    private TextView mediaTextTV;
    private TextView mediaSubTextTV;

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
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getMediaTitle() {
        return mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
        mediaTitleTV.setText(this.mediaTitle);
    }

    public String getMediaText() {
        return mediaText;
    }

    public void setMediaText(String mediaText) {
        this.mediaText = mediaText;
        mediaTextTV.setText(this.mediaText);
    }

    public String getMediaSubText() {
        return mediaSubText;
    }

    public void setMediaSubText(String mediaSubText) {
        this.mediaSubText = mediaSubText;
        mediaSubTextTV.setText(this.mediaSubText);
    }

    public ImageView getMediaIV() {
        return mediaIV;
    }

    public void setMediaIV(ImageView mediaIV) {
        this.mediaIV = mediaIV;
    }

    public TextView getMediaTitleTV() {
        return mediaTitleTV;
    }

    public TextView getMediaTextTV() {
        return mediaTextTV;
    }

    public TextView getMediaSubTextTV() {
        return mediaSubTextTV;
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

}
