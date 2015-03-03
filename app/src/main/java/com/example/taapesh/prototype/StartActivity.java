package com.example.taapesh.prototype;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

/*
 * Page seen when user opens app for first time or is logged out
 */
public class StartActivity extends ActionBarActivity {

    // Action buttons
    protected Button goToLoginButton;
    protected Button goToRegistrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        goToLoginButton = (Button) findViewById(R.id.goToLoginButton);
        goToRegistrationButton = (Button) findViewById(R.id.goToRegistrationButton);

        // Create login button listener
        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to login page
                Intent goToLogin = new Intent(StartActivity.this, LoginCustomer.class);
                startActivity(goToLogin);
            }
        });

        // Create register button listener
        goToRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to registration page
                Intent goToRegistration = new Intent(StartActivity.this, RegisterCustomer.class);
                startActivity(goToRegistration);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_start, menu);
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
