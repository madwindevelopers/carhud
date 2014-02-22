package com.madwin.carhud;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TopNotificationFragment extends Fragment{
	
	TextView title;
	TextView text;
	TextView subtext;

	String nt_title;
	String nt_text;
	String nt_subtext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

        Log.d("carhud", "top notification loaded");
    	
    	//Bundle bundle = new Bundle();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        nt_title = preferences.getString("notification_title", "no_title_in_preference");
        nt_text = preferences.getString("notification_text", "no_text_in_preference");
        nt_subtext = preferences.getString("notification_subtext", "no_subtext_in_preference");
            
    	View v = inflater.inflate(R.layout.top_notification_fragment, container, false);
        title = (TextView) v.findViewById(R.id.nt_title);
        title.setText(nt_title);
        text = (TextView) v.findViewById(R.id.nt_text);
        text.setText(nt_text);
        subtext = (TextView) v.findViewById(R.id.nt_subtext);
        subtext.setText(nt_subtext);
    	//Retrieve ui elements
    	//title = (TextView) v.findViewById(R.id.nt_title);
        //text = (TextView) v.findViewById(R.id.nt_text);
        //subtext = (TextView) v.findViewById(R.id.nt_subtext);
        //largeIcon = (ImageView) getView().findViewById(R.id.nt_largeicon);
      
    //    repeat();

        // Inflate the layout for this fragment
        return v;

	}

}

