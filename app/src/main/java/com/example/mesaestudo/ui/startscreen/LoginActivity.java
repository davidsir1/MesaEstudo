package com.example.mesaestudo.ui.startscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mesaestudo.MainActivity;
import com.example.mesaestudo.R;
import com.example.mesaestudo.utils.DatabaseConnection;
import com.example.mesaestudo.utils.SessionManager;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private EditText inLoginUser, inLoginPassword;
    private Button btnLoginSignIn, btnLoginSignUp, btnLoginGuest;
    private SessionManager sessionManager;

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

        sessionManager = new SessionManager(this);

        inLoginPassword = findViewById(R.id.inLoginPassword);
        inLoginUser = findViewById(R.id.inLoginUser);
        btnLoginSignIn = findViewById(R.id.btnLoginSignIn);
        btnLoginSignUp = findViewById(R.id.btnLoginSignUp);
        btnLoginGuest = findViewById(R.id.btnLoginGuest);

        btnLoginSignIn.setOnClickListener(v -> realizarLogin());

        btnLoginGuest.setOnClickListener(v -> {
            sessionManager.createLoginSession(-1); // Guest ID
            Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(mainActivity);
            finish();
        });

        btnLoginSignUp.setOnClickListener(v -> {
            Intent register_activity = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(register_activity);
            finish();
        });
    }

    private void realizarLogin() {
        String usuario = inLoginUser.getText().toString().trim();
        String senha = inLoginPassword.getText().toString().trim();

        if (usuario.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "SELECT id_usuario, senha FROM tb_usuario WHERE nome_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usuario);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String hashedSenha = rs.getString("senha");
                        int userId = rs.getInt("id_usuario");
                        
                        // Verifying the password
                        if (BCrypt.checkpw(senha, hashedSenha)) {
                            sessionManager.createLoginSession(userId);
                            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();
                            
                            Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainActivity);
                            finish();
                        } else {
                            Toast.makeText(this, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Usuário ou senha incorretos!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (SQLException e) {
                Toast.makeText(this, "Erro ao realizar login: " + e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Erro de conexão com o banco de dados!", Toast.LENGTH_SHORT).show();
        }
    }
}