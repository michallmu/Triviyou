package triviyou.michal.com;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class UserGuide extends AppCompatActivity {

    Context context;
    Intent inputIntent, goRegister;
    Button bBack2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_guide);
        initComponents();

        bBack2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(goRegister);
            }
        });

    }

    private void initComponents() {
        context = UserGuide.this;
        inputIntent = getIntent();
        goRegister = new Intent(context, RegisterActivity.class);
        bBack2 = findViewById(R.id.bBack2);

    }
}