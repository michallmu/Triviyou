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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import androidx.activity.EdgeToEdge;

import triviyou.michal.com.entities.Question;

public class QuestionActivity extends AppCompatActivity {
    Context context;
    ImageButton imgBback6;
    Intent goGames, inputIntent;
    String userId, gameId;
    TextView tvShowLevel, tvQuestionText;
    RadioGroup answersGroup;
    RadioButton rbAnswer1, rbAnswer2, rbAnswer3, rbAnswer4;
    Button bNextQuestion;
    int currentLevel, maxLevel, selectedAnswer, checkedId;

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
        gameId = inputIntent.getStringExtra("gameId");
        tvShowLevel = findViewById(R.id.tvShowLevel);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        rbAnswer1 = findViewById(R.id.rbAnswer1);
        rbAnswer2 = findViewById(R.id.rbAnswer2);
        rbAnswer3 = findViewById(R.id.rbAnswer3);
        rbAnswer4 = findViewById(R.id.rbAnswer4);
        bNextQuestion = findViewById(R.id.bNextQuestion);
        answersGroup = findViewById(R.id.answersGroup);


        //goto fireabse ask 2 queries
        //1. get current level of userId in gameId
        // 2. get list of all questions in gameId
        // set all data from first questionh
        currentLevel = 3;
        maxLevel = 10;
        String statusMessage = getString(R.string.statusMessage, currentLevel, maxLevel);
        tvShowLevel.setText(statusMessage);

        //get the questions left in game
        LinkedList<Question> questions = getQuestion(gameId);

        initQuestion(questions.getFirst());

        //return to  game screen
        imgBback6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goGames.putExtra("userId", userId);
                startActivity(goGames);
            }
        });


        bNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!questions.isEmpty()) { // Remove the first question from the list and get the next one
                    questions.removeFirst();
                    if (!questions.isEmpty()) {
                        initQuestion(questions.getFirst());
                    }
                }
            }
        });

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
            bNextQuestion.setVisibility(View.VISIBLE);

            // בדיקת אם התשובה שנבחרה נכונה
            if (selectedAnswer != question.correctAnswer) {
                // הצגת טוסט אם התשובה לא נכונה
                Toast.makeText(this, getString(R.string.wrongAnswerTryAgain), Toast.LENGTH_SHORT).show();
            }
        });

        // נסתר אם לא נבחרה תשובה
        bNextQuestion.setVisibility(View.GONE);
    }

    private LinkedList<Question> getQuestion(String gameId) {

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

    // i use this function to load question from local json, before i  have the questions inside firestore
    private  String loadJSONFromAsset(Context context, int resourceId) {
            StringBuilder json = new StringBuilder();
            try {
                // Open the raw resource file
                InputStream inputStream = context.getResources().openRawResource(resourceId);
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }

                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null; // Return null if there's an error
            }
            return json.toString();
        }


}