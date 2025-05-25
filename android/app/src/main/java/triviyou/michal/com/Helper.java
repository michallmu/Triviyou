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
            Network activeNetwork = connectivityManager.getActiveNetwork(); // get active network
            if (activeNetwork != null) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(activeNetwork); // get capabilities
                return capabilities != null &&
                        (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || // has Wi-Fi
                                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)); // or has cellular data
            }
        }
        return false; // no internet
    }

    public static class ReminderHelper {

        // set an alarm to show a notification in 2 minutes
        public static void setReminderAlarm(Context context) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notification_type", "notificationToPlayAgain");

            // create a pending intent with flag to update if already exists
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
                    | PendingIntent.FLAG_IMMUTABLE);

            // set the alarm to trigger the notification
            long duration = System.currentTimeMillis() + 2 * 60 * 1000;

            if (alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, duration, pendingIntent);
            }
        }

        // cancel the previously set reminder alarm
        public static void cancelReminderAlarm(Context context) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("notification_type", "notificationToPlayAgain");

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT
                    | PendingIntent.FLAG_IMMUTABLE);

            // cancel the alarm if it exists
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }
        }
    }

    // called when the activity becomes visible, cancels any pending reminder
    public static void onActivityStarted(Context context) {
        //cancel notification
        ReminderHelper.cancelReminderAlarm(context);
    }

    // called when the activity is no longer visible, sets a reminder to bring user back
    public static void onActivityStopped(Context context) {
        //user left the app – activate the reminder
        ReminderHelper.setReminderAlarm(context);
    }




}

