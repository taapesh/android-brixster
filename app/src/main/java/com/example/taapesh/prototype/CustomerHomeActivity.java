package com.example.taapesh.prototype;

/**
 * User location is tracked and updated periodically
 * User can search for a store and all locations of
 * that store near them are displayed.
 * Before user searches for a store, the page shows
 * favorite stores, suggested stores, and special
 * deals
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.content.Intent;
import android.view.MenuInflater;
import android.view.MenuItem;

// Imports for retrieving Google Places data
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;


public class CustomerHomeActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static DrawerLayout mDrawerLayout;
    private static ImageView menuToggleIcon;
    private static View menuToggleArea;

    private static View storeSearchBox;
    private static final int searchBarHeight = 75;
    private static final int searchBarMargin = 10;

    private static final String TAG = CustomerHomeActivity.class.getSimpleName();

    private static float screenDensity;

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
    private static final int UPDATE_INTERVAL = 7;
    private static final int FAST_UPDATE_INTERVAL = 1;

    // Latitude and Longitude
    private double currentLatitude;
    private double currentLongitude;

    // ArrayList to hold store info
    private static ArrayList<Store> storeResults;

    // ListView to hold store cards
    private static ListView storeResultsView;

    // ListViews for store favorites, suggestions, deals
    private static ListView storeFavorites;
    private static ListView storeSuggestions;
    private static ListView storeDeals;

    // Google Places
    private static GooglePlaces googlePlaces;

    // Input manager
    private static InputMethodManager imm;

    // UI elements
    private AutoCompleteTextView storeSearchField;

    // Reference to activity
    private static Activity actRef;

    // Progress dialog
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_home_activity);

        screenDensity = getResources().getDisplayMetrics().density;

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.custom_action_bar);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        // Setup menu toggle stuff
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuToggleIcon = (ImageView) findViewById(R.id.menuIcon);
        menuToggleArea = findViewById(R.id.menuToggleArea);

        menuToggleArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        // Set activity reference
        actRef = CustomerHomeActivity.this;

        // Create Google Places Object
        googlePlaces = new GooglePlaces();

        // Initialize longitude and latitude
        currentLatitude = 0.0;
        currentLongitude = 0.0;

        // Initialize store info ArrayList
        storeResults = new ArrayList<>();

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

        // Initialize input method manager
        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        storeSearchBox = findViewById(R.id.storeSearchBox);

        // Get store search field and attach it to autocomplete
        storeSearchField = (AutoCompleteTextView) findViewById(R.id.storeSearchField);
        storeSearchField.setAdapter(new StoreSearchAdapter(this, R.layout.list_item));

        // Find store results ListView and hide it until customer picks a store
        storeResultsView = (ListView) findViewById(R.id.storeResultsView);
        storeResultsView.setVisibility(View.GONE);

        // Attach item clicked listener to ListView
        storeResultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get selected store
                Store selectedStore = (Store) parent.getItemAtPosition(position);

                // Intent to enter store
                Intent enterStore = new Intent(getApplicationContext(),
                        StoreBrowseActivity.class);

                // Pass store name and address to new activity
                enterStore.putExtra("store", selectedStore.getName());
                enterStore.putExtra("address", selectedStore.getAddress());
                startActivity(enterStore);
            }
        });

        /**
        * On selecting a store from autocomplete dropdown
        * load and show store locations
        */
        storeSearchField.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Hide the soft keyboard
                new HideKeyboard().execute();

                // Hide store favorites/deals/suggestions

                // Get selected store name and load store locations asynchronously
                String storeName = (String) parent.getItemAtPosition(position);
                new LoadStores().execute(storeName);
            }
        });

        mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(this,
                mDrawerLayout, null, 0, 0){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                menuToggleIcon.setImageResource(R.drawable.menu_24);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                menuToggleIcon.setImageResource(R.drawable.left_arrow_24);
            }});

        setupUI();
    }

    private void setupUI() {
        int barHeight = dpToPx(searchBarHeight);
        int margin = dpToPx(searchBarMargin);

        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, barHeight);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rParams.setMargins(margin, margin, margin, 0);
        storeSearchBox.setLayoutParams(rParams);

        rParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rParams.topMargin = barHeight + margin;
        rParams.leftMargin = margin;
        rParams.rightMargin = margin;
        storeResultsView.setLayoutParams(rParams);

    }

    /**
     * Convert dp to pixels
     */
    private int dpToPx(int dp) {
        return Math.round((float)dp * screenDensity);
    }

    /**
     * Asynchronously get store results using Google Places API
     * Create progress dialog and dismiss it when job is done
     */
    class LoadStores extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread show progress dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CustomerHomeActivity.this);
            pDialog.setMessage("Loading Storefront");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Get store information asynchronously
         * Using google places API
         */
        protected String doInBackground(String... args) {
            storeResults.clear();
            String storeName = args[0];

            ArrayList<String> storePredictions = googlePlaces.getStorePredictions(
                    storeName, currentLatitude, currentLongitude);

            Map placeDetails = new HashMap();
            for (String store : storePredictions) {
                try {
                    placeDetails = googlePlaces.getStoreDetails(store);
                } catch (Exception e) {
                    // pass
                }

                // Extract store details from details map
                if (placeDetails.size() > 0) {

                    // Extract store details
                    // Create a new store and set its details
                    String name = placeDetails.get("name").toString();
                    String address = placeDetails.get("address").toString();
                    String phone = placeDetails.get("phone_number").toString();

                    Store s = new Store(name, address, phone);
                    storeResults.add(s);
                }
            }

            return null;
        }

        /**
         * After completing background task, dismiss the progress dialog
         * And create the cards for each store. Finish by attaching the
         * CardArrayAdapter
         */
        protected void onPostExecute(String result) {
            hideProgress();

            // Create custom array adapter and attach it to ListView
            CardAdapter cardAdapter= new CardAdapter(
                    CustomerHomeActivity.this,
                    R.layout.store_card,
                    storeResults);

            // Set adapter and show the results
            storeResultsView.setAdapter(cardAdapter);
            storeResultsView.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress() {
        if(pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
        }
                /*
                //get the Context object that was used to great the dialog
                Context context = ((ContextWrapper)pDialog.getContext()).getBaseContext();

                //if the Context used here was an activity AND it hasn't been finished or destroyed
                //then dismiss it
                if(context instanceof Activity) {
                    if(!((Activity)context).isFinishing() && !((Activity)context).isDestroyed())
                        pDialog.dismiss();
                } else //if the Context used wasnt an Activity, then dismiss it too
                    pDialog.dismiss();
            }
            pDialog = null;
            */

    }

    public class CardAdapter extends ArrayAdapter<Store> {
        private final LayoutInflater inflater;
        private final int layoutId;

        /**
         * CardAdapter constructor
         */
        public CardAdapter(final Context context,
                           final int layoutId,
                           final ArrayList<Store> objects) {

            super(context, layoutId, objects);

            this.inflater = LayoutInflater.from(context);
            this.layoutId = layoutId;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View itemView = convertView;
            ViewHolder holder;
            final Store store = getItem(position);

            if(null == itemView) {
                itemView = this.inflater.inflate(layoutId, parent, false);

                // Create holder instance and find individual views
                holder = new ViewHolder();
                holder.storeName = (TextView) itemView.findViewById(R.id.storeName);
                holder.storeDetails = (TextView) itemView.findViewById(R.id.storeDetails);

                itemView.setTag(holder);
            } else {
                holder = (ViewHolder)itemView.getTag();
            }

            // Fill store card with details
            holder.storeName.setText(store.getName());
            holder.storeDetails.setText(store.getAddress());

            // Return this store card
            return itemView;
        }

        /**
         * Hold store details that are displayed on store card
         */
        protected class ViewHolder{
            protected TextView storeName;
            protected TextView storeDetails;
        }
    }

    /**
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

    /**
     * Connect API client on resuming activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        storeSearchField.setText("");
        mGoogleApiClient.connect();

        // For some reason, I have to refind drawer on page resume
        // or it stops responding to the toggle button
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuToggleIcon = (ImageView) findViewById(R.id.menuIcon);
    }

    /**
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

    /**
     * Process new location
     */
    private void handleNewLocation(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    /**
     * Manually stop checking for location updates
     */
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Manually start checking for location updates
     */
    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Called automatically when location changes
     */
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    /**
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
        /**
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /**
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /**
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * Easy toast maker
     */
    private void MakeToast(String message) {
        Toast.makeText(CustomerHomeActivity.this, message, Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    /**
     * Hide soft keyboard asynchronously
     */
    class HideKeyboard extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            imm.hideSoftInputFromWindow(actRef.getCurrentFocus().getWindowToken(), 0);
            return null;
        }
    }

    private void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            menuToggleIcon.setImageResource(R.drawable.menu_24);
        } else {
            mDrawerLayout.openDrawer(Gravity.START);
            menuToggleIcon.setImageResource(R.drawable.left_arrow_24);
        }
    }
}
