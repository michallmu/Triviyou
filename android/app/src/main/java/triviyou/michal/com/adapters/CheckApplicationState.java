package triviyou.michal.com.adapters;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import triviyou.michal.com.NotificationReceiver;

public class CheckApplicationState extends Application implements Application.ActivityLifecycleCallbacks {
        Context context = CheckApplicationState.this;
        private int activityCount = 0;

        @Override
        public void onCreate() {
            super.onCreate();
            registerActivityLifecycleCallbacks(this);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            activityCount++;
        }

        @Override
        public void onActivityStopped(Activity activity) {
            activityCount--;
            if (activityCount == 0) {
                // user left the application / close it
                callNotification();
            }
        }

        private void callNotification() {
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

        // other required overrides (empty)
        // these overrides supposed to catch the events of the activities (created, paused...)
        @Override public void onActivityCreated(Activity activity, android.os.Bundle savedInstanceState) {}
        @Override public void onActivityResumed(Activity activity) {}
        @Override public void onActivityPaused(Activity activity) {}
        @Override public void onActivitySaveInstanceState(Activity activity, android.os.Bundle outState) {}
        @Override public void onActivityDestroyed(Activity activity) {}
    }
