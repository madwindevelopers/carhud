package com.madwin.carhud.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.madwin.carhud.R;

public class MapMenuFragment extends Fragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_menu_fragment, container, false);

        RelativeLayout menu = (RelativeLayout) view.findViewById(R.id.map_menu_layout);
        menu.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        MapMenuDialogFragment mmdf = new MapMenuDialogFragment();
        mmdf.show(getActivity().getFragmentManager(), "MapMenu");
    }
}
