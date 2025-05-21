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
    Button bLogin;
    TextView tvRegisterLink;
    String email, password;
    Helper helper = new Helper();
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this); // make sure Firebase is initialized
        setContentView(R.layout.activity_login);
        context = LoginActivity.this;
        inputIntent = getIntent();
        goGames = new Intent(context, GamesActivity.class);
        goRegister = new Intent(context, RegisterActivity.class);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        bLogin = findViewById(R.id.bLogin);
        tvRegisterLink = findViewById(R.id.tvRegisterLink);
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = etEmail.getText().toString().toLowerCase();
                password = etPassword.getText().toString();
                if (email.isEmpty() || !email.contains("@") || password.equals("")) {
                    helper.toasting(context, getString(R.string.messageEmptyOrInvalid));
                } else {
                    checkValidAuth(email, password); // check in firebaseAuth
                }
                if (!Helper.isInternetAvailable(context)) {
                    helper.toasting(context,getString(R.string.noInternetConnection));
                    return;
                }
            }
        });
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goRegister);
            }
        });
    }


    private void checkValidAuth(String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) { // sign in successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        goGames.putExtra("email", email);
                        goGames.putExtra("userId", user.getUid());
                        startActivity(goGames);
                    } else { // sign in failed
                        String errorMessage = task.getException().getMessage();
                        Log.e("LoginError", errorMessage);
                        helper.toasting(context, getString(R.string.messageEmptyOrInvalid));
                    }
                });
    }
}