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


        userId = inputIntent.getStringExtra("userId");
        //email = inputIntent.getStringExtra(email);

        imgBback3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goLogin);
            }
        });

        imageAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goProfile);
        //        goProfile.putExtra("email", email);
            }
        });

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

       gameList =  new ArrayList<Game>();
       adapter = new GameAdapter(this, gameList);
        // Set adapter
        lvGames.setAdapter(adapter);
        getGamesFromDB();



        // Item click listener

        lvGames.setOnItemClickListener((parent, view, position, id) -> {
            Game clickedGame = gameList.get(position);
            if (clickedGame.isActive()) {
                // Logic for when a game is clicked
                Intent goPlaying = new Intent(context, PlayingActivity.class);

                /*goPlaying.putExtra("gameTitle", clickedGame.getTitle());
                goPlaying.putExtra("gameDescription", clickedGame.getDescription());
                goPlaying.putExtra("gameUrl", clickedGame.getImageUrl());*/
                goPlaying.putExtra("userId", userId);
                goPlaying.putExtra("gameId", clickedGame.getId());
                startActivity(goPlaying);

            }
        });
    }

    private void getGamesFromDB() {


//        gameList.add(new Game("1", "סרטים", "גלה את העולם המרתק של הסרטים","hello123.hpg", true));
//        gameList.add(new Game("2", "מדינות", "חידון מדינות","hello123.hpg", true));
//        gameList.add(new Game("3", "מאכלים", "חידון מאכלים","hello123.hpg", true));

        db.collection("games")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Game game = document.toObject(Game.class);
                            gameList.add(game);
                        }
                        adapter.notifyDataSetChanged(); // Update ListView
                    } else {
                        Log.e("DataBase", "Error", task.getException());
                    }
                });
    }
}
