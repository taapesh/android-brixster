package com.example.taapesh.prototype;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.widget.EditText;
import android.widget.Button;


public class RegisterActivity extends Activity {

    protected EditText userFirstName;
    protected EditText userLastName;
    protected EditText userEmail;
    protected EditText userPassword;

    protected Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize user registration fields
        userFirstName = (EditText) findViewById(R.id.registerFirstName);
        userLastName = (EditText) findViewById(R.id.registerLastName);
        userEmail = (EditText) findViewById(R.id.registerEmail);
        userPassword = (EditText) findViewById(R.id.registerPassword);

        registerButton = (Button) findViewById(R.id.registerButton);

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
