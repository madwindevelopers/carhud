package com.madwin.carhud.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.R;


public class MapMenuDialogFragment extends DialogFragment implements View.OnClickListener {

    private CheckBox trafficCB;

    public MapMenuDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Base_V21_Theme_AppCompat_Light_Dialog);

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

        trafficCB.setChecked(MainActivity.getMapFragment().isTrafficEnabled());

        return view;
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.traffic_check_box) {
            MainActivity.getMapFragment().toggleTrafficEnabled();
            trafficCB.setChecked(MainActivity.getMapFragment().isTrafficEnabled());
        }
    }
}


