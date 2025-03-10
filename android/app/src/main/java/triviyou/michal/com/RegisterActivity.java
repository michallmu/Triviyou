package triviyou.michal.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;

public class RegisterActivity extends AppCompatActivity {

    ImageButton imgBback1;
    Context context;
    Intent inputIntent, goGames, goLogin, goUserGuide;
    EditText etEmailRegister, etPasswordRegister, etRepeatPasswordRegister;
    Button bCreateAcc;
    Helper helper = new Helper();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initComponents();
        firebaseAuth = FirebaseAuth.getInstance();

        imgBback1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goLogin);
            }
        });
        bCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Helper.isInternetAvailable(context)) {
                    helper.toasting(context,"אין חיבור לאינטרנט");
                    return;
                }

                String stEmail = etEmailRegister.getText().toString();
                String stPassword = etPasswordRegister.getText().toString();
                String stRepeatPassword = etRepeatPasswordRegister.getText().toString();

                if (validate(stEmail, stPassword, stRepeatPassword)) {
                    checkIfEmailExistsAndRegister(stEmail, stPassword);
                }


            }


            private void checkIfEmailExistsAndRegister(final String email, final String password) {
                firebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                            if (isNewUser) {

                                createNewUser(email, password);
                            } else {
                                Toast.makeText(RegisterActivity.this, getString(R.string.emailAlreadyExists), Toast.LENGTH_SHORT).show();
                            }
                        } /*else {
                            Toast.makeText(RegisterActivity.this, getString(R.string.ErrorCheckingEmail), Toast.LENGTH_SHORT).show();
                        }*/
                    }
                });
            }

            private void createNewUser(final String email, final String password) {
                firebaseAuth.createUserWithEmailAndPassword(email.toLowerCase(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registration success, user is created
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {

                                // Save additional user details like nickname to database if needed
                                Toast.makeText(RegisterActivity.this, getString(R.string.registeredSuccessfuly), Toast.LENGTH_SHORT).show();
                                // Redirect to PlayActivity
                                startActivity(new Intent(RegisterActivity.this, GamesActivity.class));
                                finish();
                            }
                        } else {
                            // we already check the email exist before, but in order  to be sure , we let user know , if already exist
                            // can happened in case some users register in parallel

                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, getString(R.string.emailAlreadyExists), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "הרשמה נכשלה: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }

            private boolean validate(String stEmail, String stPassword, String stRepeatPassword) {
                if (stEmail.equals("") || stPassword.equals("")
                        || stRepeatPassword.equals("")) {
                    helper.toasting(context, getString(R.string.emptyStatement));
                    return false;
                }
                if (stPassword.length() < 3) {
                    helper.toasting(context, getString(R.string.messagePassword));
                    return false;
                }
                if (!stPassword.equals(stRepeatPassword) || etPasswordRegister.length() < 3) {
                    helper.toasting(context, getString(R.string.messageNotEqualPasswords));
                    return false;
                }
                return true;
            }
        });
    }

            private void initComponents() {
                context = RegisterActivity.this;
                inputIntent = getIntent();
                goLogin = new Intent(context, LoginActivity.class);
                goGames = new Intent(context, GamesActivity.class);
                goUserGuide = new Intent(context, UserGuide.class);
                etEmailRegister = findViewById(R.id.etEmailRegister);
                etPasswordRegister = findViewById(R.id.etPasswordRegister);
                etRepeatPasswordRegister = findViewById(R.id.etRepeatPasswordRegister);
                bCreateAcc = findViewById(R.id.bCreateAcc);
                imgBback1 = findViewById(R.id.imgBback1);
            }
        }


