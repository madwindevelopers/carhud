package com.madwin.carhud.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.R;


public class NavigationDialogFragment extends DialogFragment implements View.OnClickListener {
    Button yes, no, yes_with_maps;

    public NavigationDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Resources res = getResources();
        final int dividerColor = res.getColor(R.color.DividerGrey);
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = getDialog().findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(dividerColor);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("NAVIGATION");
        View view = inflater.inflate(R.layout.fragment_navigation_dialog, null);
        yes = (Button) view.findViewById(R.id.navigation_true);
        no = (Button) view.findViewById(R.id.navigation_false);
        yes_with_maps = (Button) view.findViewById(R.id.navigation_true_with_maps);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        yes_with_maps.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View view) {
        MapFragment mapFragment = MainActivity.getMapFragment();

        if(view.getId() == R.id.navigation_true) {
            dismiss();
            mapFragment.clearMap();
            mapFragment.showRoute();

        }
        if(view.getId() == R.id.navigation_false) {
            dismiss();
        }
        if(view.getId() == R.id.navigation_true_with_maps) {
            dismiss();
            mapFragment.clearMap();
            mapFragment.showRoute();
            String navURL = "http://maps.google.com/maps?daddr="
                    + mapFragment.getToPosition().latitude + "," +
                    mapFragment.getToPosition().longitude;
            Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navURL));
            startActivity(navIntent);
        }
    }

}


