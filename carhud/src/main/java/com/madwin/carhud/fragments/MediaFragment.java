package com.madwin.carhud.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.madwin.carhud.R;

public class MediaFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.mediafragment, container, false);

/*
        TextView title = (TextView) v.findViewById(R.id.nt_title);
        title.setText(nt_title);
        TextView text = (TextView) v.findViewById(R.id.nt_text);
        text.setText(nt_text);
        TextView subtext = (TextView) v.findViewById(R.id.nt_subtext);
        subtext.setText(nt_subtext);
        // Inflate the layout for this fragment*/

  /*      Button back = (Button) getView().findViewById(R.id.music_previous);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             //preform action on click

            }
        });

        Button play_pause = (Button) getView().findViewById(R.id.music_play_pause);
        play_pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //preform action on click

            }
        });

        Button next = (Button) getView().findViewById(R.id.music_next);
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //preform action on click

            }
        });*/
        return v;
    }
}
