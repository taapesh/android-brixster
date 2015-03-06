package com.example.taapesh.prototype;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Check all sources of internet for a live connection
 */
public class DetectConnection {
    private Context _context;

    public DetectConnection(Context context) {
        this._context = context;
    }

    public boolean canConnectToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();

            if (info != null) {
                int length = info.length;
                for (int i = 0; i < length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
