package triviyou.michal.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    Context context;
    Intent goGames, goRegister, inputIntent;
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView registerLinkTview;
    String email, password;
    Helper helper = new Helper();
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // Make sure Firebase is initialized
        setContentView(R.layout.activity_login);
        initComponents();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = etEmail.getText().toString().toLowerCase();
                password = etPassword.getText().toString();

                //change mail length to 3
                if (email.length() < 2 || password.equals("")) {
                    helper.toasting(context, getString(R.string.messageEmptyOrInvalid));
                } else {
                    checkValidAuth(email, password); // check in firebaseAuth
                }
            }
        });

        registerLinkTview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goRegister);
            }
        });
    }


    private void checkValidAuth(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        Log.d("LoginActivity", "Email: " + email + " Password: " + password);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) { // Sign-in successful
                        FirebaseUser user = mAuth.getCurrentUser();

                        goGames.putExtra("email", email);
                        goGames.putExtra("userId", user.getUid());
                        startActivity(goGames);

                    } else { // Sign-in failed
                        String errorMessage = task.getException().getMessage();
                        Log.e("LoginError", errorMessage);
                        helper.toasting(context, getString(R.string.messageEmptyOrInvalid));
                    }
                });
    }

    private void initComponents() {
        context = LoginActivity.this;
        inputIntent = getIntent();
        goGames = new Intent(context, GamesActivity.class);
        goRegister = new Intent(context, RegisterActivity.class);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        registerLinkTview = findViewById(R.id.registerLinkTview);
    }
}