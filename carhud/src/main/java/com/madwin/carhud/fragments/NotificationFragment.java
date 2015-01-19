package com.madwin.carhud.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madwin.carhud.R;

public class NotificationFragment extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        Log.d("carhud", "top notification loaded");

    	return inflater.inflate(R.layout.notification_fragment, container, false);

	}

}

