package triviyou.michal.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {

    TextView tvBack;
    Context context;
    Intent inputIntent, goMain, goLogin;
    EditText etNicknameReg, emailEtRegister, passwordEtRegister, repeatPasswordEtRegister;
    Button btnCreateAcc;
    Helper helper = new Helper();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
        firebaseAuth = FirebaseAuth.getInstance();


        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stEmail  = emailEtRegister.getText().toString();
                String stPassword = passwordEtRegister.getText().toString();
                String stNickName = etNicknameReg.getText().toString();
                String stRepeatPassword = repeatPasswordEtRegister.getText().toString();

                if(validate(stEmail, stNickName, stPassword, stRepeatPassword))
                {

                                ///nicknema
                    checkIfEmailExistsAndRegister(stEmail, stPassword, stNickName);


//                    firebaseAuth.createUserWithEmailAndPassword(email, password)
//                            .addOnCompleteListener(RegisterActivity.this, task -> {
//                                if (task.isSuccessful()) {
//                                    // Registration successful
//                                    Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
//                                    FirebaseUser user = firebaseAuth.getCurrentUser();
//                                    // You can redirect to another activity here
//                                } else {
//                                    // Registration failed
//                                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            });
                }


                }
            });



        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goLogin);
            }
        });
    }

private void checkIfEmailExistsAndRegister(final String email, final String nickname, final String password) {
    firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
        @Override
        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
            if (task.isSuccessful()) {
                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                if (isNewUser) {
                    //     helper.toasting(context, String.valueOf(isNewUser));
                     createNewUser(email, nickname, password);
                } else {
                    // Email already exists
                    Toast.makeText(RegisterActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Error checking email!", Toast.LENGTH_SHORT).show();
            }
        }
    });
}

private void createNewUser(final String email, final String nickname, final String password) {
    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Registration success, user is created
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Save additional user details like nickname to database if needed
                    Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                    // Redirect to PlayActivity
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                    finish();
                }
            } else {
                // we already check the email exist before, but in order  to be sure , we let user know , if already exist
                // can happened in case some users register in parallel

                if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                    Toast.makeText(RegisterActivity.this, "Email already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    });
}


private boolean validate(String stEmail, String stNickName, String stPassword, String stRepeatPassword) {

        if (stEmail.equals("") || stNickName.equals("") || stPassword.equals("")
                || stRepeatPassword.equals("")) {
            helper.toasting(context,getString(R.string.emptyStatement));
            return false;
        }

        if (stPassword.length()<3) {
            helper.toasting(context,getString(R.string.messagePassword));
            return false;
        }

        if (!stPassword.equals(stRepeatPassword) || passwordEtRegister.length()<3) {
            helper.toasting(context,getString(R.string.messageNotEqualPasswords));
            return false;
        }
        return true;
    }

    private void initComponents() {
        context = RegisterActivity.this;
        inputIntent = getIntent();
        goLogin = new Intent(context, LoginActivity.class);
        goMain = new Intent(context, MainActivity.class);
        etNicknameReg = findViewById(R.id.etNicknameReg);
        emailEtRegister = findViewById(R.id.emailEtRegister);
        passwordEtRegister = findViewById(R.id.passwordEtRegister);
        repeatPasswordEtRegister = findViewById(R.id.repeatPasswordEtRegister);
        btnCreateAcc = findViewById(R.id.btnCreateAcc);
        tvBack = findViewById(R.id.tvBack);
    }
}