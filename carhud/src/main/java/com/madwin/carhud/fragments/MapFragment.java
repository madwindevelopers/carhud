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
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

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
    private MapMenuFragment mapMenuFragment = new MapMenuFragment();

    private int animationSpeed;

    LocationManager locationManager;
    LocationListener locationListener;
    private LatLng currentLocation;
    private LatLng adjustedLocation;
    float CURRENT_BEARING = 0;
    float ZOOM_LEVEL = 6;

    private boolean routeIsVisible = false;

    private Boolean MyLocationClicked = true;
    SharedPreferences sp;
    SupportMapFragment thisMap;
    GMapV2Direction md;

    private LatLng fromPosition, toPosition;

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

        locationManager = (LocationManager) this.getActivity().
                getSystemService(Context.LOCATION_SERVICE);

        thisMap = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mv);

        if (thisMap != null ) {
            mSetupMap();
        } else {
            Log.d(TAG, "<<<<<<<<<<<<<<<<<<supportmapfragment == null>>>>>>>>>>>>>>>>>>");
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (location.getSpeed() > 2.2) // doesn't update bearing when speed is under # m/s
                    CURRENT_BEARING = location.getBearing();

                ZOOM_LEVEL = CarHUDMap.speedBasedZoom(location.getSpeed(),
                        map.getCameraPosition().zoom);

                setAdjustedLocation(new CarHUDMap().getAdjustedCoordinates(map,
                        location, CURRENT_BEARING, getActivity()));

                setAnimationSpeed(Integer.parseInt(sp.getString("map_animation_speed", "900")));

                if (MyLocationClicked) {
                    final CameraPosition cameraPosition = new CameraPosition(
                            getAdjustedLocation(),
                            ZOOM_LEVEL,
                            CarHUDMap.getMaximumTilt(ZOOM_LEVEL),
                            CURRENT_BEARING);
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    getAnimationSpeed(), null);
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
        ft.add(R.id.map_fragment_frame, mapMenuFragment);
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
           setRouteIsVisible(true);
        }
    }

    public void clearMap() {
        map.clear();

        if (routeIsVisible)
            shakeMap();

        setRouteIsVisible(false);
    }

    public boolean getRouteIsVisible() {
        return routeIsVisible;
    }

    public void setRouteIsVisible(boolean routeIsVisible) {
        this.routeIsVisible = routeIsVisible;
    }

    public void showRoute(LatLng fromPosition, LatLng toPosition) {
        setFromPosition(fromPosition);
        setToPosition(toPosition);
        new showRoute().execute();
        setRouteIsVisible(true);
    }

    public void showRoute(LatLng toPosition) {
        setToPosition(toPosition);
        new showRoute().execute();
        setRouteIsVisible(true);
    }

    public void showRoute() {
        new showRoute().execute();
        setRouteIsVisible(true);
    }

    public void stopLocationListener() {
        locationManager.removeUpdates(locationListener);
    }

    public void startLocationListener() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    public void setToPosition(LatLng latLng) {
        toPosition = latLng;
    }

    public LatLng getToPosition() { return toPosition; }

    public void setFromPosition(LatLng latLng) {
        fromPosition = latLng;
    }

    public void setAdjustedLocation(LatLng adjustedLocation) {
        this.adjustedLocation = adjustedLocation;
    }

    public LatLng getAdjustedLocation() { return this.adjustedLocation;}

    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public int getAnimationSpeed() {
        return this.animationSpeed;
    }

    public void toggleTrafficEnabled() {
        map.setTrafficEnabled(!map.isTrafficEnabled());
    }

    public boolean isTrafficEnabled() {
        return map.isTrafficEnabled();
    }

    public void writeToFile() {
        String string;
        string = currentLocation.latitude + ", " + currentLocation.longitude + ", " +
                getAdjustedLocation().latitude + ", " + getAdjustedLocation().longitude + ", " +
                CURRENT_BEARING + ", " + ZOOM_LEVEL;


    }

    public void shakeMap() {
        View view = getActivity().getWindow().getDecorView().findViewById(R.id.map_fragment_layout);

        int duration = 50;

        AnimationSet animationSet = new AnimationSet(true);

        TranslateAnimation animationToLeft = new TranslateAnimation(0, -100, 0, 0);
        animationToLeft.setFillEnabled(false);
        animationToLeft.setFillAfter(false);
        animationToLeft.setDuration(duration);

        TranslateAnimation animationLeftToRight = new TranslateAnimation(-100, 200, 0, 0);
        animationLeftToRight.setFillEnabled(false);
        animationLeftToRight.setFillBefore(false);
        animationLeftToRight.setFillAfter(false);
        animationLeftToRight.setDuration(duration * 2);
        animationLeftToRight.setStartOffset(duration);

        TranslateAnimation animationRightToLeft = new TranslateAnimation(200, -100, 0, 0);
        animationRightToLeft.setFillEnabled(false);
        animationRightToLeft.setFillBefore(false);
        animationRightToLeft.setFillAfter(false);
        animationRightToLeft.setDuration(duration * 2);
        animationRightToLeft.setStartOffset(duration * 3);

        TranslateAnimation animationLeftToCenter = new TranslateAnimation(-100, 0, 0, 0);
        animationLeftToCenter.setFillEnabled(false);
        animationLeftToCenter.setFillBefore(false);
        animationLeftToCenter.setDuration(duration);
        animationLeftToCenter.setStartOffset(duration * 5);

        animationSet.addAnimation(animationToLeft);
        animationSet.addAnimation(animationLeftToRight);
        animationSet.addAnimation(animationRightToLeft);
        animationSet.addAnimation(animationLeftToCenter);

        if (view != null) {
            view.startAnimation(animationSet);
        }

    }
}
