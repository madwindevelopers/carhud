package com.madwin.carhud;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;


public class MapFragment extends Fragment{

	GoogleMap map;
	String Test = "test";
    String TAG = "MapFragment";

    LocationManager locationManager;
    LocationListener locationListener;
    LatLng CURRENT_LOCATION = new LatLng(43.035, -87.907);
    float CURRENT_BEARING = 0;
    float CURRENT_SPEED = 0;
    private LocationClient mLocationClient;
    Boolean MyLocationClicked = true;

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.mapfragment, container, false);

    	
    	Log.d("carhud", "debug test = " + Test);
    	map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mv)).getMap(); // Obtain the map from a MapFragment or MapView.
    	map.setMyLocationEnabled(true);

        map.setOnMyLocationButtonClickListener(myLocationListener);
        map.setOnMapClickListener(mapClickListener);

    	//Move the camera instantly to hamburg with a zoom of 15.
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 15));

    	// Zoom in, animating the camera.
    	map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null); 
    	

    	/********Trial Code Location**************************/
        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "on location changed Tilt = " + map.getCameraPosition().tilt);

                CURRENT_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());

                if (location.getSpeed() > 0.5) {
                    CURRENT_BEARING = location.getBearing();
                }
                Log.d(TAG, "BEARING : " + location.getBearing());

                    Log.d(TAG, "MyLocationClick onLocationChanged : " + MyLocationClicked);
                    if (MyLocationClicked) {
                        //Log.d("carhud", "update_bearing = " + UPDATE_BEARING);
                        CameraPosition cameraPosition = new CameraPosition(CURRENT_LOCATION,
                                                                map.getCameraPosition().zoom,
                                                                map.getCameraPosition().tilt,
                                                                CURRENT_BEARING);

                        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 200, null);
                    }

                CURRENT_SPEED = location.getSpeed();
                mSendSpeed();
                mSendLocation();


            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            	Log.d("carhud", "status changed");
            }

            @Override
            public void onProviderEnabled(String provider) {
            	Log.d("carhud", "provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
            	Log.d("carhud", "provider disabled");
				locationManager.removeUpdates(locationListener);
            }
        };

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
    	
    	/**********Trial Code Location************************/
    	
        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MapFragment Destroyed");

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
            // implement code here
        }
    
    }

    public void mSendSpeed() {
        Intent i = new Intent("com.madwin.carhud.SPEED_LISTENER");
        i.putExtra("CURRENT_SPEED", CURRENT_SPEED);


        getActivity().sendBroadcast(i);
    }

    public void mSendLocation() {
        Intent i = new Intent("com.madwin.carhud.LOCATION_LISTENER");
        i.putExtra("LATITUDE", CURRENT_LOCATION.latitude);
        i.putExtra("LONGITUDE", CURRENT_LOCATION.longitude);
        getActivity().sendBroadcast(i);

    }


}
