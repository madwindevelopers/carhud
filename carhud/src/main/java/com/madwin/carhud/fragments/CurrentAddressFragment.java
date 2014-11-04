package com.madwin.carhud.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madwin.carhud.R;

public class CurrentAddressFragment extends Fragment {
    public CurrentAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_current_address, container, false);
        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();

    }

}
