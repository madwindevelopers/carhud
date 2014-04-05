package com.madwin.carhud;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class NavigationDialogFragment extends DialogFragment {
    public NavigationDialogFragment() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_dialog, null);
    }
}
