package com.example.taapesh.prototype;

import android.graphics.Point;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

public class CustomerLoginActivity extends ActionBarActivity{
    protected EditText userEmail;
    protected EditText userPassword;
    protected Button loginButton;

    // Screen and tab bar dimensions
    private static int screenWidth;
    private static int screenHeight;
    private static float screenDensity;

    private static Button employeeLoginBtn;
    private static Button businessLoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_login_activity);

        screenDensity = getResources().getDisplayMetrics().density;

        // Get user login fields
        userEmail = (EditText) findViewById(R.id.loginEmail);
        userPassword = (EditText) findViewById(R.id.loginPassword);
        loginButton = (Button) findViewById(R.id.loginButton);

        // Create listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user's credentials and convert to String
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                // Attempt to authenticate user and redirect to homepage
                // If info is incorrect, show error

                // Temporary: just go to homepage
                Intent goToHomepage = new Intent(CustomerLoginActivity.this, CustomerHomeActivity.class);
                startActivity(goToHomepage);
            }
        });

        businessLoginBtn = (Button) findViewById(R.id.businessLoginButton);
        employeeLoginBtn = (Button) findViewById(R.id.employeeLoginButton);

        // Alternate login for businesses
        businessLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToBusinessLogin = new Intent(CustomerLoginActivity.this, BusinessLoginActivity.class);
                startActivity(goToBusinessLogin);
            }
        });

        // Alternate login for employees
        employeeLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToEmployeeLogin = new Intent(CustomerLoginActivity.this, EmployeeLoginActivity.class);
                startActivity(goToEmployeeLogin);
            }
        });

        setupUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.default_menu, menu);
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

        // Set login and registration button specs
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(
                btnWidth, btnHeight);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        businessLoginBtn.setLayoutParams(rParams);

        rParams = new RelativeLayout.LayoutParams(
                btnWidth, btnHeight);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        employeeLoginBtn.setLayoutParams(rParams);
    }

    /**
     * Convert dp to pixels
     */
    private int dpToPx(int dp) {
        return Math.round((float)dp * screenDensity);
    }
}
