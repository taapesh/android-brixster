package com.example.taapesh.prototype;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.view.View;
import android.widget.TextView;


public class LoginCustomer extends ActionBarActivity {

    protected EditText userEmail;
    protected EditText userPassword;
    protected CheckBox rememberMe;
    protected Button loginButton;

    protected TextView businessLoginText;
    protected TextView employeeLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                Intent goToHomepage = new Intent(LoginCustomer.this, HomepageCustomer.class);
                startActivity(goToHomepage);
            }
        });

        // Create listeners for alternate login links that redirect to other activities
        businessLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToBusinessLogin = new Intent(LoginCustomer.this, BusinessLogin.class);
                startActivity(goToBusinessLogin);
            }
        });

        employeeLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToEmployeeLogin = new Intent(LoginCustomer.this, EmployeeLogin.class);
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
