package com.example.taapesh.prototype;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class StorePasswordActivity extends ActionBarActivity {

    protected EditText storePasswordField;
    protected Button employeeLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_password_activity);

        storePasswordField = (EditText) findViewById(R.id.storePasswordField);
        employeeLoginButton = (Button) findViewById(R.id.employeeLoginButton);
        employeeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get store password as String
                String storePassword = storePasswordField.getText().toString().trim();

                // Check store password against database record
                //

                // If password is correct, go to Employee Homepage
                Intent goToEmployeeHomepage = new Intent(StorePasswordActivity.this, EmployeeHomeActivity.class);
                startActivity(goToEmployeeHomepage);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_enter_store_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
