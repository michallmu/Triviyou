package triviyou.michal.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    String userId;
    ImageView imgQuestion;
    VideoView videoQuestion;
    int gameId;
    TextView tvShowLevel, tvQuestionText;
    RadioGroup answersGroup;
    RadioButton rbAnswer1, rbAnswer2, rbAnswer3, rbAnswer4;
    Button bSubmit;
    int userLevel, selectedAnswer, checkedId;
    private List<Question> questionList = new LinkedList<>(); // Initialize as an empty list
    private ProgressBar progressBar;

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
        userId = inputIntent.getStringExtra("userId");
        gameId = inputIntent.getIntExtra("gameId", 1);
        tvShowLevel = findViewById(R.id.tvShowLevel);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        rbAnswer1 = findViewById(R.id.rbAnswer1);
        rbAnswer2 = findViewById(R.id.rbAnswer2);
        rbAnswer3 = findViewById(R.id.rbAnswer3);
        rbAnswer4 = findViewById(R.id.rbAnswer4);
        bSubmit = findViewById(R.id.bSubmit);
        answersGroup = findViewById(R.id.answersGroup);
        progressBar = findViewById(R.id.progressBar);
        imgQuestion = findViewById(R.id.imgQuestion);
        videoQuestion = findViewById(R.id.videoQuestion);

        //init db (firestore)
        db = FirebaseFirestore.getInstance();

        // Get user current level from Firestore
       getUserHistoryAndQuestions(userId, gameId);




        // Listen for back button click (currently commented out)
        imgBback6.setOnClickListener(v -> {
            goGames.putExtra("userId", userId);
            startActivity(goGames);
        });
        // Submit button action (currently commented out)
        bSubmit.setOnClickListener(v -> {
            onSubmitClicked();
        });
    }

    private void onSubmitClicked() {
        if (!questionList.isEmpty()) {
            // Check if the selected answer is correct
            int selectAnswer = 0;
            selectAnswer = getSelectAnswer(selectAnswer);
            if(selectAnswer == 0)
            {
                Toast.makeText(this,"no answer selected", Toast.LENGTH_SHORT).show();
            }
            else if (selectAnswer != questionList.get(0).correctAnswer) {
                Toast.makeText(this, getString(R.string.wrongAnswerTryAgain), Toast.LENGTH_SHORT).show();
            }
            else {
                //answer is correct !!!
                // first- remove the question from the list
                userLevel = questionList.get(0).getLevel();
                questionList.remove(0);
                if((questionList.size()>0) && (questionList.get(0).getLevel() == (userLevel + 1))) {
                    userLevel = questionList.get(0).getLevel();
                }
                    //update user history  in DB
                updateUserGameHistoryLevelInDB(userLevel);


                if(questionList.isEmpty()) {
                    moveToSummaryActivity();
                }
                else {
                    //show next question after submit
                    showSingleQuestion(questionList.get(0)); // Get next question
                }
            }
        }
    }

    // Method to continue with the rest of the logic after fetching the user history data
    private void continueAfterGettingUserHistory(UserGameHistory userGameHistory) {
        if (userGameHistory == null) {
            userLevel = 1;
        }
        else {
            userLevel = userGameHistory.getCurrentLevel();
            if (userGameHistory.isFinished()) {
                moveToSummaryActivity();
            }
        }


        // get the questions from Firestore
        getQuestionsFromDB(gameId, userLevel);


    }
    private void getUserHistoryAndQuestions(String userId, int gameId) {
        String documentId = userId + "_" + gameId; // Combine userId and gameId to form the document ID
        DocumentReference docRef = db.collection("userGameHistory").document(documentId);

        // Asynchronously get the document
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserGameHistory userGameHistory =null;

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    // Document exists, process it
                    try {
                        userGameHistory = documentSnapshot.toObject(UserGameHistory.class);

                    }
                    catch (Exception e) {
                        Log.e("Error casting",e.getMessage());
                    }

                } else {
                    // Document doesn't exist, handle accordingly (return null)
                    Log.d("UserHistory", "No history found for this user and game.");
                }
                continueAfterGettingUserHistory(userGameHistory);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                // Handle any errors that occur during the fetch
                Log.e("UserHistory", "Error fetching data: " + e.getMessage());

                // return null - docment  maybe not found
                continueAfterGettingUserHistory(null);
            }
        });
    }

    private void updateUserGameHistoryLevelInDB(int userLevel) {
        UserGameHistory userGameHistory = new UserGameHistory(gameId,userId,questionList.isEmpty(),userLevel);
        String documentId = userId + "_" + gameId;

        // Save to Firestore in "userGameHistory" collection
        db.collection("userGameHistory").document(documentId)
                .set(userGameHistory)
                .addOnSuccessListener(aVoid ->
                        System.out.println("UserGameHistory saved successfully!")
                )
                .addOnFailureListener(e ->
                        System.err.println("Error saving UserGameHistory: " + e.getMessage())
                );

    }

    // Fetching Document Snapshot based on userId and gameId
    private void getQuestionsFromDB(int gameId, int userLevel) {
        // Show loading indicator while fetching data (optional)
        try {
            progressBar.setVisibility(View.VISIBLE); // Assuming you have a progressBar

            db.collection("questions")
                    .whereEqualTo("gameId", gameId)
                    .whereGreaterThanOrEqualTo("level", userLevel)
                    .orderBy("level", Query.Direction.ASCENDING)
                    .get()
                    .addOnCompleteListener(task -> {
                        try{
                            // Hide loading indicator after the task completes
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                questionList.clear(); // Clear old data
                                // the task return document (json) for each question in FB
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    //convert document to Question Class
                                    Question question = document.toObject(Question.class);
                                    questionList.add(question);
                                }

                                // Ensure we have data before calling initQuestion
                                if (!questionList.isEmpty()) {
                                    progressBar.setVisibility(View.GONE);
                                    //show the first question from the list
                                    showSingleQuestion(questionList.get(0)); // Show the first question



                                } else {
                                    Toast.makeText(QuestionActivity.this, "No questions found.", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                                Log.d("QuestionList", "Questions fetched: " + questionList.size());
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Log.e("FirestoreError", "Error fetching questions: ", task.getException());
                                Toast.makeText(QuestionActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("FirestoreException", "Exception in Firestore callback: ", e);
                            Toast.makeText(QuestionActivity.this, "Unexpected error occurred", Toast.LENGTH_SHORT).show();
                        }


                    });


        }
        catch (Exception e) {
            Log.e("DatabaseException", "Exception in getQuestionsFromDB: ", e);
            Toast.makeText(QuestionActivity.this, "Unexpected error occurred while fetching questions", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showSingleQuestion(Question question) {
        String qt = question.questionText + question.id + ",Correct ans:" + question.correctAnswer + " level: " + question.level;
        tvQuestionText.setText(qt);
        answersGroup.clearCheck();
        rbAnswer1.setText(question.answer1);
        rbAnswer1.setChecked(false);
        rbAnswer2.setText(question.answer2);
        rbAnswer2.setChecked(false);
        rbAnswer3.setText(question.answer3);
        rbAnswer3.setChecked(false);
        rbAnswer4.setText(question.answer4);
        rbAnswer4.setChecked(false);

        imgQuestion.setVisibility(View.GONE);
        videoQuestion.setVisibility(View.GONE);

        String statusMessage = getString(R.string.statusMessage, userLevel, questionList.get(questionList.size() - 1).getLevel());
        tvShowLevel.setText(statusMessage);

        switch (question.getQuestionType().toLowerCase()) {
            case "video":
                videoQuestion.setVisibility(View.VISIBLE);
                break;
            case "image":
                //https://drive.google.com/uc?id=1fTQYtxXkda0j_kAKnyBj5_xHJFFrAs8-
                imgQuestion.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(question.getQuestionUrl())
                        //.placeholder(R.drawable.placeholder) // Show a placeholder while loading
                        //.error(R.drawable.error_image) // Show an error image if the URL fails
                        .into(imgQuestion);
                break;
            default:
                break;

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

    //todo  -   move to  Sumamry activity
    private void moveToSummaryActivity() {
        goSummary.putExtra("gameId", gameId);
        startActivity(goSummary);

    }



}
