package com.example.mesaestudo.ui.startscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mesaestudo.MainActivity;
import com.example.mesaestudo.R;

public class LoginActivity extends AppCompatActivity {

    EditText inLoginUser, inLoginPassword;

    Button btnLoginSignIn, btnLoginSignUp, btnLoginGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inLoginPassword = findViewById(R.id.inLoginPassword);
        inLoginUser = findViewById(R.id.inLoginUser);
        btnLoginSignIn = findViewById(R.id.btnLoginSignIn);
        btnLoginSignUp = findViewById(R.id.btnLoginSignUp);
        btnLoginGuest = findViewById(R.id.btnLoginGuest);

        btnLoginGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });

        btnLoginSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_activity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register_activity);
                finish();
            }
        });
    }
}