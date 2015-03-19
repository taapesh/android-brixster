package com.example.taapesh.prototype;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;


public class StoreBrowseActivity extends ActionBarActivity {
    private static DrawerLayout mDrawerLayout;
    private static ImageView menuToggleIcon;
    private static View menuToggleArea;

    // Tab buttons
    private static TextView tabBackground;
    private static ImageButton storeButton;
    private static ImageButton scanButton;
    private static ImageButton cartButton;
    private static final int NUM_TABS = 3;
    private static final int TAB_DIVIDER_WIDTH = 1;
    private static final int TAB_BAR_HEIGHT = 58;

    // Screen and tab bar dimensions
    private static int screenWidth;
    private static int screenHeight;
    private static int tabWidth;
    private static int tabBarHeight;
    private static float screenDensity;

    private static Button businessLoginBtn;
    private static Button employeeLoginBtn;

    // Test data
    private static final String[] productNames = { "Peace Tea Georgia Peach Tea" };
    private static final String[] productCategories = { "Drinks" };
    private static final double[] productPrices = { 1.20 };
    private static final String[] productCodes = { "070847018544" };

    // Keep user's cart saved in the database
    // But also keep it locally in memory for faster display
    // Pass cart information from one activity to the next
    // until user has completed the session
    private static BigDecimal cartTotal;
    private static int cartSize;
    private static ArrayList<Product> itemsInCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_browse_activity);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.custom_action_bar);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuToggleIcon = (ImageView) findViewById(R.id.menuIcon);
        menuToggleArea = findViewById(R.id.menuToggleArea);

        menuToggleArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleDrawer();
            }
        });

        screenDensity = getResources().getDisplayMetrics().density;

        // Check for cart info passed by other activity
        Intent it = getIntent();
        boolean hasCart = it.getBooleanExtra("hasCart", false);

        if (hasCart) {
            // Get cart info
            cartSize = it.getIntExtra("cartSize", 0);

            // Must first get cart total as String and convert to BigDecimal
            String cartTotalString = it.getStringExtra("cartTotal");
            cartTotal = new BigDecimal(cartTotalString);

            // Get items in cart
            itemsInCart = it.getParcelableArrayListExtra("itemsInCart");

        } else {
            // Otherwise, initialize cart info
            // Initialize cart information
            cartTotal = BigDecimal.ZERO;
            cartSize = 0;
            itemsInCart = new ArrayList<>();
        }

        // Get tab bar buttons
        storeButton = (ImageButton) findViewById(R.id.storeButton);
        scanButton = (ImageButton) findViewById(R.id.scanButton);
        cartButton = (ImageButton) findViewById(R.id.cartButton);
        tabBackground = (TextView) findViewById(R.id.tabBackground);

        getScreenDimensions();
        setUpControlBar();

        // Set tab bar click events
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToScanning = new Intent(
                        StoreBrowseActivity.this, StoreScanActivity.class);
                goToScanning.putParcelableArrayListExtra("itemsInCart", itemsInCart);
                goToScanning.putExtra("hasCart", true);
                goToScanning.putExtra("cartTotal", cartTotal.toString());
                goToScanning.putExtra("cartSize", cartSize);
                startActivity(goToScanning);
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCart = new Intent(
                        StoreBrowseActivity.this, StoreCartActivity.class);
                goToCart.putParcelableArrayListExtra("itemsInCart", itemsInCart);
                goToCart.putExtra("hasCart", true);
                goToCart.putExtra("cartTotal", cartTotal.toString());
                goToCart.putExtra("cartSize", cartSize);
                startActivity(goToCart);
            }
        });

        mDrawerLayout.setDrawerListener(new ActionBarDrawerToggle(this,
                mDrawerLayout, null, 0, 0){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                menuToggleIcon.setImageResource(R.drawable.menu_24);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                menuToggleIcon.setImageResource(R.drawable.left_arrow_24);
            }});
    }

    /**
     * Set tab bar height and tab widths
     */
    private void setUpControlBar() {
        tabBackground.getLayoutParams().height = tabBarHeight + dpToPx(TAB_DIVIDER_WIDTH);
        storeButton.getLayoutParams().height = tabBarHeight;
        storeButton.getLayoutParams().width = tabWidth - dpToPx(TAB_DIVIDER_WIDTH);
        cartButton.getLayoutParams().height = tabBarHeight;
        cartButton.getLayoutParams().width = tabWidth - dpToPx(TAB_DIVIDER_WIDTH);
        scanButton.getLayoutParams().height = tabBarHeight;
        scanButton.getLayoutParams().width = tabWidth;
    }

    /**
     * Get all screen dimensions and setup tab bar dimensions
     */
    private void getScreenDimensions() {
        // Get screen dimensions
        WindowManager w = getWindowManager();
        Point size = new Point();
        w.getDefaultDisplay().getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        // Setup tab button widths, subtract value to set divider length
        tabWidth = (screenWidth / NUM_TABS);
        tabBarHeight = dpToPx(TAB_BAR_HEIGHT);
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

    @Override
    protected void onResume() {
        overridePendingTransition(0,0);
        super.onResume();

        // For some reason, I have to refind drawer on page resume
        // or it stops responding to the toggle button
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuToggleIcon = (ImageView) findViewById(R.id.menuIcon);
    }

    private void toggleDrawer() {
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawer(Gravity.START);
            menuToggleIcon.setImageResource(R.drawable.menu_24);
        } else {
            mDrawerLayout.openDrawer(Gravity.START);
            menuToggleIcon.setImageResource(R.drawable.left_arrow_24);
        }
    }
}
