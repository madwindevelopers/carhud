package com.madwin.carhud.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.madwin.carhud.MainActivity;
import com.madwin.carhud.R;
import com.madwin.carhud.maps.CarHUDMap;
import com.madwin.carhud.maps.GMapV2Direction;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class MapFragment extends Fragment {

    GoogleMap map;
    String TAG = "MapFragment";

    private FragmentManager fm;
    private FragmentTransaction ft;
    private SpeedFragment speedFragment = new SpeedFragment();
    private RefreshRouteFragment refreshRouteFragment = new RefreshRouteFragment();
    private CurrentAddressFragment currentAddressFragment = new CurrentAddressFragment();

    LocationManager locationManager;
    LocationListener locationListener;
    private LatLng currentLocation;
    float CURRENT_BEARING = 0;
    float ZOOM_LEVEL = 6;

    private Boolean MyLocationClicked = true;
    SharedPreferences sp;
    SupportMapFragment thisMap;
    GMapV2Direction md;

    private LatLng fromPosition, toPosition;

//    private int addressBroadcastCounter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.map_fragment, container, false);

        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location != null) {

            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        } else {
            currentLocation = new LatLng(0, 0);
        }

        thisMap = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mv);
        if (thisMap != null ) {
            mSetupMap();
        } else {
            Log.d(TAG, "<<<<<<<<<<<<<<<<<<supportmapfragment == null>>>>>>>>>>>>>>>>>>");
        }

        CameraPosition cp = new CameraPosition(currentLocation, ZOOM_LEVEL,
                                  CarHUDMap.getMaximumTilt(ZOOM_LEVEL), 0);

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cp), 1000, null);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (location.getSpeed() > 2.2352)
                    CURRENT_BEARING = location.getBearing();

                ZOOM_LEVEL = CarHUDMap.speedBasedZoom(location.getSpeed(),
                        map.getCameraPosition().zoom);

                LatLng adjustedLocation = new CarHUDMap().getAdjustedCoordinates(map,
                        location, CURRENT_BEARING, getActivity());

                if (MyLocationClicked) {
                    final CameraPosition cameraPosition = new CameraPosition(adjustedLocation,
                            ZOOM_LEVEL,
                            CarHUDMap.getMaximumTilt(ZOOM_LEVEL),
                            CURRENT_BEARING);
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    Integer.parseInt(sp.getString("map_animation_speed", "900")), null);
                }
                currentAddressFragment.setCurrentLocation(getLocationLatLng());
                speedFragment.setSpeed(location.getSpeed());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {
                locationManager.removeUpdates(locationListener);
            }
        };
        startLocationListener();

        // Inflate the view for this fragment
        return v;
    }

    private void mSetupMap() {

        map = thisMap.getMap();
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(myLocationListener);
        map.setOnMapClickListener(mapClickListener);
        map.setOnMapLongClickListener(mapLongClickListener);


        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();

        ft.add(R.id.map_fragment_frame, refreshRouteFragment);
        ft.add(R.id.map_fragment_frame, speedFragment);
        ft.add(R.id.map_fragment_frame, currentAddressFragment);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    private GoogleMap.OnMyLocationButtonClickListener myLocationListener = new OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            Log.d(TAG, "onMyLocationButtonClick");
            MyLocationClicked = true;
            return false;
        }
    };

    private GoogleMap.OnMapClickListener mapClickListener = new GoogleMap.OnMapClickListener() {
        @Override
        public void onMapClick(LatLng latLng) {
            Log.d(TAG, "onMapClick");
            MyLocationClicked = false;
        }
    };

    private GoogleMap.OnMapLongClickListener mapLongClickListener = new GoogleMap.OnMapLongClickListener() {
        @Override
        public void onMapLongClick(LatLng latLng) {
            setFromPosition(getLocationLatLng());
            setToPosition(latLng);
            showNavigationDialog();
        }
    };

    public void showNavigationDialog() {
        NavigationDialogFragment navigationDialogFragment = new NavigationDialogFragment();
        navigationDialogFragment.show(getActivity().getFragmentManager(), "NavigationDialog");
    }

    public LatLng getLocationLatLng() {
        return currentLocation;
    }

    class showRoute extends AsyncTask<Void, Void , Document> {

        Document doc;
        PolylineOptions rectLine;

        @Override
        protected Document doInBackground(Void ...params) {

            md = new GMapV2Direction();
            doc = md.getDocument(fromPosition, toPosition, GMapV2Direction.MODE_DRIVING);

            ArrayList<LatLng> directionPoint = md.getDirection(doc);
            rectLine = new PolylineOptions().width(7).color(Color.RED);

            for (LatLng aDirectionPoint : directionPoint) {
                rectLine.add(aDirectionPoint);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Document result) {
            map.addPolyline(rectLine);
        }
    }

    public void updateRoute() {

        fromPosition = currentLocation;
        if (toPosition != null) {
            map.clear();
            md = new GMapV2Direction();
            new showRoute().execute();
        }
    }

    public void clearMap() {
        map.clear();
    }

    public void showRoute(LatLng fromPosition, LatLng toPosition) {
        setFromPosition(fromPosition);
        setToPosition(toPosition);
        new showRoute().execute();
    }

    public void showRoute(LatLng toPosition) {
        setToPosition(toPosition);
        new showRoute().execute();
    }

    public void showRoute() {
        new showRoute().execute();
    }

    public void stopLocationListener() {
        locationManager.removeUpdates(locationListener);
    }

    public void startLocationListener() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    public void setToPosition(LatLng latLng) {
        toPosition = latLng;
    }

    public LatLng getToPosition() { return toPosition; }

    public void setFromPosition(LatLng latLng) {
        fromPosition = latLng;
    }
}
