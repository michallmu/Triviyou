package triviyou.michal.com;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.LinkedList;
import java.util.List;
import androidx.activity.EdgeToEdge;
import triviyou.michal.com.entities.Question;
import triviyou.michal.com.entities.UserGameHistory;

public class QuestionActivity extends AppCompatActivity {
    Context context;
    ImageButton imgBback6;
    private FirebaseFirestore db;
    Intent goGames, inputIntent, goSummary;
    private CountDownTimer countdownTimer;
    String userId;
    Helper helper = new Helper();
    ImageView imgQuestion;
    WebView videoQuestion;
    TextView tvShowLevel, tvQuestionText, tvQuestionInfo, tvTimerQuestion, tvPodium;
    RadioGroup answersGroup;
    RadioButton rbAnswer1, rbAnswer2, rbAnswer3, rbAnswer4;
    Button bSubmit;
    int userLevel, gameId, failuresNumber, selectedAnswer, checkedId;
    private List<Question> questionList = new LinkedList<>(); // initialize as an empty list
    Boolean isTop = false;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ui issues
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);
        context = QuestionActivity.this;
        goGames = new Intent(context, GamesActivity.class);
        imgBback6 = findViewById(R.id.imgbBack6);
        inputIntent = getIntent();
        goSummary = new Intent(context, SummaryActivity.class);
        gameId = inputIntent.getIntExtra("gameId", 1);
        tvShowLevel = findViewById(R.id.tvShowLevel);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        tvQuestionInfo = findViewById(R.id.tvQuestionInfo);
        tvTimerQuestion = findViewById(R.id.tvTimerQuestion);
        tvPodium = findViewById(R.id.tvPodium);
        rbAnswer1 = findViewById(R.id.rbAnswer1);
        rbAnswer2 = findViewById(R.id.rbAnswer2);
        rbAnswer3 = findViewById(R.id.rbAnswer3);
        rbAnswer4 = findViewById(R.id.rbAnswer4);
        bSubmit = findViewById(R.id.bSubmit);
        answersGroup = findViewById(R.id.answersGroup);

        imgQuestion = findViewById(R.id.imgQuestion);
        videoQuestion = findViewById(R.id.videoQuestion);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        userId = auth.getCurrentUser().getUid();


        //init db (firestore)
        db = FirebaseFirestore.getInstance();

        // get user current level from Firestore
        getUserHistoryAndQuestions(userId, gameId);

        // listen for back button click (currently commented out)
        imgBback6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goGames.putExtra("userId", userId);
                startActivity(goGames);
            }
        });

        // submit button action (currently commented out)
        bSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Helper.isInternetAvailable(context)) {
                    helper.toasting(context, getString(R.string.noInternetConnection));
                    return;
                }

                onSubmitClicked();            }
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


    private void showElements() {
        rbAnswer1.setVisibility(View.VISIBLE);
        rbAnswer2.setVisibility(View.VISIBLE);
        rbAnswer3.setVisibility(View.VISIBLE);
        rbAnswer4.setVisibility(View.VISIBLE);
        bSubmit.setVisibility(View.VISIBLE);
    }

    private void onSubmitClicked() {
        if (!questionList.isEmpty()) {
            // check if the selected answer is correct
            int selectAnswer = 0;
            selectAnswer = getSelectAnswer(selectAnswer);
            if(selectAnswer == 0)
            {
                helper.toasting(context, getString(R.string.noAnswerSelected));
            }
            else if (selectAnswer != questionList.get(0).correctAnswer) {
                failuresNumber++;
                helper.toasting(context, getString(R.string.wrongAnswerTryAgain));
            }
            else {
                // answer is correct
                // so first - remove the question from the list

                if (countdownTimer != null) {
                    countdownTimer.cancel();
                }

                userLevel = questionList.get(0).getLevel();
                questionList.remove(0);
                if((questionList.size()>0) && (questionList.get(0).getLevel() == (userLevel + 1))) {
                    userLevel = questionList.get(0).getLevel();
                }

                // update user history  in DB
                updateUserGameHistoryInDB(userLevel, failuresNumber);

                if(questionList.isEmpty()) {
                    moveToSummaryActivity();
                }
                else {
                    // show next question after submit
                    showSingleQuestion(questionList.get(0)); // get next question
                }
            }
        }
    }

    // method to continue with the rest of the logic after fetching the user history data
    private void continueAfterGettingUserHistory(UserGameHistory userGameHistory) {
        if (userGameHistory == null) {
            userLevel = 1;
            failuresNumber = 0;

            }
        else {
            userLevel = userGameHistory.getCurrentLevel();
            failuresNumber = userGameHistory.getFailuresNumber();
            if (userGameHistory.isFinished()) {
                moveToSummaryActivity();

            }
        }

        // get the questions from Firestore
        getQuestionsFromDB(gameId, userLevel);


    }

    private void getUserHistoryAndQuestions(String userId, int gameId) {
        String documentId = userId + "_" + gameId; // combine userId and gameId to form the document ID
        DocumentReference docRef = db.collection("userGameHistory").document(documentId);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserGameHistory userGameHistory =null;

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // document exists, process it
                    try {
                        userGameHistory = documentSnapshot.toObject(UserGameHistory.class);

                    }
                    catch (Exception e) {
                        Log.e("Error casting",e.getMessage());
                    }

                } else {
                    // document doesn't exist, handle accordingly (return null)
                    Log.d("UserHistory", "No history found for this user and game.");
                }
                continueAfterGettingUserHistory(userGameHistory);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // handle any errors that occur during the fetch
                Log.e("UserHistory", "Error fetching data: " + e.getMessage());

                // return null - maybe the document not found
                continueAfterGettingUserHistory(null);
            }
        });
    }

    private void isUserTopFromDB(String userId, int gameId) {


        db.collection("userGameHistory")
                .whereEqualTo("gameId", gameId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    int maxLevel = 0;
                    for (DocumentSnapshot doc : snapshot) {
                        Long levelLong = doc.getLong("currentLevel");
                        if (levelLong != null) {
                            maxLevel = Math.max(maxLevel, levelLong.intValue());
                        }
                    }
                    isTop = (userLevel == maxLevel);

                    //check if user on pudiom (3 first places)
                    if(isTop)
                    {
                        tvPodium.setVisibility(View.VISIBLE);
                        tvPodium.setText(getString(R.string.podiumText));
                    }
                    else
                        tvPodium.setVisibility(View.INVISIBLE);
                });

    }

    private void updateUserGameHistoryInDB(int userLevel, int failuresNumber) {

        UserGameHistory userGameHistory = new UserGameHistory(gameId,userId,questionList.isEmpty(),userLevel, failuresNumber);
        String documentId = userId + "_" + gameId;

        // save to Firestore in "userGameHistory" collection
        db.collection("userGameHistory").document(documentId)
                .set(userGameHistory)
                .addOnSuccessListener(aVoid -> {

                    isUserTopFromDB( userId,  gameId);
                    System.out.println("UserGameHistory saved successfully!"); }
                )
                .addOnFailureListener(e ->
                        System.err.println("Error saving UserGameHistory: " + e.getMessage())
                );

    }

    // fetching document snapshot based on userId and gameId
    private void getQuestionsFromDB(int gameId, int userLevel) {
        // show loading indicator while fetching data (optional)
        try {
            db.collection("questions")
                    .whereEqualTo("gameId", gameId)
                    .whereGreaterThanOrEqualTo("level", userLevel)
                    .orderBy("level", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        try{
                            // hide loading indicator after the task completes
                            if (task.isSuccessful()) {
                                questionList.clear(); // clear old data

                                // the task return document (json) for each question in FB
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    // convert document to Question Class
                                    Question question = document.toObject(Question.class);
                                    questionList.add(question);
                                }

                                // ensure we have data before calling initQuestion
                                if (!questionList.isEmpty()) {

                                    // show the first question from the list
                                    showSingleQuestion(questionList.get(0)); // show the first question
                                } else {
                                    helper.toasting(context, getString(R.string.noQuestionFound));
                                }

                                Log.d("QuestionList", "Questions fetched: " + questionList.size());
                            } else {

                                Log.e("FirestoreError", "Error fetching questions: ", task.getException());
                                helper.toasting(context, getString(R.string.errorFetchingData));
                            }
                        } catch (Exception e) {
                            Log.e("FirestoreException", "Exception in Firestore callback: ", e);
                            helper.toasting(context, getString(R.string.unexpectedErrorOccurred));
                        }


                    });


        }
        catch (Exception e) {
            Log.e("DatabaseException", "Exception in getQuestionsFromDB: ", e);
            helper.toasting(context, getString(R.string.unexpectedErrorOccurred));
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showSingleQuestion(Question question) {
        // just now, we visible the elements
        imgQuestion.setVisibility(View.GONE);
        videoQuestion.setVisibility(View.GONE);

        videoQuestion.loadUrl("about:blank");
        videoQuestion.clearHistory();
        videoQuestion.clearCache(true);
        videoQuestion.reload();


        if (!Helper.isInternetAvailable(context)) {
            helper.toasting(context, getString(R.string.noInternetConnection));
            return; }
        else
            showElements();

        String qt = question.questionText;
        String qtInfo = getString(R.string.questionNumber) + question.id + "\n" + getString(R.string.correctAnswer) + question.correctAnswer + "\n" +getString(R.string.level) + question.level;
        tvQuestionText.setText(qt);
        tvQuestionInfo.setText(qtInfo);
        answersGroup.clearCheck();
        rbAnswer1.setText(question.answer1);
        rbAnswer1.setChecked(false);
        rbAnswer2.setText(question.answer2);
        rbAnswer2.setChecked(false);
        rbAnswer3.setText(question.answer3);
        rbAnswer3.setChecked(false);
        rbAnswer4.setText(question.answer4);
        rbAnswer4.setChecked(false);

        if ("video".equalsIgnoreCase(question.getQuestionType())) {
            tvTimerQuestion.setVisibility(View.GONE);
        } else {
            startTimer(userLevel);
            tvTimerQuestion.setVisibility(View.VISIBLE);
        }

        String statusMessage = getString(R.string.statusMessage, userLevel, questionList.get(questionList.size() - 1).getLevel());
        tvShowLevel.setText(statusMessage);

        switch (question.getQuestionType().toLowerCase()) {
            case "video":
                videoQuestion.setVisibility(View.VISIBLE);
                videoQuestion.setWebViewClient(new WebViewClient());
                String videoUrl = question.questionUrl;
                videoQuestion.getSettings().setJavaScriptEnabled(true);
                videoQuestion.loadUrl(videoUrl);

                break;

            case "image":
                imgQuestion.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(question.getQuestionUrl())
                        .into(imgQuestion);
                break;
            default:
                break;
        }
    }

    private void startTimer(int userLevel) {
        // cancel any existing timer if it's already running
        if (countdownTimer != null ) {
            countdownTimer.cancel();
        }


        countdownTimer = new CountDownTimer((userLevel * 1000) + 8000, 1000) { // Milliseconds
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished / 1000 > 0)
                    tvTimerQuestion.setText(getString(R.string.areLeft) + (millisUntilFinished / 1000) + getString(R.string.seconds));
                else
                    tvTimerQuestion.setText(getString(R.string.timesOut));
            }

            @Override
            public void onFinish() {
                // for the timer, do nothing
            }
        };

        countdownTimer.start();
    }

    private boolean isAnswerSubmitted() {
        return rbAnswer1.isChecked() || rbAnswer2.isChecked() || rbAnswer3.isChecked() || rbAnswer4.isChecked();
    }


    @Override
    public void onBackPressed() {
        if (videoQuestion.canGoBack()) {
            videoQuestion.goBack();
        } else {
            super.onBackPressed();
        }
    }
    private int getSelectAnswer(int selectAnswer) {
        if(rbAnswer1.isChecked())
        {
            selectAnswer = 1;
        }
        if(rbAnswer2.isChecked())
        {
            selectAnswer = 2;
        }
        if(rbAnswer3.isChecked())
        {
            selectAnswer = 3;
        }
        if(rbAnswer4.isChecked())
        {
            selectAnswer = 4;
        }
        return selectAnswer;
    }

    private void moveToSummaryActivity() {
        goSummary.putExtra("gameId", gameId);
        startActivity(goSummary);

    }

}

