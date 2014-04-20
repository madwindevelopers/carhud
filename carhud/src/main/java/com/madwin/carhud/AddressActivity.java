package com.madwin.carhud;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import java.io.IOException;
import java.util.List;


public class AddressActivity extends Activity implements View.OnClickListener {

    String TAG = "com.madwin.carhud.AddressActivity";
    String address;
    double latitude;
    double longitude;
    double from_latitude;
    double from_longitude;
    double to_latitude;
    double to_longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        from_latitude = 0;
        from_longitude = 0;
        to_latitude = 0;
        to_longitude = 0;

    }

    @Override
    public void onBackPressed() {
        mUpdateAndBack();
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.address, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                        EditText fromET = (EditText)findViewById(R.id.from_address_edit_text);
                        fromET.setText(location.getLatitude() + ", " + location.getLongitude());
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
        EditText toET = (EditText)findViewById(R.id.to_address_edit_text);
        EditText fromET = (EditText)findViewById(R.id.from_address_edit_text);


        if (toET.getText() != null && !toET.getText().toString().equals("")) {
            address = toET.getText().toString();

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

        if (fromET.getText() != null && !fromET.getText().toString().equals("")) {
            address = fromET.getText().toString();

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
        Intent intent = new Intent(this, MainActivity.class);

        if (from_latitude != 0 && from_longitude != 0 && to_latitude != 0 && to_longitude != 0) {

            intent.putExtra("from_latitude", from_latitude);
            intent.putExtra("from_longitude", from_longitude);
            intent.putExtra("to_latitude", to_latitude);
            intent.putExtra("to_longitude", to_longitude);
        }
            startActivity(intent);
    }



}
