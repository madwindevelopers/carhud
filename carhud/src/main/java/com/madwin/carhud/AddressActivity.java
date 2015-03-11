package com.madwin.carhud;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.madwin.carhud.fragments.MapFragment;
import com.madwin.carhud.maps.MyLocation;
import com.madwin.carhud.utils.DisplayUtils;

import java.io.IOException;
import java.util.List;


public class AddressActivity extends ActionBarActivity implements View.OnClickListener {

    private String TAG = "com.madwin.carhud.AddressActivity";
    private String address;
    private double latitude;
    private double longitude;
    private double from_latitude;
    private double from_longitude;
    private double to_latitude;
    private double to_longitude;
    private Button done;
    private Button doneAndNavigate;

    private EditText fromAddressEditText;
    private EditText toAddressEditText;

    static private MapFragment mapFragment = MainActivity.getMapFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        setupToolbar();

        from_latitude = 0;
        from_longitude = 0;
        to_latitude = 0;
        to_longitude = 0;

        fromAddressEditText = (EditText) findViewById(R.id.from_address_edit_text);
        toAddressEditText = (EditText) findViewById(R.id.to_address_edit_text);

        done = (Button) findViewById(R.id.done);
        doneAndNavigate = (Button) findViewById(R.id.done_and_navigate);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT)
            setupButtons();

    }

    @Override
    public void onBackPressed() {
        mUpdateAndBack();
        finish();
        super.onBackPressed();
    }

    private void getCoordinates() throws IOException {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        addresses = geocoder.getFromLocationName(address, 1);
        if(addresses.size() > 0) {
            latitude = addresses.get(0).getLatitude();
            longitude = addresses.get(0).getLongitude();
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.get_location_button:
                Log.e(TAG, "Location Button Pushed");
                MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
                    @Override
                    public void gotLocation(Location location){
                        //Got the location!
                        fromAddressEditText.setText(location.getLatitude() + ", " + location.getLongitude());
                    }
                };
                MyLocation myLocation = new MyLocation();
                myLocation.getLocation(this, locationResult);

            break;

            case R.id.done:
                mUpdateAndBack();
                finish();
            break;

            case R.id.done_and_navigate:
                mUpdateAndBack();
                String navURL = "http://maps.google.com/maps?daddr="
                        + to_latitude + "," + to_longitude;
                Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(navURL));
                this.startActivity(navIntent);
                finish();

        }
    }

    private void mUpdateAndBack() {

        if (toAddressEditText.getText() != null &&
                !fromAddressEditText.getText().toString().equals("")) {
            address = toAddressEditText.getText().toString();

            try {
                getCoordinates();
                if (latitude != 0 || longitude != 0) {
                    to_latitude = latitude;
                    to_longitude = longitude;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (fromAddressEditText.getText() != null &&
                !fromAddressEditText.getText().toString().equals("")) {
            address = fromAddressEditText.getText().toString();

            try {
                getCoordinates();
                if (latitude != 0 || longitude != 0) {
                    from_latitude = latitude;
                    from_longitude = longitude;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (from_latitude != 0 && from_longitude != 0 && to_latitude != 0 && to_longitude != 0) {
            LatLng toPosition = new LatLng(to_latitude, to_longitude);
            LatLng fromPosition = new LatLng(from_latitude, from_longitude);
            mapFragment.clearMap();
            mapFragment.showRoute(fromPosition, toPosition);
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (null != toolbar) {
            Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
            toolbar.setNavigationIcon(upArrow);

            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            toolbar.setTitle(R.string.about);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
//        setSupportActionBar(toolbar);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setupButtons() {
        done.setBackground(getResources().getDrawable(R.drawable.rounded_button_selector));
        doneAndNavigate.setBackground(getResources().getDrawable(R.drawable.rounded_button_selector));

        int height = (int) DisplayUtils.convertDpToPixel(36f);
        done.getLayoutParams().height = height;
        doneAndNavigate.getLayoutParams().height = height;

        done.setTextSize(14);
        doneAndNavigate.setTextSize(14);
    }

}
