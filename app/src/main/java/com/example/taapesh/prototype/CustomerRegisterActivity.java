package com.example.taapesh.prototype;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;


public class CustomerRegisterActivity extends ActionBarActivity {
    // UI Elements
    protected EditText userFirstName;
    protected EditText userLastName;
    protected EditText userEmail;
    protected EditText userPassword;
    protected Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_register_activity);

        // Initialize user registration fields
        userFirstName = (EditText) findViewById(R.id.registerFirstName);
        userLastName = (EditText) findViewById(R.id.registerLastName);
        userEmail = (EditText) findViewById(R.id.registerEmail);
        userPassword = (EditText) findViewById(R.id.registerPassword);

        registerButton = (Button) findViewById(R.id.registerButton);

        // Create listener for register button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user information and convert to String
                String firstName = userFirstName.getText().toString().trim();
                String lastName = userLastName.getText().toString().trim();
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                // Check if email is already in use or if
                // password does not meet requirements and if so, display error message

                // Store user in AWS DynamoDB
                // If successful, show success message and take user to Homepage

                // Temporary: just go to homepage
                Intent goToHomepage = new Intent(CustomerRegisterActivity.this, CustomerHomeActivity.class);
                startActivity(goToHomepage);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
