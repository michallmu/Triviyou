package triviyou.michal.com;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "Received broadcast for notification");

        // הוספת NotificationChannel עבור גרסאות Android 8 ומעלה
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Game Notifications";
            String description = "Notifications for game events";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("GAME_NOTIFICATION_CHANNEL", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // יצירת Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "GAME_NOTIFICATION_CHANNEL");
        builder.setSmallIcon(R.drawable.finishgameclear);
        builder.setContentTitle(context.getString(R.string.gameOver));
        builder.setContentText(context.getString(R.string.goodJobToTheList));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setAutoCancel(true);

        // הצגת notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

}
