package com.madwin.carhud.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madwin.carhud.R;


public class RefreshRouteFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       // RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.speed_frame);

        //mSetLayoutElevation(v, 5);
        return inflater.inflate(R.layout.refresh_route_fragment, container, false);
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

    /*@TargetApi(Build.VERSION_CODES.)  Add L build API 21
    private void mSetLayoutElevation(View v, int elevation) {
        v.setElevation(5);
    }*/
}
