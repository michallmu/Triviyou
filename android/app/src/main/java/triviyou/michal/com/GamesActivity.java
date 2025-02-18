package triviyou.michal.com;
import android.Manifest;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;
import triviyou.michal.com.adapters.GameAdapter;
import triviyou.michal.com.entities.Game;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class GamesActivity extends AppCompatActivity {

    private ListView lvGames;
    private FirebaseFirestore db;
    private List<Game> gameList;
    GameAdapter adapter;

    ImageButton imgBback3, imageAccount;
    Intent goLogin, goProfile, inputIntent;
    Context context;
    String email, userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        context = GamesActivity.this;
        inputIntent = getIntent();
        goLogin = new Intent(context, LoginActivity.class);
        goProfile = new Intent(context, ProfileActivity.class);
        imgBback3 = findViewById(R.id.imgBback3);
        lvGames = findViewById(R.id.lvGames);
        imageAccount = findViewById(R.id.imageAccount);

        lvGames.setClickable(true);

        userId = inputIntent.getStringExtra("userId");

        imgBback3.setOnClickListener(v -> startActivity(goLogin));

        imageAccount.setOnClickListener(v -> startActivity(goProfile));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        gameList = new ArrayList<>();
        adapter = new GameAdapter(this, gameList);
        // Set adapter to ListView
        lvGames.setAdapter(adapter);
        getGamesFromDB();

        checkUnfinishedGamesForUser();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

    }

    private void checkUnfinishedGamesForUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("userGameHistory")
                .whereEqualTo("userId", userId)
                .whereEqualTo("finished", false)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        scheduleNotification(queryDocumentSnapshots.size());
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching unfinished games", e));
    }

    private void scheduleNotification(int countHistories) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("countHistories", countHistories); // Pass message

        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            long triggerTime = System.currentTimeMillis() + 3000; // 3 seconds delay

            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
                Log.d("AlarmManager", "Notification scheduled in 3 seconds with message: " + countHistories);
            } else {
                Log.e("AlarmManager", "Failed to get AlarmManager instance.");
            }
        }

        catch (Exception e) {
            Log.e("error loading size", "failed");
        }
    }

    private void getGamesFromDB() {
        db.collection("games")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        gameList.clear(); // Clear old data

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Game game = document.toObject(Game.class);

                            //this handle the boolean from firestore object
                            boolean isActive = document.getBoolean("isActive");
                            game.setActive(isActive);

                            gameList.add(game);
                        }
                        adapter.notifyDataSetChanged(); // Update ListView

                        
                        // Set item click listener after data is fetched
                        lvGames.setOnItemClickListener((parent, view, position, id) -> {
                            Log.d("ItemClick", "Item clicked at position: " + position);  // Log click event
                            Game clickedGame = gameList.get(position);
                            if (clickedGame.isActive()) {
                                Intent startPlaying = new Intent(context, QuestionActivity.class);
                                startPlaying.putExtra("userId", userId);
                                startPlaying.putExtra("gameId", clickedGame.getId());
                                startActivity(startPlaying);
                            }
                        });
                    } else {
                        Log.e("DataBase", "Error", task.getException());
                    }
                });
    }
}