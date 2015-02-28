package com.madwin.carhud.fragments;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.madwin.carhud.R;
import com.madwin.carhud.utils.RoundAppIcon;

public class MediaFragment extends Fragment implements View.OnClickListener{

    public static final String MEDIA_INTENT = "COM.MADWIN.CARHUD.MEDIA.INTENT";
    public static final String PACKAGE_NAME = "PACKAGE_NAME";
    public static final String PACKAGE_LABEL = "PACKAGE_LABEL";
    public static final String Track = "Track";
    public static final String ARTIST = "ARTIST";
    public static final String ALBUM = "ALBUM";

    private String currentApplicationPackage;
    private String mediaTrack;
    private String mediaArtist;
    private String mediaAlbum;
    private ImageView mediaAlbumIV;
    private Drawable appIcon;

    private TextView mediaTrackTV;
    private TextView mediaArtistTV;
    private TextView mediaAlbumTV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.media_fragment, container, false);

        mediaTrackTV = (TextView) view.findViewById(R.id.music_title);
        mediaArtistTV = (TextView) view.findViewById(R.id.music_text);
        mediaAlbumTV = (TextView) view.findViewById(R.id.music_subtext);
        mediaAlbumIV = (ImageView) view.findViewById(R.id.album_art);
        appIcon = getActivity().getResources().getDrawable(R.drawable.ic_media_play);
        mediaAlbumIV.setImageDrawable(new RoundAppIcon(appIcon));

        mediaAlbumIV.setOnClickListener(this);

        return view;
    }

    public String getCurrentApplicationPackage() {
        return currentApplicationPackage;
    }

    public void setCurrentApplicationPackage(String currentApplicationPackage) {
        this.currentApplicationPackage = currentApplicationPackage;
        setCurrentApplicationIcon();
    }

    public void setMediaTrack(String mediaTitle) {
        this.mediaTrack = mediaTitle;
        mediaTrackTV.setText(this.mediaTrack);
    }

    public void setMediaArtist(String mediaText) {
        this.mediaArtist = mediaText;
        mediaArtistTV.setText(this.mediaArtist);
    }

    public void setMediaAlbum(String mediaSubText) {
        this.mediaAlbum = mediaSubText;
        mediaAlbumTV.setText(this.mediaAlbum);
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public void setCurrentApplicationIcon() {
        try {
            setAppIcon(getActivity().getPackageManager().
                    getApplicationIcon(getCurrentApplicationPackage()));
            mediaAlbumIV.setImageDrawable(new RoundAppIcon(getAppIcon()));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void openApplication() {
        if (getCurrentApplicationPackage() != null) {
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
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.album_art) {
            openApplication();
        }
    }
}
