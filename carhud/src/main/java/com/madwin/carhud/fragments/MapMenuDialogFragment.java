package com.madwin.carhud.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
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

    private Drawable unChecked;
    private Drawable checked;

    public MapMenuDialogFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Base_V21_Theme_AppCompat_Light_Dialog);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Base_Theme_AppCompat_Light_Dialog);

        setCheckBoxDrawables();

        mapFragment = MainActivity.getMapFragment();
    }

    @Override
    public void onStart() { super.onStart(); }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_menu_dialog, null);

        trafficCB = (CheckBox) view.findViewById(R.id.traffic_check_box);
        trafficCB.setOnClickListener(this);
        trafficCB.setChecked(mapFragment.isTrafficEnabled());
        setCheckBoxColor(trafficCB);

        satteliteCB = (CheckBox) view.findViewById(R.id.satellite_check_box);
        satteliteCB.setOnClickListener(this);
        satteliteCB.setChecked(mapFragment.isSatelliteEnabled());
        setCheckBoxColor(satteliteCB);

        hybridCB = (CheckBox) view.findViewById(R.id.hybrid_check_box);
        hybridCB.setOnClickListener(this);
        hybridCB.setChecked(mapFragment.isHybridEnabled());
        setCheckBoxColor(hybridCB);

        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.traffic_check_box:
                mapFragment.toggleTrafficEnabled();
                trafficCB.setChecked(mapFragment.isTrafficEnabled());
                setCheckBoxColor(trafficCB);
                break;

            case R.id.satellite_check_box:
                mapFragment.toggleSatelliteEnabled();
                satteliteCB.setChecked(mapFragment.isSatelliteEnabled());
                setCheckBoxColor(satteliteCB);
                hybridCB.setChecked(mapFragment.isHybridEnabled());
                setCheckBoxColor(hybridCB);
                break;

            case R.id.hybrid_check_box:
                mapFragment.toggleHybridEnabled();
                hybridCB.setChecked(mapFragment.isHybridEnabled());
                setCheckBoxColor(hybridCB);
                satteliteCB.setChecked(mapFragment.isSatelliteEnabled());
                setCheckBoxColor(satteliteCB);
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setCheckBoxColor(CheckBox checkBox) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (checkBox.isChecked())
                checkBox.setButtonTintList(getResources().getColorStateList(R.color.checkbox_color_state_checked));
            else
                checkBox.setButtonTintList(getResources().getColorStateList(R.color.checkbox_color_state_unchecked));
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            if (checkBox.isChecked())
                checkBox.setButtonDrawable(checked);
            else
                checkBox.setButtonDrawable(unChecked);
        }
    }

    private void setCheckBoxDrawables() {

        PorterDuff.Mode srcIn = PorterDuff.Mode.SRC_IN;

        unChecked = getResources().getDrawable(R.drawable.abc_btn_check_to_on_mtrl_000);
        unChecked.setColorFilter(getResources().getColor(R.color.Grey600), srcIn);

        checked = getResources().getDrawable(R.drawable.abc_btn_check_to_on_mtrl_015);
        checked.setColorFilter(getResources().getColor(R.color.DeepOrangeA400), srcIn);

    }
}


