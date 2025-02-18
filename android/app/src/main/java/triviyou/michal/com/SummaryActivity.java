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
            }
        }.start(); // <-- You forgot this!

        bBackGamesList.setOnClickListener(v -> {
            isButtonClicked = true; // Prevent auto navigation
            countDownTimer.cancel();
            startActivity(goGames);
            finish();
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
