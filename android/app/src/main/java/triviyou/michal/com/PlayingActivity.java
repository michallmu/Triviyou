package triviyou.michal.com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PlayingActivity extends AppCompatActivity {

    Context context;
    ImageView imgGame;
    TextView tvTitle, tvDescription, tvStatus, tvDetails;
    Button bPlay;
    ImageButton imgBback5;
    Intent goGames, goQuestion;
    String userId, gameId;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);

        context = PlayingActivity.this;
        imgGame = findViewById(R.id.imgGame);
        imgBback5 = findViewById(R.id.imgBback5);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvStatus = findViewById(R.id.tvStatus);
        tvDetails = findViewById(R.id.tvDetails);

        bPlay = findViewById(R.id.bPlay);

        Intent inputIntent = getIntent();
        goGames = new Intent(context, GamesActivity.class);
        goQuestion = new Intent(context, QuestionActivity.class);

        String gameId = inputIntent.getStringExtra("gameId");
        String userId = inputIntent.getStringExtra("userId");

        //todo  -  get from games collection
        String gameTitle = "this is the title";// inputIntent.getStringExtra("gameTitle");
        String gameDescription = "this is the description"; ///inputIntent.getStringExtra("gameDescription");
        String gameImageUrl = "";  ////inputIntent.getStringExtra("gameImageUrl");

        tvTitle.setText(gameTitle);
        tvDescription.setText(gameDescription);

        imgBback5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goGames);
            }
        });

        //code that check questions  userGames  table -  userid+ gameId ==>   currentLevel
        //if none -  level is 0
        //else    show message
        int currentLevel = 1;
        if(currentLevel == 1) {
            tvStatus.setText("השלב שלך: " + currentLevel);
        }


        bPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goQuestion.putExtra("gameId", gameId);
                goQuestion.putExtra("userId", userId);
                startActivity(goQuestion);
            }
        });



        // Load image using Glide
        Glide.with(this)
                .load(gameImageUrl) // URL of the game's image
                //.placeholder(R.drawable.placeholder) // Default image while loading
                //.error(R.drawable.error_image) // Fallback image
                .into(imgGame);

        // Set click listener for the Play button
       /* btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect to another activity or start the game logic
                Intent playIntent = new Intent(Playing.this, PlayingActivity.class);
                startActivity(playIntent);
            }
        });*/
    }
}
