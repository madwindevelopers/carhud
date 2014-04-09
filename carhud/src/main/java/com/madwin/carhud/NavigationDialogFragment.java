package com.madwin.carhud;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NavigationDialogFragment extends DialogFragment implements View.OnClickListener {
    Button yes, no;
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
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.navigation_true)
        {
            dismiss();
            communicator.onDialogMessage("Yes Clicked");

        }
        if(view.getId() == R.id.navigation_false)
        {
            dismiss();
        }
    }

    interface Communicator {
        public void onDialogMessage(String message);
    }
}


