package com.madwin.carhud.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.madwin.carhud.MainActivity;
import com.madwin.carhud.Manifest;
import com.madwin.carhud.R;
import com.madwin.carhud.maps.CarHUDMap;
//import com.madwin.carhud.maps.GMapV2Direction;

import org.w3c.dom.Document;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap map;
    private String TAG = "MapFragment";

    private static final String tiltPreferenceKey = "tilt_preference";
    private static final String offsetLocationPreferenceKey = "offset_location_preference";

    private FragmentManager fm;
    private FragmentTransaction ft;
    private SpeedFragment speedFragment = new SpeedFragment();
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
    private double minimumUpdateSpeed = 2.2352;  // 5 mph

    private boolean satelliteEnabled = false;
    private boolean hybridEnabled = false;

    private Boolean MyLocationClicked = true;
    private SharedPreferences sp;
    private SupportMapFragment thisMap;

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

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                boolean tilt = sp.getBoolean(tiltPreferenceKey, true);
                speed = location.getSpeed();

                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if (speed > minimumUpdateSpeed) // doesn't update bearing when speed is under # m/s
                    CURRENT_BEARING = location.getBearing();

                ZOOM_LEVEL = CarHUDMap.speedBasedZoom(speed,
                        map.getCameraPosition().zoom);

                adjustedLocation = new CarHUDMap().getAdjustedCoordinates(map,
                        location, CURRENT_BEARING, getActivity(), tilt);

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

        mSetupMap();


        return v;
    }

    private boolean locationPermissionAllowed() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.getAppContext(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED)
            return true;
        else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION
                    }, 1);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (thisMap != null ) {
            mSetupMap();
        }
    }

    private void mSetupMap() {

        thisMap.getMapAsync(this);

        fm = getActivity().getSupportFragmentManager();
        ft = fm.beginTransaction();

        ft.add(R.id.mv, speedFragment);
        ft.add(R.id.mv, currentAddressFragment);
        ft.add(R.id.mv, mapMenuFragment);
        ft.commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        startLocationListener();
        map = googleMap;
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (locationPermissionAllowed()) {
            googleMap.setMyLocationEnabled(true);
        }
        googleMap.setOnMyLocationButtonClickListener(myLocationListener);
        googleMap.setOnMapClickListener(mapClickListener);
    }

    public void enableMyLocation() {
        if (locationPermissionAllowed())
            thisMap.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    googleMap.setMyLocationEnabled(true);
                }
            });
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

    public LatLng getLocationLatLng() {
        return currentLocation;
    }

    public void stopLocationListener() {
        locationManager.removeUpdates(locationListener);
    }

    public void startLocationListener() {
        if (!locationPermissionAllowed()) return;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    }

    public void setAdjustedLocation(LatLng adjustedLocation) {
        this.adjustedLocation = adjustedLocation;
    }

    public LatLng getAdjustedLocation() {
        if (sp.getBoolean(offsetLocationPreferenceKey, false))
            return adjustedLocation;
        else
            return currentLocation;
    }

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

}
