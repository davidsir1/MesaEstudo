package com.example.mesaestudo.ui.timer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mesaestudo.R;
import com.example.mesaestudo.models.Disciplina;
import com.example.mesaestudo.utils.DatabaseConnection;
import com.example.mesaestudo.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TimerClockFragment extends Fragment {

    private TextView tvTimer, tvState;
    private Button btnStart, btnEnd, btnBreak;
    private Button btn30, btn35, btn40, btn45;
    private EditText etWorkDescription;
    private Spinner spinnerDisciplines;
    
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private long startTimeInMillis = 30 * 60 * 1000; // Default 30m
    private boolean timerRunning = false;
    private boolean isBreak = false;
    
    private SessionManager sessionManager;
    private List<Disciplina> disciplines = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer_clock, container, false);
        
        sessionManager = new SessionManager(getContext());
        
        tvTimer = view.findViewById(R.id.tvTimer);
        tvState = view.findViewById(R.id.textView15);
        btnStart = view.findViewById(R.id.button);
        btnEnd = view.findViewById(R.id.button2);
        btnBreak = view.findViewById(R.id.button3);
        
        btn30 = view.findViewById(R.id.button5);
        btn35 = view.findViewById(R.id.button6);
        btn40 = view.findViewById(R.id.button7);
        btn45 = view.findViewById(R.id.button8);
        
        etWorkDescription = view.findViewById(R.id.editTextText);
        spinnerDisciplines = view.findViewById(R.id.spinner);
        
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        
        setupListeners();
        loadDisciplines();
        
        return view;
    }

    private void setupListeners() {
        btnStart.setOnClickListener(v -> {
            if (timerRunning) pauseTimer();
            else startTimer();
        });

        btnEnd.setOnClickListener(v -> encerrarSessao());
        btnBreak.setOnClickListener(v -> startBreak());

        btn30.setOnClickListener(v -> setInitialTime(30));
        btn35.setOnClickListener(v -> setInitialTime(35));
        btn40.setOnClickListener(v -> setInitialTime(40));
        btn45.setOnClickListener(v -> setInitialTime(45));
    }

    private void setInitialTime(int minutes) {
        if (!timerRunning) {
            startTimeInMillis = minutes * 60 * 1000L;
            timeLeftInMillis = startTimeInMillis;
            updateCountDownText();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                btnStart.setText("Iniciar");
                if (!isBreak) {
                    salvarSessao(startTimeInMillis);
                    getParentFragmentManager().setFragmentResult("session_refresh", new Bundle());
                    Toast.makeText(getContext(), "Sessão de Foco Finalizada!", Toast.LENGTH_LONG).show();
                } else {
                    isBreak = false;
                    tvState.setText("Foco");
                    resetTimer();
                    getParentFragmentManager().setFragmentResult("session_refresh", new Bundle());
                    Toast.makeText(getContext(), "Intervalo Finalizado!", Toast.LENGTH_LONG).show();
                }
            }
        }.start();

        timerRunning = true;
        btnStart.setText("Pausar");
    }

    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        timerRunning = false;
        btnStart.setText("Continuar");
    }

    private void resetTimer() {
        pauseTimer();
        isBreak = false;
        tvState.setText("Foco");
        timeLeftInMillis = startTimeInMillis;
        updateCountDownText();
        btnStart.setText("Iniciar");
    }

    private void encerrarSessao() {
        if (!isBreak && (timeLeftInMillis < startTimeInMillis)) {
            long tempoDecorrido = startTimeInMillis - timeLeftInMillis;
            salvarSessao(tempoDecorrido);
            getParentFragmentManager().setFragmentResult("session_refresh", new Bundle());
            Toast.makeText(getContext(), "Sessão encerrada e salva!", Toast.LENGTH_SHORT).show();
        }
        resetTimer();
    }

    private void startBreak() {
        pauseTimer();
        isBreak = true;
        tvState.setText("Pausa");
        timeLeftInMillis = 10 * 60 * 1000; // 10 minutes
        updateCountDownText();
        startTimer();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
    }

    private void loadDisciplines() {
        disciplines.clear();
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            try {
                String sql = "SELECT * FROM tb_disciplina WHERE id_usuario = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, sessionManager.getUserId());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    disciplines.add(new Disciplina(rs.getInt("id_disciplina"), rs.getString("nome_disciplina"), rs.getString("cor")));
                }
                ArrayAdapter<Disciplina> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, disciplines);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDisciplines.setAdapter(adapter);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void salvarSessao(long tempoMillis) {
        Disciplina d = (Disciplina) spinnerDisciplines.getSelectedItem();
        if (d == null) return;

        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            try {
                String sql = "INSERT INTO tb_sessao (id_disciplina, id_usuario, tempo, data_sessao) VALUES (?, ?, ?, CURDATE())";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, d.getId());
                stmt.setInt(2, sessionManager.getUserId());
                
                long durationSeconds = tempoMillis / 1000;
                long hours = durationSeconds / 3600;
                long minutes = (durationSeconds % 3600) / 60;
                long seconds = durationSeconds % 60;
                String timeStr = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                stmt.setString(3, timeStr);
                
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}