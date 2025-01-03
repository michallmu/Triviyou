package triviyou.michal.com;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class ProfileActivity extends AppCompatActivity {

    Context context;
    ImageButton imgbBack4;
    TextView tvWantChangePassword, tvIwantLogOut, tvCurrentEmail;
    Intent inputIntent, goGames, goLogin;
    String email;
    private boolean isFragmentDisplayed = false; // משתנה שמנהל את מצב הפרגמנט
    private static final String FRAGMENT_TAG = "CHANGE_PASSWORD_FRAGMENT"; // תגית לזיהוי הפרגמנט

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        context = ProfileActivity.this;
        imgbBack4 = findViewById(R.id.imgbBack4);
        tvWantChangePassword = findViewById(R.id.tvWantChangePassword);
        inputIntent = getIntent();
        tvIwantLogOut = findViewById(R.id.tvIwantLogOut);
        tvCurrentEmail = findViewById(R.id.tvCurrentEmail);
        goGames = new Intent(context, GamesActivity.class);
        goLogin = new Intent(context, LoginActivity.class);

        email = inputIntent.getStringExtra("email");
        tvCurrentEmail.setText(email);

        tvWantChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFragmentDisplayed) {
                    closeFragment();
                    tvWantChangePassword.setText(getString(R.string.iWantChangePass)); // מצב מקורי - רוצה לשנות
                } else {
                    showFragment(); // Show the fragment
                    tvWantChangePassword.setText(getString(R.string.close)); // אני רוצה לסגור את הפרגמנט
                }
            }
        });

        tvIwantLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goLogin);
            }
        });
    }

        private void showFragment() {
            Fragment fragment = new changePasswordFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frameLayoutChangePassword, fragment, FRAGMENT_TAG)
                    .commit();
            isFragmentDisplayed = true;
        }

        private void closeFragment() {
            Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commit();
                isFragmentDisplayed = false;
            }

            imgbBack4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(goGames);
                }
            });
    }


        private void replaceFragment(Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayoutChangePassword, fragment);
            fragmentTransaction.commit();

        }
    }

