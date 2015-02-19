package com.madwin.carhud.fragments;

import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.madwin.carhud.MainActivity;
import com.madwin.carhud.R;

import java.io.IOException;
import java.util.List;

public class CurrentAddressFragment extends Fragment {

    private final static String TAG = "CurrentAddressFragment";

    TextView currentAddressTextView;
    LatLng currentLocation;
    LatLng previousLocation = new LatLng(0, 0);

    UpdateCurrentLocation updateCurrentLocation;

    SharedPreferences sp;

    int updateCount = 0;

    public CurrentAddressFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(
                MainActivity.getAppContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_current_address, container, false);

        currentAddressTextView = (TextView) v.findViewById(R.id.current_address_fragment_textview);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setCurrentLocation(LatLng currentLocation) {
        int updateInterval = Integer.parseInt(sp.getString("address_update_interval", "5"));

        if (updateInterval == updateCount) {

            this.currentLocation = currentLocation;

            if (previousLocation.latitude != currentLocation.latitude
                    && previousLocation.longitude != currentLocation.longitude) {

                updateCurrentLocation = new UpdateCurrentLocation();
                updateCurrentLocation.execute();
            }
            previousLocation = currentLocation;
        }

       if (updateCount <= updateInterval)
           updateCount++;
       else
           updateCount = 0;
    }

    public LatLng getCurrentLocation() {
        return currentLocation;
    }

    public LatLng getPreviousLocation() {
        return previousLocation;
    }

    private class UpdateCurrentLocation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Log.d(TAG, "<<<Processing current address>>>");
            Geocoder geocoder = new Geocoder(getActivity());

            String addressText = ((TextView) getActivity()
                    .findViewById(R.id.current_address_fragment_textview)).getText().toString();

            Log.d(TAG, "current location" + currentLocation);
            try {
                List <Address> addressList = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);
                Log.d(TAG, "addressList size == " + addressList.size());
                if (addressList.size() > 0) {
                    addressText = addressList.get(0).getAddressLine(0);
                } else {
                    addressText = "";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressText != null) {
                return addressText;
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d(TAG, "<<<post execute>>>");
            currentAddressTextView.setText(result);
            updateCurrentLocation.cancel(true);
        }
    }

}
