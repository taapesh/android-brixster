package com.example.taapesh.prototype;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

// Imports for Google Places


public class HomepageCustomer extends ActionBarActivity {

    protected Button showGPSButton;

    // GPS tracker class
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_customer);

        showGPSButton = (Button) findViewById(R.id.showGPSButton);

        showGPSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create gps class object
                gps = new GPSTracker(HomepageCustomer.this);

                // Check if GPS enabled
                if(gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    // Show location using toast
                    Toast.makeText(getApplicationContext(),
                            "Your Location \nLat: " + latitude +
                            "\nLong: " + longitude,
                            Toast.LENGTH_LONG).show();
                } else {
                    // Can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
