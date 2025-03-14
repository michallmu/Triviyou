package triviyou.michal.com;

import android.content.Context;
import android.widget.Toast;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public  class Helper {

    public void toasting(Context context,String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }


    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); // connection manager of the system and returns an event of it
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork(); // get the connection
            if (activeNetwork != null) { // might be null, such as an old nokia
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork); // allows checking the connection
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        }
        return false;
    }
    }