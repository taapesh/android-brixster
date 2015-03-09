package com.example.taapesh.prototype;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;


public class StoreBrowseActivity extends ActionBarActivity {
    // Tab buttons
    private static TextView tabBackground;
    private static ImageButton storeButton;
    private static ImageButton barcodeButton;
    private static ImageButton cartButton;
    private static final int NUM_TABS = 3;
    private static final int TAB_DIVIDER_WIDTH = 1;
    private static final int TAB_BAR_HEIGHT = 75;

    // Screen and tab bar dimensions
    private static int screenWidth;
    private static int screenHeight;
    private static int tabWidth;
    private static int tabBarHeight;
    private static float screenDensity;

    // Tab host
    private static TabHost th;

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
        barcodeButton = (ImageButton) findViewById(R.id.barcodeButton);
        cartButton = (ImageButton) findViewById(R.id.cartButton);
        tabBackground = (TextView) findViewById(R.id.tabBackground);

        getScreenDimensions();
        setUpControlBar();
        setUpTabs();

        // Set tab bar click events
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToStore = new Intent(
                        StoreBrowseActivity.this, StoreBrowseActivity.class);
                goToStore.putParcelableArrayListExtra("itemsInCart", itemsInCart);
                goToStore.putExtra("hasCart", true);
                goToStore.putExtra("cartTotal", cartTotal.toString());
                goToStore.putExtra("cartSize", cartSize);
                startActivity(goToStore);
            }
        });

        barcodeButton.setOnClickListener(new View.OnClickListener() {
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


        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                int i = th.getCurrentTab();

                Toast.makeText(
                    StoreBrowseActivity.this,
                    "On tab " + Integer.toString(i),
                    Toast.LENGTH_SHORT
                ).show();

                // Depending on the tab chosen, start/stop
                // certain services

            }
        });
    }

    /**
     * Set tab bar height and tab widths
     */
    private void setUpControlBar() {
        tabBackground.getLayoutParams().height = tabBarHeight;
        storeButton.getLayoutParams().height = tabBarHeight;
        storeButton.getLayoutParams().width = tabWidth;
        cartButton.getLayoutParams().height = tabBarHeight;
        cartButton.getLayoutParams().width = tabWidth;
        barcodeButton.getLayoutParams().height = tabBarHeight;
        barcodeButton.getLayoutParams().width = tabWidth;
    }

    /**
     * Setup tab subviews
     */
    private void setUpTabs() {
        th = (TabHost) findViewById(R.id.tabHost);
        th.setup();

        // Setup browse tab
        TabHost.TabSpec specs = th.newTabSpec("tag1");
        specs.setContent(R.id.tab1);
        specs.setIndicator("Browse");
        th.addTab(specs);

        // Setup deals tab
        specs = th.newTabSpec("tag2");
        specs.setContent(R.id.tab2);
        specs.setIndicator("Deals");
        th.addTab(specs);

        // Setup store navigation tab
        specs = th.newTabSpec("tag3");
        specs.setContent(R.id.tab3);
        specs.setIndicator("Store Map");
        th.addTab(specs);
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
        tabWidth = (screenWidth / NUM_TABS) - dpToPx(TAB_DIVIDER_WIDTH);
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
        getMenuInflater().inflate(R.menu.menu_store_browse, menu);
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
    }
}
