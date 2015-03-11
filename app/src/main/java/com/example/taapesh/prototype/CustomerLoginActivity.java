package com.example.taapesh.prototype;

import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

public class CustomerLoginActivity extends ActionBarActivity{
    protected EditText userEmail;
    protected EditText userPassword;
    protected CheckBox rememberMe;
    protected Button loginButton;

    protected TextView businessLoginText;
    protected TextView employeeLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_login_activity);

        // Get user login fields
        userEmail = (EditText) findViewById(R.id.loginEmail);
        userPassword = (EditText) findViewById(R.id.loginPassword);
        rememberMe = (CheckBox) findViewById(R.id.rememberMeCheckBox);
        loginButton = (Button) findViewById(R.id.loginButton);

        // Get business and employee login links
        businessLoginText = (TextView) findViewById(R.id.businessLogin);
        employeeLoginText = (TextView) findViewById(R.id.employeeLogin);

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

        // Alternate login for businesses
        businessLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToBusinessLogin = new Intent(CustomerLoginActivity.this, BusinessLoginActivity.class);
                startActivity(goToBusinessLogin);
            }
        });

        // Alternate login for employees
        employeeLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToEmployeeLogin = new Intent(CustomerLoginActivity.this, EmployeeLoginActivity.class);
                startActivity(goToEmployeeLogin);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
