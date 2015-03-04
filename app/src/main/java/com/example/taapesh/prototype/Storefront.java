package com.example.taapesh.prototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


/*
 * Storefront is where users can see all the stores nearby that match
 * their search request. From here, they can click a button to request
 * additional info, or they can click to enter a particular store.
 */
public class Storefront extends ActionBarActivity {
    // Google Places object
    private GooglePlaces googlePlaces;

    // Current latitude and longitude of user
    private double latitude;
    private double longitude;

    // Store name
    private String storeName;

    // ArrayList to hold store info
    private ArrayList<Store> stores;

    // ListView to hold store cards
    private ListView storeResultsView;

    // Progress dialog
    ProgressDialog pDialog;

    // UI Elements

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storefront);

        // Create Google Places Object
        googlePlaces = new GooglePlaces();

        // Initialize store info ArrayList
        stores = new ArrayList<Store>();

        // Get referring intent and retrieve store name
        Intent it = getIntent();
        Bundle extras = it.getExtras();
        storeName = it.getStringExtra("store");
        latitude = extras.getDouble("latitude");
        longitude = extras.getDouble("longitude");

        // Find store results ListView
        storeResultsView = (ListView) findViewById(R.id.storeResultsView);
        storeResultsView.addFooterView(new View(this), null, false);
        storeResultsView.addHeaderView(new View(this), null, false);

        // Execute async background thread to load stores
        // After execution, assign store results to the list
        // view
        new LoadStores().execute(storeName);
    }

    /*
     * Asynchronously get store results using Google Places API
     * Create progress dialog and dismiss it when job is done
     */
    class LoadStores extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread show progress dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Storefront.this);
            pDialog.setMessage("Loading Storefront");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /*
         * Get store information asynchronously
         * Using google places API
         */
        protected String doInBackground(String... args) {
            String storeName = args[0];

            ArrayList<String> storeResults = googlePlaces.getStorePredictions(storeName, latitude, longitude);

            Map placeDetails = new HashMap();
            for (String store : storeResults) {
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
                    stores.add(s);
                }
            }

            return null;
        }

        /*
         * After completing background task, dismiss the progress dialog
         * And create the cards for each store. Finish by attaching the
         * CardArrayAdapter
         */
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            // Create custom array adapter and attach it to ListView
            CardAdapter cardAdapter= new CardAdapter(
                    Storefront.this,
                    R.layout.store_card,
                    stores);

            storeResultsView.setAdapter(cardAdapter);
        }
    }

    public class CardAdapter extends ArrayAdapter<Store> {
        private final LayoutInflater inflater;
        private final int layoutId;

        /*
         * General constructor
         *
         * @param context
         * @param resource
         * @param textViewResourceId
         * @param objects
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

            holder.storeName.setText(store.getName());
            holder.storeDetails.setText(store.getAddress());

            return itemView;
        }

        protected class ViewHolder{
            protected TextView storeName;
            protected TextView storeDetails;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_storefront, menu);
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
