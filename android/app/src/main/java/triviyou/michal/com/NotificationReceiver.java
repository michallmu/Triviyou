package triviyou.michal.com;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class NotificationReceiver extends BroadcastReceiver {
    int countHistories;

    @Override
    public void onReceive(Context context, Intent intent) {

        countHistories = intent.getIntExtra("countHistories", 0);
        String notificationType = intent.getStringExtra("notification_type");
        if(notificationType.equals("notificationToPlayAgain"))
            showNotification(context, notificationType,false);
        else if (notificationType.equals("notificationShowHistories")) {
            showNotification(context, notificationType,true);
        }
    }

    private void showNotification(Context context, String notificationType,boolean autoDismiss) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "triviyou_channel";

        //check that the device's version supports notification firing
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "TRIVIYOU Alerts", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        else {
            return; //the device is older 26
        }

        //build the messsage to show in notification
        String message = context.getString(R.string.allThe);
        if (notificationType.equals("notificationShowHistories") && countHistories > 0)
            message = context.getString(R.string.thereAre) + countHistories + context.getString(R.string.didntFinish);

        if (notificationType.equals("notificationToPlayAgain")) {
            message = context.getString(R.string.reminderContinuePlaying);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.finishgameclear)
                .setContentTitle(context.getString(R.string.reminding))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.blue));

        int notificationId = 1;
        notificationManager.notify(notificationId, builder.build());
        if(autoDismiss) {
            new Handler().postDelayed(() -> notificationManager.cancel(notificationId), 8000);
        }
    }
}
