package triviyou.michal.com;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import triviyou.michal.com.entities.UserGameHistory;

public class SummaryActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Context context;
    Intent goGames, inputIntent, startGame;
    Button bBackGamesList, bStartGame;
    CountDownTimer countDownTimer;
    TextView tvTimer, tvResults;
    String userId;
    Helper helper = new Helper();
    boolean isButtonClicked = false;
    int gameId, summaryDuration = 15000, grade;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_summary);

        context = SummaryActivity.this;
        goGames = new Intent(context, GamesActivity.class);
        startGame = new Intent(context, QuestionActivity.class);
        bBackGamesList = findViewById(R.id.bBackGamesList);
        inputIntent = getIntent();
        tvTimer = findViewById(R.id.tvTimer);
        tvResults = findViewById(R.id.tvResults);
        bStartGame = findViewById(R.id.bStartGame);

        grade = inputIntent.getIntExtra("grade",grade);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();

        gameId = inputIntent.getIntExtra("gameId", gameId);


        getUserHistory(userId, gameId);

        countDownTimer = new CountDownTimer(summaryDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvTimer.setText(getString(R.string.automaticTransition) + (millisUntilFinished / 1000) + " שניות");
            }

            @Override
            public void onFinish() {
                if (!isButtonClicked) {
                    startActivity(goGames);
                    finish();
                }
            }
        }.start();


        bStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUserGameHistory(userId, gameId);
            }
        });

        bBackGamesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isButtonClicked = true; // prevent auto navigation
                countDownTimer.cancel();
                startActivity(goGames);
                finish();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        Helper.onActivityStarted(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Helper.onActivityStopped(this);
    }


    private void deleteUserGameHistory(String userId, int gameId) {
        String documentId = userId + "_" + gameId; // the document id

        db.collection("userGameHistory").document(documentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    startGame.putExtra("gameId", gameId);
                    startActivity(startGame);

                    Log.d("Firestore", "Document successfully deleted!");
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error deleting document", e);
                    helper.toasting(context, getString(R.string.errorDeletingGame));
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }


    private void getUserHistory(String userId, int gameId) {
        String documentId = userId + "_" + gameId; // combine userId and gameId to form the document ID
        DocumentReference docRef = db.collection("userGameHistory").document(documentId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // document exists, process it
                    try {
                        UserGameHistory userGameHistory = documentSnapshot.toObject(UserGameHistory.class);
                        if (userGameHistory.getFailuresNumber() > 0)
                            tvResults.setText(getString(R.string.numberOfFailures) + userGameHistory.getFailuresNumber());
                    }
                    catch (Exception e) {
                        Log.e("Error casting",e.getMessage());
                    }

                } else {
                    // document doesn't exist, handle accordingly (return null)
                    Log.d("UserHistory", "No history found for this user and game.");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // handle any errors that occur during the fetch
                Log.e("UserHistory", "Error fetching data: " + e.getMessage());}
        });
    }


}
