package com.madwin.carhud.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.R;


public class RefreshRouteFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.refresh_route_fragment, container, false);

        Button refreshButton = (Button) view.findViewById(R.id.refresh_button);

        refreshButton.setOnClickListener(this);

       // RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.speed_frame);

        //mSetLayoutElevation(v, 5);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.refresh_button) {
            MainActivity.getMapFragment().updateRoute();
        }
    }

    /*@TargetApi(Build.VERSION_CODES.)  Add L build API 21
    private void mSetLayoutElevation(View v, int elevation) {
        v.setElevation(5);
    }*/
}
