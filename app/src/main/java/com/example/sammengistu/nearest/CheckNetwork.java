package com.example.sammengistu.nearest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckNetwork {
    public static boolean networkConnection(Context appContext) {
        ConnectivityManager cm =
            (ConnectivityManager) appContext.getSystemService (Context.CONNECTIVITY_SERVICE );

        NetworkInfo activeNetwork = cm . getActiveNetworkInfo ();
        return activeNetwork.isConnectedOrConnecting ();
    }
}
