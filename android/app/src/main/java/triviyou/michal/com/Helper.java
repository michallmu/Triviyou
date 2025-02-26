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
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); //מנהל החיבורים של המערכת ומחזיר מופע שלו
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork(); //מקבל את החיבור הנוכחי, אם אין יהיה נאל
            if (activeNetwork != null) { //יכול להיות נאל אם השירות לא קיים במערכת כמו נוקיה,האם קיימת רשת פעילה?
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork); //מאפשר לבדוק אם הרשת הנוכחית היא WiFi או סלולרית
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        }
        return false;
    }
    }