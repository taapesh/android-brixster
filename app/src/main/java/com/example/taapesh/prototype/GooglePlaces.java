package com.example.taapesh.prototype;

/*
 * Handles place searches
 * and place detail requests
 */
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import java.net.URL;
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.io.InputStreamReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;


public class GooglePlaces {
    private static final String TAG = GooglePlaces.class.getSimpleName();

    // Temporary: list of accepted places
    private static final String[] stores = { "h-e-b", "kroger", "randalls", "supertarget",
            "target", "trader joe's", "walmart", "walmart supercenter", "whole foods",
            "whole foods inc", "whole foods market", "whole foods market inc" };

    private static final int NUM_STORES = stores.length;

    // Google Places strings
    private static final String PLACES_AUTOCOMPLETE = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    private static final String PLACES_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String PLACES_API_KEY = "AIzaSyD9i1BbRuXDRhQvxJ8OOoVzyrzbgg2II6o";

    private static final String types ="establishment";
    private static final int MAX_RESULTS = 5;

    public GooglePlaces() {

        // Test: check if stores array is sorted
        boolean sorted = true;
        for(int i = 0; i < stores.length-1; i ++){
            if (stores[i].compareTo(stores[i+1]) > 0) {
                Log.i("SORTED", "NO, NOT SORTED!!! " + stores[i] + " " + stores[i+1]);
                sorted = false;
                break;
            }
        } if (sorted) Log.i("SORTED", "YES SORTED!!!");
    }

    /*
     * Use placeID to get store details and create store card
     */
    public ArrayList<String> createStoreCard(String placeID) {
        ArrayList<String> resultList = null;

        return resultList;
    }

    /*
     * Get Google Places autocomplete results based on user input and location
     */
    public ArrayList<String> getStorePredictions(String name, double lat, double lon) {
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_AUTOCOMPLETE);

            String latitude = Double.valueOf(lat).toString();
            String longitude = Double.valueOf(lon).toString();
            String location = latitude+","+longitude;

            sb.append("input=" + URLEncoder.encode(name, "utf8"));
            sb.append("&types=" + "establishment");
            sb.append("&location=" + location);
            sb.append("&radius=15000");
            sb.append("&key=" + PLACES_API_KEY);

            URL url = new URL(sb.toString());
            Log.i("URL", url.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        /*
         * Parse results and add them to results list
         */
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            int lenPredArray = predsJsonArray.length();

            // Create result and placeID lists
            resultList = new ArrayList<>();

            int resultsAdded = 0;

            for (int i = 0; i < lenPredArray; i++) {
                JSONObject item = predsJsonArray.getJSONObject(i);

                // Determine if store should be included in results
                String desc = item.getString("description");
                String placeID = item.getString("place_id");

                int idx = desc.indexOf(",");
                String storeName = desc.substring(0, idx);
                String storeNameLower = storeName.toLowerCase();

                int found = Arrays.binarySearch(stores, storeNameLower);
                if (found >= 0) {
                    Log.i("BINARY", "Found through binary search!");
                    resultList.add(placeID);

                    resultsAdded++;
                    if (resultsAdded == MAX_RESULTS) {
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }
        // Return results
        return resultList;
    }

    /*
     * Get Google Place Details results based on place id
     * Return an array of place information
     */
    public Map getStoreDetails(String reference) throws Exception {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        Map details = new HashMap();

        try {
            // Build Google Places request URL
            StringBuilder sb = new StringBuilder(PLACES_DETAILS);
            sb.append("placeid=" + reference);
            sb.append("&key=" + PLACES_API_KEY);

            // Open a a connection and read in json results
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[8192];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        // Now we have our JSON results, attempt to parse
        try {
            // Create a JSONObject with the received response string
            JSONObject jsonObject = new JSONObject(jsonResults.toString());
            JSONObject results = jsonObject.getJSONObject("result");

            // Get specific details from Json results such as name, type, address, etc.
            details.put("name", results.getString("name"));
            details.put("address", results.get("vicinity").toString());
            details.put("phone_number", results.getString("formatted_phone_number"));
            details.put("types", results.getString("types"));
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return details;
    }
}
