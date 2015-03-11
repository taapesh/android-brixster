package com.example.taapesh.prototype;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.mirasense.scanditsdk.ScanditSDKBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDK;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


public class StoreScanActivity extends Activity
    implements ScanditSDKListener {
    /**
     * If quick scan is enabled, items will be added
     * to cart as soon as they are scanned.
     * Otherwise, item info will be displayed first,
     * along with a button for add to cart.
     */
    private static boolean quickScan = false;

    public static final int CANCEL = 0;

    // Progress dialog
    ProgressDialog pDialog;

    // ScanditSDK object
    private ScanditSDK mPicker;

    // Store details
    private static String storeName;
    private static String storeAddress;

    private static final String SCANDIT_API_KEY = "RM7dYB+aa4yeb8axHN2SVH+gyyr20c5sF0LQHztKorY";

    // Screen and tab bar dimensions
    private static int screenWidth;
    private static int screenHeight;
    private static int tabWidth;
    private static int tabBarHeight;
    private static float screenDensity;
    private static int addBtnPadding;

    // UI Elements
    private static final int NUM_TABS = 3;
    private static final int TAB_DIVIDER_WIDTH = 1;
    private static final int TAB_BAR_HEIGHT = 58;
    private static final int PRODUCT_CARD_HEIGHT = 165;
    private static final int PRODUCT_CARD_PADDING = 4;

    private static TextView tabBackground;
    private static ImageButton storeButton;
    private static ImageButton scanButton;
    private static ImageButton cartButton;

    private static ImageButton menuToggleBtn;
    private static View productCardView;
    private static ImageButton confirmBtn;
    private static ImageButton cancelBtn;
    private static RelativeLayout rootView;

    // Test data
    private static final String[] productNames = { "Peace Tea Georgia Peach Tea" };
    private static final String[] productCategories = { "Drinks" };
    private static final BigDecimal[] productPrices = { new BigDecimal(1.20) };
    private static final String[] productCodes = { "070847018544" };
    private static final int numProducts = productNames.length;

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
        setContentView(R.layout.store_scan_activity);
        rootView = (RelativeLayout) findViewById(R.id.root);
        screenDensity = getResources().getDisplayMetrics().density;

        // Get referring intent and identify the store
        Intent it = getIntent();
        Bundle extras = it.getExtras();
        storeName = it.getStringExtra("store");
        storeAddress = it.getStringExtra("address");

        // Get cart information
        itemsInCart = it.getParcelableArrayListExtra("itemsInCart");
        cartSize = it.getIntExtra("cartSize", 0);
        String cartTotalString = it.getStringExtra("cartTotal");
        if (cartTotalString.isEmpty()) {
            cartTotal = BigDecimal.ZERO;
        } else {
            cartTotal = new BigDecimal(cartTotalString);
        }

        // After store is identified, load store catalog and info asynchronously

        // Get tab bar buttons
        storeButton = (ImageButton) findViewById(R.id.storeButton);
        scanButton = (ImageButton) findViewById(R.id.scanButton);
        cartButton = (ImageButton) findViewById(R.id.cartButton);
        tabBackground = (TextView) findViewById(R.id.tabBackground);

        // Set tab bar click events
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToStore = new Intent(
                        StoreScanActivity.this, StoreBrowseActivity.class);
                goToStore.putParcelableArrayListExtra("itemsInCart", itemsInCart);
                goToStore.putExtra("hasCart", true);
                goToStore.putExtra("cartTotal", cartTotal.toString());
                goToStore.putExtra("cartSize", cartSize);
                goToStore.putExtra("hasCart", true);
                startActivity(goToStore);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToScanning = new Intent(
                        StoreScanActivity.this, StoreScanActivity.class);
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
                        StoreScanActivity.this, StoreCartActivity.class);
                goToCart.putParcelableArrayListExtra("itemsInCart", itemsInCart);
                goToCart.putExtra("hasCart", true);
                goToCart.putExtra("cartTotal", cartTotal.toString());
                goToCart.putExtra("cartSize", cartSize);
                startActivity(goToCart);
            }
        });

        // Setup UI elements
        menuToggleBtn = (ImageButton) findViewById(R.id.menuButton);
        productCardView = findViewById(R.id.productCard);
        confirmBtn = (ImageButton) findViewById(R.id.confirmBtn);
        cancelBtn = (ImageButton) findViewById(R.id.cancelBtn);
        //productCardView.setVisibility(View.GONE);

        getScreenDimensions();
        // Initialize scanner
        initializeScanner();
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
        addBtnPadding = screenWidth / 3;
    }

    /**
     * Setup UI elements on top of scan view
     */
    private void setupUI() {
        int btnSize = dpToPx(58);
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(
                btnSize, btnSize);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        rootView.removeView(menuToggleBtn);
        rootView.addView(menuToggleBtn, rParams);

        rParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dpToPx(PRODUCT_CARD_HEIGHT));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.bottomMargin = tabBarHeight + dpToPx(PRODUCT_CARD_PADDING);
        rootView.removeView(productCardView);
        rootView.addView(productCardView, rParams);

        cancelBtn.setPadding(0, 0, addBtnPadding, 0);
        confirmBtn.setPadding(addBtnPadding, 0, 0, 0);
    }

    /**
     * Convert dp to pixels
     */
    private int dpToPx(int dp) {
        return Math.round((float)dp * screenDensity);
    }

    /**
     * Set tab bar height and tab widths
     * We have to add the buttons after
     * the scan view so that they render on top
     */
    private void setUpTabs() {
        // Add tab bar background to root view
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, tabBarHeight+dpToPx(TAB_DIVIDER_WIDTH));
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

        rootView.removeView(tabBackground);
        rootView.addView(tabBackground, rParams);

        // Add store button to root view
        rParams = new RelativeLayout.LayoutParams(
                tabWidth, tabBarHeight);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.addRule(RelativeLayout.ALIGN_LEFT);
        rootView.removeView(storeButton);
        rootView.addView(storeButton, rParams);

        // Add barcode button to root view
        rParams = new RelativeLayout.LayoutParams(
                tabWidth, tabBarHeight);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        rootView.removeView(scanButton);
        rootView.addView(scanButton, rParams);

        // Add cart button to root view
        rParams = new RelativeLayout.LayoutParams(
                tabWidth, tabBarHeight);
        rParams.addRule(RelativeLayout.ALIGN_BOTTOM);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rParams.addRule(RelativeLayout.ALIGN_RIGHT);
        rParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rootView.removeView(cartButton);
        rootView.addView(cartButton, rParams);
    }

    /**
     * Initialize the barcode scanner and setup the view
     */
    public void initializeScanner() {
        // Instantiate the default barcode picker
        ScanditSDKBarcodePicker picker =
                new ScanditSDKBarcodePicker(
                        StoreScanActivity.this, SCANDIT_API_KEY, ScanditSDKBarcodePicker.CAMERA_FACING_BACK);

        // Register listener, in order to be notified about relevant events
        // (e.g. a successfully scanned bar code).
        picker.getOverlayView().addListener(this);

        // Setup scanner view dimensions
        RelativeLayout.LayoutParams rParams = new RelativeLayout.LayoutParams(
                        LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        // Add scan view to root view
        rootView.addView(picker, rParams);

        // Determine how far to up to move scanner hotspot to center it vertically
        float hotSpotY = 0.5f - (1.0f * tabBarHeight / screenHeight) / 2.0f;
        //picker.setScanningHotSpot(0.5f, hotSpotY);
        picker.setScanningHotSpot(0.5f, hotSpotY);

        // Add tab bar on top of scanner view
        setUpTabs();

        // Setup UI on top of the scanner view
        setupUI();

        mPicker = picker;

        // Start the scanner asynchronously
        new StartScanner().execute();
    }

    /**
     * Once the activity is in the foreground again, restart scanning.
     */
    @Override
    protected void onResume() {
        overridePendingTransition(0,0);
        if (mPicker != null) {
            // Start the scanner asynchronously
            new StartScanner().execute();
        }
        super.onResume();
    }

    /**
     * When the activity is in the background immediately stop the
     * scanning to save resources and free the camera.
     */
    @Override
    protected void onPause() {
        if (mPicker != null) {
            mPicker.stopScanning();
        }
        super.onPause();
    }

    /**
     * Handle scanned barcode
     * Called on every successful scan
     */
    @Override
    public void didScanBarcode(String barcode, String symbology) {
        barcode = barcode.trim();

        // Lookup barcode
        lookupProduct(barcode);
    }

    @Override
    public void didManualSearch(String entry) {
        // This callback is called when you use the Scandit SDK search bar.
    }

    /**
     * Called when the user canceled the bar code scanning.
     */
    @Override
    public void didCancel() {
        // This callback is deprecated since Scandit SDK 3.0
    }

    /**
     * Start scanner asynchronously
     */
    class StartScanner extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            mPicker.startScanning();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

    /**
     * Asynchronously load store information
     */
    class LoadStores extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread show progress dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StoreScanActivity.this);
            pDialog.setMessage("Loading Store");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * Get store information asynchronously
         */
        protected String doInBackground(String... args) {
            return null;
        }

        /**
         * After completing background task, dismiss the progress dialog
         * And create the cards for each store. Finish by attaching the
         * CardArrayAdapter
         */
        protected void onPostExecute(String result) {
            pDialog.dismiss();

            Toast.makeText(StoreScanActivity.this,
                    "TEST: Welcome to " + storeName + "\n" + storeAddress,
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_store_entry, menu);
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
     * Look up scanned product in database
     */
    public void lookupProduct(String barcode) {

        int idx = findProduct(barcode);


        if (idx >= 0) {
            // Get product info from database
            String productName;
            String productPrice;
            String productDescription;
            String productDiscount;
            String pricePerUnit;
            String pricePerPound;

            if (quickScan) {
                // If quick scan is enabled, add item to card
                addScannedToCart();
            } else {
                // Show info card for product
                showProductCard(idx);
            }
        } else {
            Toast.makeText(StoreScanActivity.this, "Item not found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Display card with product information
     * along with a button to add product to cart
     */
    public void showProductCard(int i) {
        Product product = new Product(
                productNames[i], productPrices[i], productCodes[i]);
        BigDecimal price = productPrices[i];


        Toast.makeText(
                StoreScanActivity.this,
                product.getProductName() +"\n"+
                        product.getProductPrice().setScale(2, RoundingMode.CEILING) +"\n"+
                "Added to cart",
                Toast.LENGTH_LONG).show();

        // Display product cart with product information, along with button to add to cart
    }

    /**
     * Add the scanned item to cart
     */
    public void addScannedToCart() {
        /**
         * Display item name that was added
         * The item price, and updated cart total.
         * Show option to undo.
         */
    }

    /**
     * Add searched item to cart
     * from the item view interface
     */
    public void addSearchedToCart() {
        /**
         * Display item name that was added,
         * item price, and updated cart total.
         * Show option to undo
         */
    }

    /**
     * Find product and return index found at
     */
    private int findProduct(String code) {
        for(int i = 0; i < numProducts; i++) {
            if (productCodes[i].equals(code)) {
                return i;
            }
        }

        // Product not found, return -1
        return -1;
    }
}
