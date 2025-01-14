package triviyou.michal.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.LinkedList;
import java.util.List;
import androidx.activity.EdgeToEdge;

import triviyou.michal.com.entities.Question;

public class QuestionActivity extends AppCompatActivity {
    Context context;
    ImageButton imgBback6;
    private FirebaseFirestore db;
    Intent goGames, inputIntent;
    String userId;
    int gameId;
    TextView tvShowLevel, tvQuestionText;
    RadioGroup answersGroup;
    RadioButton rbAnswer1, rbAnswer2, rbAnswer3, rbAnswer4;
    Button bSubmit;
    int currentLevel, maxLevel, selectedAnswer, checkedId;
    private List<Question> questionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);

        context = QuestionActivity.this;
        goGames = new Intent(context, GamesActivity.class);
        imgBback6 = findViewById(R.id.imgbBack6);
        inputIntent = getIntent();
        userId = inputIntent.getStringExtra("userId");
        gameId = inputIntent.getIntExtra("gameId",1);
        tvShowLevel = findViewById(R.id.tvShowLevel);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        rbAnswer1 = findViewById(R.id.rbAnswer1);
        rbAnswer2 = findViewById(R.id.rbAnswer2);
        rbAnswer3 = findViewById(R.id.rbAnswer3);
        rbAnswer4 = findViewById(R.id.rbAnswer4);
        bSubmit = findViewById(R.id.bSubmit);
        answersGroup = findViewById(R.id.answersGroup);
        db = FirebaseFirestore.getInstance();


        //goto fireabse ask 2 queries
        //1. get current level of userId in gameId
        // 2. get list of all questions in gameId
        // set all data from first questionh
        currentLevel = 3;
        maxLevel = 10;
        String statusMessage = getString(R.string.statusMessage, currentLevel, maxLevel);
        tvShowLevel.setText(statusMessage);

        //get the questions left in game
         getQuestionsFromDB(gameId);

        initQuestion(questionList.get(0));

        //return to  game screen
        imgBback6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goGames.putExtra("userId", userId);
                startActivity(goGames);
            }
        });


//        bSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!questions.isEmpty()) { // Remove the first question from the list and get the next one
//                    questions.removeFirst();
//                    if (!questions.isEmpty()) {
//                        initQuestion(questions.getFirst());
//                    }
//                }
//            }
//        });

    }
    private void initQuestion(Question question) {
        tvQuestionText.setText(question.questionText);
        rbAnswer1.setText(question.answer1);
        rbAnswer2.setText(question.answer2);
        rbAnswer3.setText(question.answer3);
        rbAnswer4.setText(question.answer4);
        // הגדרת מאזין לבחירת תשובה
        answersGroup.setOnCheckedChangeListener((group, checkedId) -> {
            // הגדרת המשתנה selectedAnswer לפי ה-checkedId
            if (checkedId == R.id.rbAnswer1) {
                selectedAnswer = 1;
            } else if (checkedId == R.id.rbAnswer2) {
                selectedAnswer = 2;
            } else if (checkedId == R.id.rbAnswer3) {
                selectedAnswer = 3;
            } else if (checkedId == R.id.rbAnswer4) {
                selectedAnswer = 4;
            }

            // הצגת כפתור "שאלה הבאה" לאחר בחירת תשובה
            bSubmit.setVisibility(View.VISIBLE);

            // בדיקת אם התשובה שנבחרה נכונה
            if (selectedAnswer != question.correctAnswer) {
                // הצגת טוסט אם התשובה לא נכונה
                Toast.makeText(this, getString(R.string.wrongAnswerTryAgain), Toast.LENGTH_SHORT).show();
            }
        });

        // נסתר אם לא נבחרה תשובה
        bSubmit.setVisibility(View.GONE);
    }


    private void getQuestionsFromDB(int gameId) {
        db.collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        questionList.clear(); // Clear old data
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Question question = document.toObject(Question.class);
                            questionList.add(question);
                        }

                        Log.d("QuestionList", "Questions fetched: " + questionList.size());

                    } else {
                        Log.e("DataBase", "Error", task.getException());
                    }
                });
    }

    /* i keep this code - that i use when start programing, before i have the quesoins inside firestore

    private LinkedList<Question> getQuestions(String gameId) {

        // Load and deserialize JSON from assets
        LinkedList<Question> questions = new LinkedList<Question>();
        try {
            // Read the JSON file from res/raw
            String json = loadJSONFromAsset(this, R.raw.questions); // Replace 'data' with your JSON file name (without extension)
            // Create Gson instance
            Gson gson = new Gson();
            // Define the type for the array of questions
            Type questionListType = new TypeToken<List<Question>>() {
            }.getType();
            // Deserialize JSON into a list of Question objects
            ArrayList<Question> tempList = gson.fromJson(json, questionListType);
            questions = new LinkedList<>(tempList);
        } catch (Exception e) {
            Log.e("QuestionActivity", "Error reading the JSON file", e);
        }
        return questions;
    }
     */


}