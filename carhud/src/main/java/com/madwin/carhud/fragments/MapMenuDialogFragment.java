package com.madwin.carhud.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.R;


public class MapMenuDialogFragment extends DialogFragment implements View.OnClickListener {

    private CheckBox trafficCB;
    private CheckBox satteliteCB;
    private CheckBox hybridCB;
    private MapFragment mapFragment;

    public MapMenuDialogFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Base_V21_Theme_AppCompat_Light_Dialog);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Base_Theme_AppCompat_Light_Dialog);

        mapFragment = MainActivity.getMapFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("NAVIGATION");
        View view = inflater.inflate(R.layout.map_menu_dialog, null);

        trafficCB = (CheckBox) view.findViewById(R.id.traffic_check_box);
        trafficCB.setOnClickListener(this);
        trafficCB.setChecked(mapFragment.isTrafficEnabled());

        satteliteCB = (CheckBox) view.findViewById(R.id.satellite_check_box);
        satteliteCB.setOnClickListener(this);
        satteliteCB.setChecked(mapFragment.isSatelliteEnabled());

        hybridCB = (CheckBox) view.findViewById(R.id.hybrid_check_box);
        hybridCB.setOnClickListener(this);
        hybridCB.setChecked(mapFragment.isHybridEnabled());

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.traffic_check_box:
                mapFragment.toggleTrafficEnabled();
                trafficCB.setChecked(mapFragment.isTrafficEnabled());
                break;
            case R.id.satellite_check_box:
                mapFragment.toggleSatelliteEnabled();
                satteliteCB.setChecked(mapFragment.isSatelliteEnabled());
                hybridCB.setChecked(mapFragment.isHybridEnabled());
                break;
            case R.id.hybrid_check_box:
                mapFragment.toggleHybridEnabled();
                hybridCB.setChecked(mapFragment.isHybridEnabled());
                satteliteCB.setChecked(mapFragment.isSatelliteEnabled());
                break;
        }

    }
}


