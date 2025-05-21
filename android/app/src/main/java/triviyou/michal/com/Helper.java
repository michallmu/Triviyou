package triviyou.michal.com;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class Helper {

    public void toasting(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            Network activeNetwork = connectivityManager.getActiveNetwork();
            if (activeNetwork != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork);
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
            }
        }
        return false;
    }

    public static class ReminderHelper {

        public static void setReminderAlarm(Context context) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notification_type", "notificationToPlayAgain");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            long duration = System.currentTimeMillis() + 10 * 1000; //current time + 10 sec

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, duration, pendingIntent);
            }
        }

        public static void cancelReminderAlarm(Context context) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notification_type", "notificationToPlayAgain");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    public static void onActivityStarted(Context context) {
        // משתמש נכנס לאפליקציה - בטל את ההתראה
        ReminderHelper.cancelReminderAlarm(context);
    }

    public static void onActivityStopped(Context context) {
        // משתמש יצא מהאפליקציה - הפעל את ההתראה
        ReminderHelper.setReminderAlarm(context);
    }


    public static class FragmentHelper {
        public static void closeFragment(FragmentManager fragmentManager, String fragmentTag) {
            Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);
            if (fragment != null) {
                fragmentManager.beginTransaction()
                        .remove(fragment)
                        .commitNow();
            }
        }
    }


}

