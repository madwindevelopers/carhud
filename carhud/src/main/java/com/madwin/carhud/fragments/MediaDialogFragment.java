package com.madwin.carhud.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.madwin.carhud.R;


public class MediaDialogFragment extends DialogFragment implements View.OnClickListener {
    private Button apps, previous, next, play, pause;
    private Communicator communicator;

    public MediaDialogFragment() { }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog);
    }

    @Override
    public void onStart() {
        super.onStart();
        final Resources res = getResources();
        final int dividerColor = res.getColor(R.color.DividerGrey);
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = getDialog().findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(dividerColor);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (getDialog() != null) {
            getDialog().setTitle(getResources().getString(R.string.media_controls_caps));
        }
        View view = inflater.inflate(R.layout.media_dialog_fragment, null);
        if (view != null) {
            apps = (Button) view.findViewById(R.id.app_selector);
            previous = (Button) view.findViewById(R.id.media_previous);
            pause = (Button) view.findViewById(R.id.media_pause);
            play = (Button) view.findViewById(R.id.media_play);
            next = (Button) view.findViewById(R.id.media_next);
            apps.setOnClickListener(this);
            previous.setOnClickListener(this);
            pause.setOnClickListener(this);
            play.setOnClickListener(this);
            next.setOnClickListener(this);
            return view;
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.app_selector) {
            this.dismiss();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mSendMessage("APP_SELECTOR");
        }
        if(view.getId() == R.id.media_previous)
        {
            mSendMessage("PREVIOUS_CLICKED");
        }
        if(view.getId() == R.id.media_pause)
        {
           mSendMessage("PAUSE_CLICKED");
        }
        if(view.getId() == R.id.media_play)
        {
            mSendMessage("PLAY_CLICKED");
        }
        if(view.getId() == R.id.media_next)
        {
            mSendMessage("NEXT_CLICKED");
        }
    }

    private void mSendMessage (String string) {
        communicator.onDialogMessage(string);
    }

    public interface Communicator {
        public void onDialogMessage(String message);
    }
}


