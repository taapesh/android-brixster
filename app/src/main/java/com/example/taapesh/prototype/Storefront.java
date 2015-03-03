package com.example.taapesh.prototype;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;


public class Storefront extends ActionBarActivity {

    private GooglePlaces googlePlaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storefront);

        Intent i = getIntent();
        String reference = i.getStringExtra("reference");

        googlePlaces = new GooglePlaces();
        Map details = new Hashtable();
        try {
            details = googlePlaces.getPlaceDetails(reference);
        } catch (Exception e) {

        }

        Toast.makeText(Storefront.this, reference, Toast.LENGTH_LONG);
        if (details.size() > 0) {
            Toast.makeText(Storefront.this, details.get("name").toString(), Toast.LENGTH_LONG);
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
