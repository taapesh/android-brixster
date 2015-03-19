package com.example.taapesh.prototype;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


public class StoreCartActivity extends ActionBarActivity {
    // Screen and tab bar dimensions
    private static int screenWidth;
    private static int screenHeight;
    private static int tabWidth;
    private static int tabBarHeight;
    private static float screenDensity;

    private static Button checkoutBtn;

    // Tab buttons
    private static TextView tabBackground;
    private static ImageButton storeButton;
    private static ImageButton scanButton;
    private static ImageButton cartButton;
    private static final int NUM_TABS = 3;
    private static final int TAB_DIVIDER_WIDTH = 1;
    private static final int TAB_BAR_HEIGHT = 58;

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

    // ListView to hold cart contents
    private static ListView cartContentsView;

    // Bitmap images to be saved
    private static ArrayList<Bitmap> bitmaps;

    private static BarcodeGenerator barcodeGenerator;

    // Button to apply coupon, button to checkout

    // TextView to show total cost

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_cart_activity);

        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.custom_action_bar);

        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);

        screenDensity = getResources().getDisplayMetrics().density;

        // Get tab bar buttons
        storeButton = (ImageButton) findViewById(R.id.storeButton);
        scanButton = (ImageButton) findViewById(R.id.scanButton);
        cartButton = (ImageButton) findViewById(R.id.cartButton);
        tabBackground = (TextView) findViewById(R.id.tabBackground);

        getScreenDimensions();
        setUpTabs();

        checkoutBtn = (Button) findViewById(R.id.checkoutBtn);
        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveOrder();
            }
        });

        // Set tab bar click events
        storeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToStore = new Intent(
                        StoreCartActivity.this, StoreBrowseActivity.class);
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
                        StoreCartActivity.this, StoreScanActivity.class);
                goToScanning.putParcelableArrayListExtra("itemsInCart", itemsInCart);
                goToScanning.putExtra("hasCart", true);
                goToScanning.putExtra("cartTotal", cartTotal.toString());
                goToScanning.putExtra("cartSize", cartSize);
                goToScanning.putExtra("hasCart", true);
                startActivity(goToScanning);
            }
        });

        // Get cart information
        Intent it = getIntent();

        itemsInCart = it.getParcelableArrayListExtra("itemsInCart");
        cartSize = it.getIntExtra("cartSize", 0);
        String cartTotalString = it.getStringExtra("cartTotal");
        if (cartTotalString.isEmpty()) {
            cartTotal = BigDecimal.ZERO.setScale(2, RoundingMode.CEILING);
        } else {
            cartTotal = new BigDecimal(cartTotalString);
        }

        TextView tv = (TextView) findViewById(R.id.cartInfo);
        String info = "Items in cart: " + cartSize + "\n";
        info += "Total cost: $" + cartTotal.toString() + "\n";
        info += "Items in Cart:\n";
        for(Product p : itemsInCart) {
            info += p.getProductName() + "  $" + p.getProductPrice() + "\n" + p.getProductCode();
        }

        tv.setText(info);

        // Setup barcode images
        barcodeGenerator = new BarcodeGenerator();
        bitmaps = new ArrayList<>();
    }

    /**
     * Set tab bar height and tab widths
     */
    private void setUpTabs() {
        tabBackground.getLayoutParams().height = tabBarHeight + dpToPx(TAB_DIVIDER_WIDTH);
        storeButton.getLayoutParams().height = tabBarHeight;
        storeButton.getLayoutParams().width = tabWidth  - dpToPx(TAB_DIVIDER_WIDTH);
        cartButton.getLayoutParams().height = tabBarHeight;
        cartButton.getLayoutParams().width = tabWidth  - dpToPx(TAB_DIVIDER_WIDTH);
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
    }

    /**
     * Remove an item from cart,
     * remove it locally and from database
     */
    private void removeItem(int idx) {
        itemsInCart.remove(idx);

        cartSize -= 1;
        updateCartTotal();
    }

    /**
     * Update total cost of cart
     */
    private void updateCartTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for(Product p : itemsInCart) {
            total = total.add(p.getProductPrice());
        }

        // Calculate sales and/or coupon discounts

        // Set cart total and display it
        cartTotal = total;
    }

    /**
     * Get current cart total
     */
    private BigDecimal getCartTotal() {
        return cartTotal.setScale(2, RoundingMode.CEILING);
    }


    private void saveOrder() {
        Toast.makeText(StoreCartActivity.this, "Saving order information...", Toast.LENGTH_SHORT).show();

        // Insert new order into orders table in Amazon DynamoDB
        // Order info:
        // Shopper name
        // Shopper email
        // Number of items purchased
        // For each product:
            // product name (if available)
            // product code
            // product code symbology
            // proof of purchase image

        for (Product p : itemsInCart) {
            String code = p.getProductCode();
            String symbology = p.getCodeSymbology();
            String name = p.getProductName();
        }
    }

    private void generateBarcodeImage(String code, String symbology) {
        try {
            BarcodeFormat format = barcodeGenerator.getBarcodeFormat(symbology);
            if (format != null) {
                Bitmap bmp = barcodeGenerator.encodeAsBitmap(code, format);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }
}
