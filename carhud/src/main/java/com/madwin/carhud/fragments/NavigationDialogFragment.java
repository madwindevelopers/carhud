package com.madwin.carhud.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.madwin.carhud.R;


public class NavigationDialogFragment extends DialogFragment implements View.OnClickListener {
    Button yes, no, yes_with_maps;
    Communicator communicator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    public NavigationDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().setTitle("NAVIGATION");
        View view = inflater.inflate(R.layout.fragment_navigation_dialog, null);
        yes = (Button) view.findViewById(R.id.navigation_true);
        no = (Button) view.findViewById(R.id.navigation_false);
        yes_with_maps = (Button) view.findViewById(R.id.navigation_true_with_maps);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        yes_with_maps.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.navigation_true) {
            dismiss();
            communicator.onDialogMessage("Yes Clicked");

        }
        if(view.getId() == R.id.navigation_false) {
            dismiss();
        }
        if(view.getId() == R.id.navigation_true_with_maps) {
            communicator.onDialogMessage("navigate_with_maps");
            dismiss();
        }
    }

    public interface Communicator {
        public void onDialogMessage(String message);
    }
}


