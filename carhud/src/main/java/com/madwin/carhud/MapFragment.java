package com.madwin.carhud;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapFragment extends Fragment{

	GoogleMap map;
	Boolean UPDATE_BEARING = false;
	String Test = "test";

    LocationManager locationManager;
    LocationListener locationListener;
    LatLng CURRENT_LOCATION = new LatLng(43.035, -87.907);
    private LocationClient mLocationClient;

	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
    	View v = inflater.inflate(R.layout.mapfragment, container, false);
    	
    	Log.d("carhud", "debug test = " + Test);
    	map = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.mv)).getMap(); // Obtain the map from a MapFragment or MapView.
    	map.setMyLocationEnabled(true);

        map.setOnMyLocationButtonClickListener(myLocationListener);

    	//Move the camera instantly to hamburg with a zoom of 15.
    	map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 15));

    	// Zoom in, animating the camera.
    	map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null); 
    	
    	     
        
     /*   nav_icon = BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(
        		BitmapFactory.decodeResource(
        				getResources(), R.drawable.navigation), 40, 40, false));
    	marker = map.addMarker(new MarkerOptions()
		.position(CURRENT_LOCATION)
		.icon(nav_icon));*/
    	


    	
    	
    	
    	/********Trial Code Location**************************/
        locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        //locationManager.getLastKnownLocation(Context.LOCATION_SERVICE);

        
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            /*
            	//Move the camera instantly to CURRENT_LOCATION with a zoom of 15.
            	marker = map.addMarker(new MarkerOptions()
        		.position(CURRENT_LOCATION)
        		.icon(nav_icon));
            	map.moveCamera(CameraUpdateFactory.newLatLngZoom(CURRENT_LOCATION, 
            											(int) map.getCameraPosition().zoom));*/

                //Log.d("MAP", "CURRENT_LOCATION = " + CURRENT_LOCATION);
                CURRENT_LOCATION = new LatLng(location.getLatitude(), location.getLongitude());

            	if (UPDATE_BEARING){
            		//Log.d("carhud", "update_bearing = " + UPDATE_BEARING);
	            	CameraPosition cameraPosition = new CameraPosition(CURRENT_LOCATION, 
	            											(int) map.getCameraPosition().zoom,
	               										    0,
	            											location.getBearing());
	            	
	            	map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
            	}

            	// Zoom in, animating the camera.
            	//map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null); */

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
    

            //OnMyLocationButtonClickListener mOnMyLocationButtonClickListener;
			//map.setOnMyLocationButtonClickListener(mOnMyLocationButtonClickListener);
            


            


    	
    	/**********Trial Code Location************************/
    	
        // Inflate the layout for this fragment
        return v;
    }


    
    private GoogleMap.OnMyLocationButtonClickListener myLocationListener = new OnMyLocationButtonClickListener() {
        @Override
        public boolean onMyLocationButtonClick() {
            Log.d("carhud", "MyLocation Pressed");

                UPDATE_BEARING = true;

            return false;

        }
    };




    
}
