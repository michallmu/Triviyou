package triviyou.michal.com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import triviyou.michal.com.adapters.GameAdapter;
import triviyou.michal.com.entities.Game;
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
    }

    private void getGamesFromDB() {
        db.collection("games")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        gameList.clear(); // Clear old data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Game game = document.toObject(Game.class);
                            boolean isActive = document.getBoolean("isActive");
                            game.setActive(isActive);
                            gameList.add(game);
                        }
                        Log.d("GameList", "Games fetched: " + gameList.size());  // Log the size of the list
                        adapter.notifyDataSetChanged(); // Update ListView

                        // Set item click listener after data is fetched
                        lvGames.setOnItemClickListener((parent, view, position, id) -> {
                            Log.d("ItemClick", "Item clicked at position: " + position);  // Log click event
                            Game clickedGame = gameList.get(position);
                            if (clickedGame.isActive()) {
                                Intent goPlaying = new Intent(context, PlayingActivity.class);
                                goPlaying.putExtra("userId", userId);
                                goPlaying.putExtra("gameId", clickedGame.getId());
                                startActivity(goPlaying);
                            }
                        });
                    } else {
                        Log.e("DataBase", "Error", task.getException());
                    }
                });
    }
}
