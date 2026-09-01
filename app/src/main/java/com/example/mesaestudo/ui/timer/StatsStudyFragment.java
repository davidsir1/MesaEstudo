package com.example.mesaestudo.ui.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mesaestudo.R;
import com.example.mesaestudo.utils.DatabaseConnection;
import com.example.mesaestudo.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class StatsStudyFragment extends Fragment {

    private TextView tvWeeklyTotal, tvStreakCount;
    private SessionManager sessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats_study, container, false);
        
        sessionManager = new SessionManager(getContext());
        tvWeeklyTotal = view.findViewById(R.id.tvWeeklyTotal);
        tvStreakCount = view.findViewById(R.id.tvStreakCount);
        
        getParentFragmentManager().setFragmentResultListener("session_refresh", getViewLifecycleOwner(), (requestKey, result) -> {
            carregarEstatisticas();
        });

        carregarEstatisticas();
        
        return view;
    }

    private void carregarEstatisticas() {
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            try {
                // Total week
                String sqlWeek = "SELECT SEC_TO_TIME(SUM(TIME_TO_SEC(tempo))) as total FROM tb_sessao WHERE id_usuario = ? AND YEARWEEK(data_sessao, 1) = YEARWEEK(CURDATE(), 1)";
                PreparedStatement stmtWeek = conn.prepareStatement(sqlWeek);
                stmtWeek.setInt(1, sessionManager.getUserId());
                ResultSet rsWeek = stmtWeek.executeQuery();
                if (rsWeek.next()) {
                    String total = rsWeek.getString("total");
                    tvWeeklyTotal.setText(total != null ? total.substring(0, 5) + "h" : "00:00h");
                }

                // Streak (simplified logic)
                String sqlStreak = "SELECT COUNT(DISTINCT data_sessao) as streak FROM tb_sessao WHERE id_usuario = ? AND data_sessao >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                PreparedStatement stmtStreak = conn.prepareStatement(sqlStreak);
                stmtStreak.setInt(1, sessionManager.getUserId());
                ResultSet rsStreak = stmtStreak.executeQuery();
                if (rsStreak.next()) {
                    tvStreakCount.setText(String.valueOf(rsStreak.getInt("streak")));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}