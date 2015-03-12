package com.madwin.carhud.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.madwin.carhud.MainActivity;
import com.madwin.carhud.R;


public class RefreshRouteFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.refresh_route_fragment, container, false);

        RelativeLayout refreshLayout = (RelativeLayout) view.findViewById(R.id.refresh_route_layout);

        refreshLayout.setOnClickListener(this);
        
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.refresh_route_layout) {
            MainActivity.getMapFragment().updateRoute();
        }
    }

}
