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
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

    protected EditText userEmail;
    protected EditText userPassword;
    protected CheckBox rememberMe;
    protected Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userEmail = (EditText) findViewById(R.id.loginEmail);
        userPassword = (EditText) findViewById(R.id.loginPassword);
        rememberMe = (CheckBox) findViewById(R.id.rememberMeCheckBox);
        loginButton = (Button) findViewById(R.id.loginButton);

        // Create listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // toast
                Toast.makeText(LoginActivity.this, "Login pressed", Toast.LENGTH_SHORT).show();

                // Get user's credentials and convert to String
                String email = userEmail.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                // Attempt to authenticate user and redirect to homepage
                // If info is incorrect, show error

                // Temporary: just go to homepage
                Intent goToHomepage = new Intent(LoginActivity.this, HomepageActivity.class);
                startActivity(goToHomepage);
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
