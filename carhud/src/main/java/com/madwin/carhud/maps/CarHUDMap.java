package com.madwin.carhud.maps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;
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
    final private static String SPEED_ZOOM_PREFERENCE = "speed_zoom_preference";
    final private static String ZOOM_LEVEL = "zoom_level";
    final private static String DEBUG_ZOOM_LEVEL = "debug_zoom_level";

    public LatLng getAdjustedCoordinates(GoogleMap gMap, Location location, double CURRENT_BEARING, Activity _activity) {

        this.activity = _activity;
        LatLngBounds llb = gMap.getProjection().getVisibleRegion().latLngBounds;

        FrameLayout frameLayout = (FrameLayout)this.activity.findViewById(R.id.map_fragment_frame);

        height = frameLayout.getMeasuredHeight();
        width = frameLayout.getMeasuredWidth();

        double sWLatitude = llb.southwest.latitude;
        double sWLongitude = llb.southwest.longitude;

        double centerLatitude = llb.getCenter().latitude;
        double centerLongitude = llb.getCenter().longitude;

        LatLng center = new LatLng(location.getLatitude(), location.getLongitude());

        double angle2 = Math.abs(Math.atan((width / 2) / (height / 2)));



        double longitudeDiff = Math.pow(centerLongitude-sWLongitude, 2);
        double latitudeDiff = Math.pow(centerLatitude - sWLatitude, 2);

        double sumDiff = longitudeDiff + latitudeDiff;
        double sqrtDiff = Math.sqrt(sumDiff);

        double dist_center_to_corner = sqrtDiff;

        double adjusted_distance_center_to_bottom = getAdjustmentValue(
                gMap.getCameraPosition().zoom) * dist_center_to_corner * Math.sin(angle2);

        if (CURRENT_BEARING == 0 || CURRENT_BEARING == 360) {
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
        if (zoom >= 19) {
            adjustment = 0.248;
        }else if (zoom > 18.5) {
            adjustment = 0.25;
        }else if (zoom > 18) {
            adjustment = 0.26;
        }else if (zoom > 17) {
            adjustment = 0.28;
        }else if (zoom > 16) {
            adjustment = 0.29;
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
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(MainActivity.getAppContext());
        double minimum_zoom_level = Float.parseFloat(sp.getString("minimum_zoom_level", "13"));
        double maximum_zoom_level = Float.parseFloat(sp.getString("maximum_zoom_level", "19"));
        double minimum_speed = 20;
        double maximum_speed = 70;
        double speed_mph = (speed * 2.23694);

        if (sp.getBoolean(SPEED_ZOOM_PREFERENCE, true)) {
            if (speed_mph >= maximum_speed) {
                return (float) minimum_zoom_level;
            }
            if (speed_mph <= minimum_speed) {
                return (float) maximum_zoom_level;
            } else {
                float rate_change = (float) ((maximum_zoom_level - minimum_zoom_level) / (maximum_speed - minimum_speed));
                return (float) ((speed_mph - minimum_speed) * -rate_change + maximum_zoom_level);
            }
        }
        return zoom_level;
    }

}
