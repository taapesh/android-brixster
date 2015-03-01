package com.example.taapesh.prototype;

/* While page is active, location info is tracked
 * and updated periodically so that the Google Places
 * search will return the most accurate information.
 * Efficiently manage Google Places searches and GPS tracking to
 * optimize API quota and battery usage.
 */

import android.support.v7.app.ActionBarActivity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

// Imports for Google Play Services API
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


public class HomepageCustomer extends ActionBarActivity implements ConnectionCallbacks,
        OnConnectionFailedListener, LocationListener {

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // LogCat tag
    private static final String TAG = HomepageCustomer.class.getSimpleName();

    // What is this
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private boolean mRequestingLocationUpdates = true;  // Flag for update requests, used to toggle
    private LocationRequest mLocationRequest;   // Location request object with parameters

    private Location mLastLocation; // Stores the latest location info
    private double mLatitude;       // Latest latitude info
    private double mLongitude;      // Latest longitude info

    // Location update parameters
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000;  // 5 sec
    private static int DISPLACEMENT = 10;       // 10 meters

    // UI elements
    private TextView locationInfoText;
    private Button getLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_customer);

        getLocationButton = (Button) findViewById(R.id.getLocation);
        locationInfoText = (TextView) findViewById(R.id.locationInfo);

        // Check availability of Google Play Services
        if (checkPlayServices()) {
            // If Play Services is available,
            // Build the GoogleApi client
            // and create location request object
            buildGoogleApiClient();
            createLocationRequest();
        }

        // Show latest location on click
        getLocationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getLatestLocation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    // On resume, check if Google Play Services is available
    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    // On stop, disconnect the GoogleApi client
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    // Stop requesting updates on pause
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    // Get latest location
    private void getLatestLocation() {
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();

            // Test: show latest coordinates
            String display = "Lat: " + mLatitude + "\nLon: " + mLongitude;
            locationInfoText.setText(display);
            Toast.makeText(getApplicationContext(), "Showing latest location info",
                    Toast.LENGTH_SHORT).show();
        }

        else {
            // Handle case of no location obtained
        }
    }

    // Toggle periodic location updates;
    // Location updates are on by default,
    // This method just gives us more control
    private void toggleLocationUpdates() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        } else {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    // Start location updates
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    // Stop location updates
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    // Create location request objects
    // Set parameters such as update interval
    // and desired accuracy
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    // Create Google API client object
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    // Check if Google Play Services is available on this device
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    //Google API callback methods
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with Google API, get user location
        // and start location updates
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        mLatitude = mLastLocation.getLatitude();
        mLongitude = mLastLocation.getLongitude();

        String display = "Lat: " + mLatitude + "\nLon: " + mLongitude;
        locationInfoText.setText(display);

        Toast.makeText(getApplicationContext(), "Location changed!",
                Toast.LENGTH_SHORT).show();
    }
}