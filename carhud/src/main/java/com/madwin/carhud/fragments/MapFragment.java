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

    private GoogleMap map;
    private String TAG = "MapFragment";

    private static final String tiltPreferenceKey = "tilt_preference";

    private FragmentManager fm;
    private FragmentTransaction ft;
    private SpeedFragment speedFragment = new SpeedFragment();
    private RefreshRouteFragment refreshRouteFragment = new RefreshRouteFragment();
    private CurrentAddressFragment currentAddressFragment = new CurrentAddressFragment();
    private MapMenuFragment mapMenuFragment = new MapMenuFragment();

    private int animationSpeed;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng currentLocation;
    private LatLng adjustedLocation;
    private float CURRENT_BEARING = 0;
    private float ZOOM_LEVEL = 6;
    private float speed = 0;

    private boolean routeIsVisible = false;
    private boolean satelliteEnabled = false;
    private boolean hybridEnabled = false;

    private Boolean MyLocationClicked = true;
    private SharedPreferences sp;
    private SupportMapFragment thisMap;
    private GMapV2Direction md;

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

                boolean tilt = sp.getBoolean(tiltPreferenceKey, true);
                speed = location.getSpeed();

                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (speed > 2.2) // doesn't update bearing when speed is under # m/s
                    CURRENT_BEARING = location.getBearing();

                ZOOM_LEVEL = CarHUDMap.speedBasedZoom(speed,
                        map.getCameraPosition().zoom);

                setAdjustedLocation(new CarHUDMap().getAdjustedCoordinates(map,
                        location, CURRENT_BEARING, getActivity(), tilt));

                setAnimationSpeed(Integer.parseInt(sp.getString("map_animation_speed", "900")));
                float tiltValue = 0.0f;
                if (tilt) {
                    tiltValue = CarHUDMap.getMaximumTilt(ZOOM_LEVEL);
                }

                if (MyLocationClicked) {
                    final CameraPosition cameraPosition = new CameraPosition(
                            getAdjustedLocation(),
                            ZOOM_LEVEL,
                            tiltValue,
                            CURRENT_BEARING);
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition),
                    getAnimationSpeed(), null);
                }

                currentAddressFragment.setCurrentLocation(getLocationLatLng());
                speedFragment.setSpeed(speed);
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

        ft.add(R.id.mv, refreshRouteFragment);
        ft.add(R.id.mv, speedFragment);
        ft.add(R.id.mv, currentAddressFragment);
        ft.add(R.id.mv, mapMenuFragment);
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

    public void setRouteIsVisible(boolean routeIsVisible) {
        this.routeIsVisible = routeIsVisible;
    }

    public void showRoute(LatLng fromPosition, LatLng toPosition) {
        setFromPosition(fromPosition);
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

    public int getAnimationSpeed() { return this.animationSpeed; }

    public void toggleTrafficEnabled() { map.setTrafficEnabled(!map.isTrafficEnabled()); }

    public boolean isTrafficEnabled() { return map.isTrafficEnabled(); }

    public boolean isSatelliteEnabled() { return satelliteEnabled; }

    public boolean isHybridEnabled() { return hybridEnabled; }

    public void toggleHybridEnabled() {
        if (isHybridEnabled()) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            hybridEnabled = false;
        } else {
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            satelliteEnabled = false;
            hybridEnabled = true;
        }
    }

    public void toggleSatelliteEnabled() {
        if (isSatelliteEnabled()) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            satelliteEnabled = false;
        } else {
            map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            satelliteEnabled = true;
            hybridEnabled = false;
        }
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
