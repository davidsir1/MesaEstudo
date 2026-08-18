package com.example.mesaestudo.ui.startscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mesaestudo.R;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegisterReturn, btnRegisterCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnRegisterReturn = findViewById(R.id.btnRegisterReturn);
        btnRegisterCreateAccount = findViewById(R.id.btnRegisterCreateAccount);

        btnRegisterReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_activity = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login_activity);
                finish();
            }
        });
    }
}