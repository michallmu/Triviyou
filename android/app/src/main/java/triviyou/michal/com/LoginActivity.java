package triviyou.michal.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Context context;
    Intent goMain, goRegister, inputIntent;
    EditText etEmail, etPassword;
    Button btnLogin;
    TextView registerLinkTview;
    String emailStr, passwordStr;
    Helper helper = new Helper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initComponents();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                emailStr = etEmail.getText().toString();
                passwordStr = etPassword.getText().toString();

                //todo  change mail length to 3
                if (emailStr.length() < 2 || passwordStr.equals("")) {
                    helper.toasting(context,getString(R.string.messageEmptyOrInvalid));
                }
                else {
                    if (IsUserValidAuth(emailStr, passwordStr)) //like: not in firebase auth.
                    {
                        startActivity(goMain);
                    }
                    else {
                        helper.toasting(context, getString(R.string.messageEmptyOrInvalid));

                    }
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




    private boolean IsUserValidAuth(String emailStr, String passwordStr) {
        return true;
    }

    private void initComponents() {
        context = LoginActivity.this;
        inputIntent = getIntent();
        goMain = new Intent(context, MainActivity.class);
        goRegister = new Intent(context, RegisterActivity.class);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        registerLinkTview = findViewById(R.id.registerLinkTview);
    }
}