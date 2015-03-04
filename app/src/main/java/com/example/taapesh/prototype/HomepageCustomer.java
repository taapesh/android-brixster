package com.example.taapesh.prototype;

/*
 * While page is active, location info is tracked
 * and updated periodically so that the Google Places
 * search will return the most accurate information.
 * Efficiently manage Google Places searches and GPS tracking to
 * optimize API quota and battery usage.
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.content.Intent;
import android.view.MenuItem;

// Imports for retrieving Google Places data
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

// Imports for Google Play Services location tracking
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.content.IntentSender;
import android.location.Location;
import android.widget.Toast;


public class HomepageCustomer extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = HomepageCustomer.class.getSimpleName();

    // Define a request code to send to Google Play services
    // This code is returned in Activity.onActivityResult
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // String of stores to search
    private String[] stores =  { "H-E-B", "Kroger", "Randalls", "SuperTarget",
            "Target", "Trader Joe's", "Walmart", "Whole Foods" };

    // DetectConnection object
    private DetectConnection dc;

    // Google Api Client
    private GoogleApiClient mGoogleApiClient;

    // Location request object
    private LocationRequest mLocationRequest;

    // Tracking parameters
    private static final int UPDATE_INTERVAL = 10;
    private static final int FAST_UPDATE_INTERVAL = 1;

    // Latitude and Longitude
    private double currentLatitude;
    private double currentLongitude;

    // Google Places
    private GooglePlaces googlePlaces;

    // UI elements
    private AutoCompleteTextView storeSearchField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage_customer);

        // Initialize longitude and latitude
        currentLatitude = 0.0;
        currentLongitude = 0.0;

        // Create connection detector and check for available connection
        dc = new DetectConnection(getApplicationContext());
        boolean canConnect = dc.canConnectToInternet();

        if (!canConnect) {
            // Show alert and prompt user to enable internet connection
        }

        // Create GoogleApi client object
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL * 1000)
                .setFastestInterval(FAST_UPDATE_INTERVAL * 1000);

        // Get store search field and attach it to autocomplete
        storeSearchField = (AutoCompleteTextView) findViewById(R.id.storeSearchField);
        storeSearchField.setAdapter(new StoreSearchAdapter(this, R.layout.list_item));

        /*
        * ListItem click event
        * On selecting an item, Storefront activity is launched for that store
        */
        storeSearchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Intent to go to storefront
                Intent goToStorefront = new Intent(getApplicationContext(),
                        Storefront.class);

                // Pass store name to new activity
                goToStorefront.putExtra("store", (String) parent.getItemAtPosition(position));
                goToStorefront.putExtra("latitude", currentLatitude);
                goToStorefront.putExtra("longitude", currentLongitude);
                startActivity(goToStorefront);
            }
        });
    }

    /*
     * Custom adapter for store search
     * Searches registered stores for approximate matches
     */
    private class StoreSearchAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;
        private ArrayList<String> approxResults;

        public StoreSearchAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    resultList = new ArrayList<>();
                    approxResults = new ArrayList<>();
                    FilterResults filterResults = new FilterResults();

                    if (constraint != null) {
                        // Get search text as lower case String
                        String searchText = constraint.toString().toLowerCase();

                        // Determine which stores to display in the result
                        for(String _store : stores) {
                            // Process store String
                            String store = _store.replace("-", "").replace("'","").toLowerCase();

                            // Words that start with the search text have higher priority
                            if (store.startsWith(searchText)) {
                                resultList.add(_store);
                            } else if (store.contains(searchText)) {
                                approxResults.add(_store);
                            }
                        }

                        for(String result : approxResults) {
                            resultList.add(result);
                        }

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    }
                    else {
                        notifyDataSetInvalidated();
                    }
                }};
            return filter;
        }
    }

    /*
    * Connect API client on resuming activity
    */
    @Override
    protected void onResume() {
        super.onResume();
        storeSearchField.setText("");
        mGoogleApiClient.connect();
    }

    /*
     * Disconnect API client and stop updates on pausing activity
     */
    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /*
     * Process new location
     */
    private void handleNewLocation(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    /*
     * Manually stop checking for location updates
     */
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /*
     * Manually start checking for location updates
     */
    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /*
     * Called automatically when location changes
     */
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    /*
     * On connected, get the latest location information
     * Or start receiving location updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /*
     * Easy toast maker
     */
    private void MakeToast(String message) {
        Toast.makeText(HomepageCustomer.this, message, Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_employee_homepage, menu);
        return true;
    }

    private static String CapFirst(String str) {
        String[] words = str.split(" ");
        StringBuilder capdString = new StringBuilder();
        for(int i = 0; i < words.length; i++) {
            capdString.append(Character.toUpperCase(words[i].charAt(0)));
            capdString.append(words[i].substring(1));
            if(i < words.length - 1) {
                capdString.append(' ');
            }
        }
        return capdString.toString();
    }

    /*
     * Update current latitude and longitude in the background on separate thread
     * Not currently implemented
     */
    class UpdateLocation extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            return null;
        }

        @Override
        protected void onPreExecute() {

        }

        protected void onPostExecute(String file_url) {

        }
    }
}
