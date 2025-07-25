package triviyou.michal.com;
import static androidx.core.content.ContextCompat.getSystemService;
import static triviyou.michal.com.R.*;
import android.Manifest;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.List;
import triviyou.michal.com.adapters.GameAdapter;
import triviyou.michal.com.entities.Game;
import triviyou.michal.com.Helper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
public class GamesActivity extends AppCompatActivity {

    private ListView lvGames;
    private FirebaseFirestore db;
    private List<Game> gameList;
    GameAdapter adapter;
    Intent goUserGuide, goProfile, inputIntent;
    Context context;
    Helper helper  = new Helper();
    BottomNavigationView bottomNavigationView;
    String email, userId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        bottomNavigationView = findViewById(id.bottomNavigationView);
        context = GamesActivity.this;
        inputIntent = getIntent();
        goUserGuide = new Intent(context, UserGuide.class);
        goProfile = new Intent(context, ProfileActivity.class);
        lvGames = findViewById(R.id.lvGames);

        lvGames.setClickable(true);

        userId = inputIntent.getStringExtra("userId");

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_profile) {
                    startActivity(goProfile);
                    return true; }
                else if (item.getItemId() == R.id.nav_userGuide) {
                    startActivity(goUserGuide);
                    return true; }
                else {
                    return false;
                }
            }
        });

        db = FirebaseFirestore.getInstance(); // initialize Firestore

        // setup game list and adapter
        gameList = new ArrayList<>();
        adapter = new GameAdapter(this, gameList);
        lvGames.setAdapter(adapter); // set adapter to ListView

        getGamesFromDB(); // load games from Firestore
        checkUnfinishedGamesForUser();
    }

    // check if user has unfinished games
    private void checkUnfinishedGamesForUser() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = auth.getCurrentUser().getUid();

        db.collection("userGameHistory")
                .whereEqualTo("userId", userId) // filter by userId
                .whereEqualTo("finished", false) // get only unfinished games
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // queryDocumentSnapshots, means , that user have at least one history doc in db.
                    // otherwise, we not popup him
                    if (!queryDocumentSnapshots.isEmpty()) {
                        scheduleNotification(queryDocumentSnapshots.size()); // schedule reminder
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error fetching unfinished games", e));
    }

    // schedule a notification after 3 seconds
    private void scheduleNotification(int countHistories) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        intent.putExtra("notification_type", "notificationShowHistories");
        intent.putExtra("countHistories", countHistories); // Pass message

        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            long triggerTime = System.currentTimeMillis() + 1500; //(1.5 seconds)

            if (alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent); // schedule alarm
            }
        }

        catch (Exception e) {
            Log.e("error loading size", "failed");
        }
    }

    // load games from Firestore and set in list
    private void getGamesFromDB() {
        db.collection("games")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        gameList.clear(); // clear old data

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Game game = document.toObject(Game.class); // convert to Game

                            //this handle the boolean from firestore object
                            boolean isActive = document.getBoolean("isActive"); // get 'isActive'
                            game.setActive(isActive);

                            gameList.add(game);
                        }
                        adapter.notifyDataSetChanged(); // update ListView
                        // set item click listener after data is fetched
                        lvGames.setOnItemClickListener((parent, view, position, id) -> {
                            Log.d("ItemClick", "Item clicked at position: " + position);  // log click event
                            Game clickedGame = gameList.get(position);
                            if (clickedGame.isActive()) {
                                Intent startPlaying = new Intent(context, QuestionActivity.class);
                                startPlaying.putExtra("userId", userId);
                                startPlaying.putExtra("gameId", clickedGame.getId());
                                if (!Helper.isInternetAvailable(context)) {
                                    helper.toasting(context,getString(string.noInternetConnection));
                                    return;
                                }

                                startActivity(startPlaying);
                            }
                        });
                    } else {
                        Log.e("DataBase", "Error", task.getException());
                    }
                });
    }
}