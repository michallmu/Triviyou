package triviyou.michal.com;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {
    int countHistories;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        countHistories = intent.getIntExtra("countHistories", countHistories);
        showNotification(context);

    }

    private void showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "triviyou_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "TRIVIYOU Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        String message = context.getString(R.string.allThe) ;;
        if (countHistories > 0)
            message = context.getString(R.string.thereAre) + countHistories + context.getString(R.string.didntFinish);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.finishgameclear) // Add your app's notification icon
                .setContentTitle("תזכורת קטנה")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.blue));




        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());

        new Handler().postDelayed(() -> {
            notificationManager.cancel(notificationId); // Dismiss the notification after 8 seconds
        }, 8000); // 8000 milliseconds = 8 sec
    }

}

