package com.example.taapesh.prototype;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Hashtable;
import java.util.Map;

/*
 * Storefront is where users can see all the stores nearby that match
 * their search request. From here, they can click a button to request
 * additional info, or they can click to enter a particular store.
 */
public class Storefront extends ActionBarActivity {
    // Google Places object
    private GooglePlaces googlePlaces;

    // Place details HashMap returned by Google Places API request
    Map placeDetails;

    // Progress dialog
    ProgressDialog pDialog;

    // UI Elements
    protected Button enterStoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storefront);

        // Get referring intent and retrieve place_id from it
        Intent i = getIntent();
        String placeID = i.getStringExtra("placeID");

        // Calling a Async Background thread
        new LoadPlaceDetails().execute(placeID);

        // Create Google Places Object
        // Initialize placeDetails map
        googlePlaces = new GooglePlaces();
        placeDetails = new Hashtable();

        enterStoreButton = (Button) findViewById(R.id.enterStoreButton);
        enterStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enter the store
            }
        });
    }

    /*
     * Asynchronously get place details
     * Create progress dialog and dismiss it when job is done
     */
    class LoadPlaceDetails extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread Show Progress Dialog
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
         * Get place details asynchronously
         */
        protected String doInBackground(String... args) {
            String placeID = args[0];

            // Attempt to get place details
            try {
                placeDetails = googlePlaces.getPlaceDetails(placeID);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task, dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            showDetails();
        }
    }

    /*
     * Test: show store name after details are retrieved
     */
    private void showDetails() {
        if (placeDetails.size() > 0) {
            Toast.makeText(Storefront.this, placeDetails.get("name").toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Storefront.this, "No details found", Toast.LENGTH_LONG).show();
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
