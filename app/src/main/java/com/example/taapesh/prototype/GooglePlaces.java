package com.example.taapesh.prototype;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import android.util.Log;

import java.net.URLEncoder;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.IOException;
import java.net.MalformedURLException;


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
                resultList.add(item.getString("description"));
                placeIds.add(item.getString("place_id"));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    /*
     * Get Google Place Details results based on place id
     * Return an array of place information
     */
    public Map getPlaceDetails(String reference) throws Exception {
        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        Map details = new Hashtable();

        try {
            StringBuilder sb = new StringBuilder(PLACES_DETAILS);
            sb.append("placeid=" + reference);
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
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        Log.i("JSONRESULTS", jsonResults.toString());
        // Now we have our JSON results, attempt to parse
        try {
            JSONObject jObject = new JSONObject(jsonResults.toString());

            // Get specific details from Json results such as name, type, address, etc.
            details.put("name", jObject.getString("name"));
            //details.put("address", jObject.get("vicinity").toString());
            //details.put("phone_number", jObject.getString("formatted_phone_number"));
            //details.put("types", jObject.getString("types"));
            return details;

        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return details;
    }
}
