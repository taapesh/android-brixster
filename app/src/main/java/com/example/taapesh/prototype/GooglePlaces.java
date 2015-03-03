package com.example.taapesh.prototype;

/*
 * Handles place searches
 * and place detail requests
 */
import android.util.Log;
import java.util.ArrayList;
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

    // Google Places strings
    private static final String PLACES_AUTOCOMPLETE = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    private static final String PLACES_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String PLACES_API_KEY = "AIzaSyD9i1BbRuXDRhQvxJ8OOoVzyrzbgg2II6o";

    private static final String types ="establishment";
    private static final int MAX_RESULTS = 5;

    // Store place IDs of returned places
    public static ArrayList<String> placeIds;

    public GooglePlaces() {
        placeIds = new ArrayList<>();
    }

    /*
     * Get Google Places autocomplete results based on user input and location
     */
    public ArrayList<String> autocomplete(String input, double lat, double lon) {
        placeIds.clear();
        ArrayList<String> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_AUTOCOMPLETE);

            String latitude = Double.valueOf(lat).toString();
            String longitude = Double.valueOf(lon).toString();
            String location = latitude+","+longitude;

            sb.append("input=" + URLEncoder.encode(input, "utf8"));
            sb.append("&types=" + "establishment");
            sb.append("&location=" + location);
            sb.append("&radius=100");
            sb.append("&key=" + PLACES_API_KEY);

            URL url = new URL(sb.toString());
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

            // If predictions are less than 5, use that length for results array
            // Otherwise, cap results to max length
            int lenResults = (lenPredArray > MAX_RESULTS) ? MAX_RESULTS : lenPredArray;
            resultList = new ArrayList<String>(lenResults);
            placeIds = new ArrayList<String>(lenResults);

            for (int i = 0; i < lenResults; i++) {
                JSONObject item = predsJsonArray.getJSONObject(i);

                // Get place details and fill them in
                String desc = item.getString("description");

                // Remove country, user knows which country they are in
                desc = desc.replace(", United States", "");

                // Add result to list and store corresponding place_id
                resultList.add(desc);
                placeIds.add(item.getString("place_id"));
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
    public Map getPlaceDetails(String reference) throws Exception {
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
            char[] buff = new char[2048];
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
            Log.i("TYPES", details.get("types").toString());
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return details;
    }
}
