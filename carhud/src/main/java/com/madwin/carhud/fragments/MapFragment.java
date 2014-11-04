package com.madwin.carhud.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.madwin.carhud.MainActivity;
import com.madwin.carhud.PreferencesActivity;
import com.madwin.carhud.R;
import com.madwin.carhud.maps.CarHUDMap;

public class MapFragment extends Fragment {

    GoogleMap map;
    String TAG = "MapFragment";

    LocationManager locationManager;
    LocationListener locationListener;
    LatLng CURRENT_LOCATION;// = new LatLng(43.023919, -87.913506);
    float CURRENT_BEARING = 0;
    float ZOOM_LEVEL = 6;
    Boolean MyLocationClicked = true;
    SharedPreferences sp;

    private int addressBroadcastCounter = 0;

    public final static String CURRENT_LOCATION_INTENT_FILTER = "com.madwin.carhud.CURRENT_LOCATION_FOR_ADDRESS";
    public final static String CURRENT_LOCATION_FILTER = "LOCATION";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.mapfragment, container, false);


        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {

            CURRENT_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());

        } else {
            CURRENT_LOCATION = new LatLng(0, 0);
        }

        map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mv)).getMap(); // Obtain the map from a MapFragment or MapView.
        map.setMyLocationEnabled(true);

        map.setOnMyLocationButtonClickListener(myLocationListener);
        map.setOnMapClickListener(mapClickListener);
        map.setOnMapLongClickListener(mapLongClickListener);

        CameraPosition cp = new CameraPosition(CURRENT_LOCATION, ZOOM_LEVEL,
                CarHUDMap.getMaximumTilt(ZOOM_LEVEL), 0);

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 1000, null);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
/*              CheckBox debugbearcheck = (CheckBox)v.findViewById(R.id.bearcheckBox);
                CheckBox debugzoomcheck = (CheckBox)v.findViewById(R.id.zoomcheckBox);

                if (debugbearcheck.isChecked()) {
                    EditText bearing = (EditText)v.findViewById(R.id.bearing_editText);
                    Log.e(TAG, "bearing text = " + bearing.getText().toString());
                    //NEed to test Bearing
                    try{
                        CURRENT_BEARING = Float.parseFloat(bearing.getText().toString());
                    } catch(NumberFormatException e) {
                        CURRENT_BEARING = 0;
                    }
                } else
*/
                if (location.getSpeed() > 2.2352) {
                    CURRENT_BEARING = location.getBearing();
                }
/*
                if (debugzoomcheck.isChecked()) {
                    EditText zoom = (EditText)v.findViewById(R.id.zoom_editText);
                    try {
                        ZOOM_LEVEL = Float.parseFloat(zoom.getText().toString());
                    } catch(NumberFormatException e) {
                        ZOOM_LEVEL = map.getCameraPosition().zoom;
                    }
                } else
*/
                ZOOM_LEVEL = CarHUDMap.speedBasedZoom(location.getSpeed(), map.getCameraPosition().zoom);

                // Log.e(TAG, "coordinate going into = " + location.getLatitude() + " / " + location.getLongitude());

                //Log.e(TAG, "coordinate coming out = " + CURRENT_LOCATION.latitude + " / " + CURRENT_LOCATION.longitude);


                CURRENT_LOCATION = new CarHUDMap().getAdjustedCoordinates(map, location, CURRENT_BEARING, getActivity());
                // Log.e(TAG, "Current location after adjustement = " + CURRENT_LOCATION.latitude + "/" + CURRENT_LOCATION.longitude);
                if (MyLocationClicked) {
                    final CameraPosition cameraPosition = new CameraPosition(CURRENT_LOCATION,
                            ZOOM_LEVEL,
                            CarHUDMap.getMaximumTilt(ZOOM_LEVEL),
                            CURRENT_BEARING);
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                            Integer.parseInt(sp.getString("map_animation_speed", "900")), null);
                }

                mBroadcastLocationForAddress();

                mSendSpeed(location.getSpeed());
                mSendLocation();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Log.d("carhud", "status changed");
            }

            @Override
            public void onProviderEnabled(String provider) {
                //Log.d("carhud", "provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                //Log.d("carhud", "provider disabled");
                locationManager.removeUpdates(locationListener);
            }
        };

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }

        // Inflate the view for this fragment
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.d(TAG, "MapFragment Destroyed");

        locationManager.removeUpdates(locationListener);

    }

    private GoogleMap.OnMyLocationButtonClickListener myLocationListener = new OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            // Log.d(TAG, "onMyLocationButtonClick");
            MyLocationClicked = true;
            return false;
        }
    };

    private GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            //Log.d(TAG, "onMapClick");
            MyLocationClicked = false;
        }
    };

    private GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            Intent i = new Intent("com.madwin.carhud.MAP_LONG_CLICK");
            i.putExtra("Latitude", latLng.latitude);
            i.putExtra("Longitude", latLng.longitude);
            getActivity().sendBroadcast(i);

        }
    };

    public void mSendSpeed(float speed) {
        Intent i = new Intent("com.madwin.carhud.SPEED_LISTENER");
        i.putExtra("CURRENT_SPEED", speed);
        getActivity().sendBroadcast(i);
    }

    public void mSendLocation() {
        Intent i = new Intent("com.madwin.carhud.LOCATION_LISTENER");
        i.putExtra("LATITUDE", CURRENT_LOCATION.latitude);
        i.putExtra("LONGITUDE", CURRENT_LOCATION.longitude);
        getActivity().sendBroadcast(i);

    }

    private void mBroadcastLocationForAddress() {
        //Log.d(TAG, "Broadcast Locatoin");
        //Log.d(TAG, "addressBroadcastCounter == " + addressBroadcastCounter);
        if (addressBroadcastCounter == 0) {
            if (CURRENT_LOCATION != null) {
                Intent i = new Intent(CURRENT_LOCATION_INTENT_FILTER);
                i.putExtra(CURRENT_LOCATION_FILTER, CURRENT_LOCATION);
                getActivity().sendBroadcast(i);
            }
        }

        addressBroadcastCounter++;

        //Log.d(TAG, "Interval == " + sp.getString(PreferencesActivity.CURRENT_ADDRESS_UPDATE_INTERVAL_KEY, "5"));

        if (addressBroadcastCounter >= Integer.parseInt(sp.getString(
                PreferencesActivity.CURRENT_ADDRESS_UPDATE_INTERVAL_KEY, "5"))) {

            addressBroadcastCounter = 0;

        }

    }

}
