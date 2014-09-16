package com.madwin.carhud.maps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.madwin.carhud.MainActivity;
import com.madwin.carhud.R;

public class CarHUDMap {

    private static final String TAG = "CarHUDMap";
    public Activity activity;
    double height;
    double width;

    public LatLng getAdjustedCoordinates(GoogleMap gMap, Location location, double CURRENT_BEARING, Activity _activity) {
        //Log.d(TAG, "Zoom level = " + gMap.getCameraPosition().zoom);
        //Log.e(TAG, "location lat/long = " + location.getLatitude() + "/" + location.getLongitude());

        this.activity = _activity;
        LatLngBounds llb = gMap.getProjection().getVisibleRegion().latLngBounds;
       // Log.e(TAG, "Southwest lat/long = " + llb.southwest.latitude + "/" + llb.southwest.longitude);
        FrameLayout frameLayout = (FrameLayout)this.activity.findViewById(R.id.map_fragment_frame);
        height = frameLayout.getMeasuredHeight();
        width = frameLayout.getMeasuredWidth();
        //Log.d(TAG, "carhudmap height / width = " + height + " / " + width);

        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

        //Log.e(TAG, "CURRENT_BEARING = " + CURRENT_BEARING);
        //Log.e(TAG, "interior angle = " + mGetInteriorAngle(CURRENT_BEARING));
        //Log.e(TAG, "center = " + llb.getCenter().latitude + "/" + llb.getCenter().longitude);

        double angle2 = Math.abs(Math.atan(height / width));
        //Log.e(TAG, "angle 2 = " + angle2);
        //Log.e(TAG, "sin int angle" + Math.abs(Math.sin(mGetInteriorAngle(CURRENT_BEARING))));
        //Log.e(TAG, "cos int angle" + Math.abs(Math.cos(mGetInteriorAngle(CURRENT_BEARING))));

        double dist_center_to_corner = Math.sqrt(Math.pow(llb.southwest.latitude - llb.getCenter().latitude, 2) +
                Math.pow(llb.southwest.longitude - llb.getCenter().longitude, 2));
        //Log.e(TAG, "dist center to corner = " + dist_center_to_corner);
        double adjusted_distance_center_to_bottom = getAdjustmentValue(
                gMap.getCameraPosition().zoom) * dist_center_to_corner * Math.sin(angle2);
        //Log.e(TAG, "adjusted distance center to bottom = " + adjusted_distance_center_to_bottom);

        if (CURRENT_BEARING == 0 || CURRENT_BEARING == 360) {
            //Log.e(TAG, "adjusted coord = " + (center.latitude +
            //        adjusted_distance_center_to_bottom + ", " + center.longitude));

            return new LatLng(location.getLatitude() +
                    adjusted_distance_center_to_bottom, location.getLongitude());
        }
        if (CURRENT_BEARING == 90) {
            return new LatLng(location.getLatitude(), location.getLongitude() +
                    adjusted_distance_center_to_bottom);
        }
        if (CURRENT_BEARING == 180) {
            return new LatLng((location.getLatitude() -
                    adjusted_distance_center_to_bottom), location.getLongitude());
        }
        if (CURRENT_BEARING == 270) {
            return new LatLng(location.getLatitude(), location.getLongitude() -
                    adjusted_distance_center_to_bottom);
        }

        if (0 < CURRENT_BEARING && CURRENT_BEARING < 90) {
            return new LatLng(
                    location.getLatitude() + (adjusted_distance_center_to_bottom *
                            Math.abs(Math.cos(mGetInteriorAngle(CURRENT_BEARING)))),
                    location.getLongitude() + (adjusted_distance_center_to_bottom *
                            Math.abs(Math.sin(mGetInteriorAngle(CURRENT_BEARING))))
            );
        }
        if (90 < CURRENT_BEARING && CURRENT_BEARING < 180) {
            return new LatLng(
                    location.getLatitude() - (adjusted_distance_center_to_bottom *
                            Math.abs(Math.sin(mGetInteriorAngle(CURRENT_BEARING)))),
                    location.getLongitude() + (adjusted_distance_center_to_bottom *
                            Math.abs(Math.cos(mGetInteriorAngle(CURRENT_BEARING))))
            );
        }
        if (180 < CURRENT_BEARING && CURRENT_BEARING < 270) {
            return new LatLng(
                    location.getLatitude() - (adjusted_distance_center_to_bottom *
                            Math.abs(Math.cos(mGetInteriorAngle(CURRENT_BEARING)))),
                    location.getLongitude() - (adjusted_distance_center_to_bottom *
                            Math.abs(Math.sin(mGetInteriorAngle(CURRENT_BEARING))))
            );
        }
        if (270 < CURRENT_BEARING && CURRENT_BEARING < 360) {
            return new LatLng(
                    location.getLatitude() + (adjusted_distance_center_to_bottom *
                            Math.abs(Math.sin(mGetInteriorAngle(CURRENT_BEARING)))),
                    location.getLongitude() - (adjusted_distance_center_to_bottom *
                            Math.abs(Math.cos(mGetInteriorAngle(CURRENT_BEARING))))
            );
        }
        return center;
    }

    private double mGetInteriorAngle(double bearing) {

        if (270 < bearing && bearing < 360) {
            bearing = bearing - 270;
        }
        if (180 < bearing && bearing < 270) {
            bearing = bearing - 180;
        }
        if (90 < bearing && bearing < 180) {
            bearing = bearing - 90;
        }
        return Math.toRadians(bearing);
    }

    public static float getMaximumTilt(float zoom) {
        float tilt = 30.0f;

        if (zoom > 15.5f) {
            tilt = 67.5f;
        } else if (zoom >= 14.0f) {
            tilt = (((zoom - 14.0f) / 1.5f) * (67.5f - 45.0f)) + 45.0f;
        } else if (zoom >= 10.0f) {
            tilt = (((zoom - 10.0f) / 4.0f) * (45.0f - 30.0f)) + 30.0f;
        }

        return tilt;
    }
    public static double getAdjustmentValue(double zoom) {
        double adjustment = 1.0;
        if (zoom > 18.5) {
            adjustment = 0.25;
        } else if (zoom > 17) {
            adjustment = 0.28;
        }else if (zoom > 15.5) {
            adjustment = 0.3;
        }else if (zoom>= 15.0) {
            adjustment = 0.38;
        }else if (zoom >= 14.5) {
            adjustment = 0.42;
        }else if (zoom >= 14.0) {
            adjustment = 0.43;
        }else if (zoom >= 13.5) {
            adjustment = 0.45;
        }else if (zoom >= 13.0) {
            adjustment = 0.47;
        }else if (zoom >= 12.5) {
            adjustment = 0.47;
        }else if (zoom >= 12.0) {
            adjustment = 0.5;
        }else if (zoom >= 10.0) {
            adjustment = 0.5;
        }else if (zoom >= 5.0) {
            adjustment = 0.5;
        }
        return adjustment;
    }

    public static float speedBasedZoom(double speed, float zoom_level) {
        SharedPreferences sp = MainActivity.getAppContext().getSharedPreferences("com.madwin.carhud", Context.MODE_PRIVATE);
        if (sp.getBoolean("speed_zoom_preference", true)) {
            if (speed >= 70) {
                return 13;
            }
            if (speed <= 20) {
                return 16;
            } else {
                return (float) (16.0 - ((3.0 / 50.0) * (speed - 20.0)));
            }
        }
        return zoom_level;
    }
}
