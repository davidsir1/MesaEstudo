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

import com.example.mesaestudo.R;
import com.example.mesaestudo.utils.DatabaseConnection;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterActivity extends AppCompatActivity {

    private EditText inRegisterUser, inRegisterEmail, inRegisterPassword, inRegisterConfirmPassword;
    private Button btnRegisterReturn, btnRegisterCreateAccount;

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

        inRegisterUser = findViewById(R.id.inRegisterUser);
        inRegisterEmail = findViewById(R.id.inRegisterEmail);
        inRegisterPassword = findViewById(R.id.inRegisterPassword);
        inRegisterConfirmPassword = findViewById(R.id.inRegisterConfirmPassword);
        btnRegisterReturn = findViewById(R.id.btnRegisterReturn);
        btnRegisterCreateAccount = findViewById(R.id.btnRegisterCreateAccount);

        btnRegisterReturn.setOnClickListener(v -> {
            Intent login_activity = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(login_activity);
            finish();
        });

        btnRegisterCreateAccount.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        String usuario = inRegisterUser.getText().toString().trim();
        String email = inRegisterEmail.getText().toString().trim();
        String senha = inRegisterPassword.getText().toString().trim();
        String confirmarSenha = inRegisterConfirmPassword.getText().toString().trim();

        if (usuario.isEmpty() || email.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Hashing the password
        String hashedSenha = BCrypt.hashpw(senha, BCrypt.gensalt());

        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "INSERT INTO tb_usuario (nome_usuario, email, senha) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usuario);
                stmt.setString(2, email);
                stmt.setString(3, hashedSenha);
                stmt.executeUpdate();

                Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                
                Intent login_activity = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(login_activity);
                finish();
            } catch (SQLException e) {
                Toast.makeText(this, "Erro ao cadastrar: " + e.getMessage(), Toast.LENGTH_LONG).show();
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