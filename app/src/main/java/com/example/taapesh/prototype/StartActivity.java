package com.example.taapesh.prototype;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Page seen when user opens app for first time or is logged out
 */
public class StartActivity extends Activity {
    // Screen and tab bar dimensions
    private static int screenWidth;
    private static int screenHeight;
    private static float screenDensity;

    // Action buttons
    protected Button goToLoginButton;
    protected Button goToRegistrationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);
        screenDensity = getResources().getDisplayMetrics().density;

        goToLoginButton = (Button) findViewById(R.id.goToLoginButton);
        goToRegistrationButton = (Button) findViewById(R.id.goToRegistrationButton);

        // Create login button listener
        goToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to login page
                Intent goToLogin = new Intent(StartActivity.this, CustomerLoginActivity.class);
                startActivity(goToLogin);
            }
        });

        // Create register button listener
        goToRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to registration page
                Intent goToRegistration = new Intent(StartActivity.this, CustomerRegisterActivity.class);
                startActivity(goToRegistration);
            }
        });

        setupUI();
    }

    /**
     * Get all screen dimensions and setup tab bar dimensions
     */
    private void setupUI() {
        // Get screen dimensions
        WindowManager w = getWindowManager();
        Point size = new Point();
        w.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        int btnWidth = (int) (screenWidth / 2 - dpToPx(1)/2.0f);
        int btnHeight = dpToPx(68);
        Button loginBtn = (Button) findViewById(R.id.goToLoginButton);
        Button registerBtn = (Button) findViewById(R.id.goToRegistrationButton);

        // Set login and registration button specs
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(
            btnWidth, btnHeight);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        loginBtn.setLayoutParams(rParams);

        rParams = new RelativeLayout.LayoutParams(
                btnWidth, btnHeight);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        registerBtn.setLayoutParams(rParams);
    }

    /**
     * Convert dp to pixels
     */
    private int dpToPx(int dp) {
        return Math.round((float)dp * screenDensity);
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
