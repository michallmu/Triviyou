package triviyou.michal.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Context context;
    Intent inputIntent;
    String email, id;
    TextView tvEmail, tvId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initComponents();

        email = inputIntent.getStringExtra("EMAIL");
        id = inputIntent.getStringExtra("USER ID");
        tvEmail.setText(email);
        tvId.setText(id);

    }

    private void initComponents() {
        context = MainActivity.this;
        inputIntent = getIntent();
        tvEmail = findViewById(R.id.tvEmail);
        tvId = findViewById(R.id.tvId);

    }
}