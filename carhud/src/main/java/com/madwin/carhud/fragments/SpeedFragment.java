package com.madwin.carhud.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.madwin.carhud.R;


public class SpeedFragment extends Fragment {

    private static final String UNITS_MPH = "MPH";
    private static final String UNITS_KPH = "KPH";

    private double speed;
    private TextView speedTextView;
    private String units = UNITS_MPH;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.speed_fragment, container, false);
        this.speedTextView = (TextView) v.findViewById(R.id.speedometer);
       // RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.speed_frame);

        //mSetLayoutElevation(v, 5);
        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
        switch(units) {
            case UNITS_MPH:
                speedTextView.setText(getSpeedMPH() + " " + units);
                break;
            case UNITS_KPH:
                speedTextView.setText(getSpeedKPH() + " " + units);
                break;
        }
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {

        switch (units) {
            case (UNITS_MPH):
                this.units = units;
                break;
            case (UNITS_KPH):
                this.units = units;
                break;
        }
    }

    private int getSpeedMPH() {
        return (int) (speed * 2.23694);
    }

    private int getSpeedKPH() {
        return (int) (speed * 3.6);
    }

    /*@TargetApi(Build.VERSION_CODES.)  Add L build API 21
    private void mSetLayoutElevation(View v, int elevation) {
        v.setElevation(5);
    }*/
}
