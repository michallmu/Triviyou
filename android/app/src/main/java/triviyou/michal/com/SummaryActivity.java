package triviyou.michal.com;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {

    Context context;
    Intent goGames, inputIntent;
    Button bBackGamesList;
    CountDownTimer countDownTimer;
    TextView tvTimer;
    boolean isButtonClicked = false;
    int summaryDuration = 10000; // 10 seconds

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary);

        context = SummaryActivity.this;
        goGames = new Intent(context, GamesActivity.class);
        bBackGamesList = findViewById(R.id.bBackGamesList);
        inputIntent = getIntent();
        tvTimer = findViewById(R.id.tvTimer);

        // Start countdown timer
        countDownTimer = new CountDownTimer(summaryDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("מעבר אוטומטי בעוד " + (millisUntilFinished / 1000) + " שניות");
            }

            @Override
            public void onFinish() {
                if (!isButtonClicked) { // Only navigate if button wasn't clicked
                    startActivity(goGames);
                    finish();
                }
                triggerGameEndNotification(); // Trigger notification when the countdown finishes
            }
        }.start(); // <-- You forgot this!

        bBackGamesList.setOnClickListener(v -> {
            isButtonClicked = true; // Prevent auto navigation
            countDownTimer.cancel();
            startActivity(goGames);
            finish();
            triggerGameEndNotification(); // Trigger notification when the button is clicked
        });
    }

    private void triggerGameEndNotification() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class); // Create intent for notification
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set delay for notification (for example 10 seconds)
        long triggerAtMillis = System.currentTimeMillis() + 10000; // 10 seconds
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
