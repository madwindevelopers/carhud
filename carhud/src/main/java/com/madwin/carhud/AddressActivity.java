package com.madwin.carhud;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import java.io.IOException;
import java.util.List;


public class AddressActivity extends Activity {

    String address;
    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
    }

    @Override
    public void onBackPressed() {
        try {
            getCoordinates();
            SharedPreferences preferences = this.getSharedPreferences("com.madwin.carhud", MODE_PRIVATE);
            preferences.edit().putFloat("address_latitude", (float) latitude).commit();
            preferences.edit().putFloat("address_longitude", (float) longitude).commit();


            startActivity(new Intent(this, MainActivity.class));

        } catch (IOException e) {
            e.printStackTrace();
        }




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
            latitude= addresses.get(0).getLatitude();
            longitude= addresses.get(0).getLongitude();
        }
    }

}
