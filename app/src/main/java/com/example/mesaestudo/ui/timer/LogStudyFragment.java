package com.example.mesaestudo.ui.timer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mesaestudo.R;
import com.example.mesaestudo.models.Sessao;
import com.example.mesaestudo.utils.DatabaseConnection;
import com.example.mesaestudo.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LogStudyFragment extends Fragment {

    private RecyclerView rvSessions;
    private TextView tvNoSessions;
    private TextView tvStatsToday, tvStatsWeek, tvStatsTotalSessions, tvStatsTotalHours;
    private SessionManager sessionManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_study, container, false);
        
        sessionManager = new SessionManager(getContext());
        rvSessions = view.findViewById(R.id.rvSessions);
        rvSessions.setLayoutManager(new LinearLayoutManager(getContext()));
        tvNoSessions = view.findViewById(R.id.tvNoSessions);

        tvStatsToday = view.findViewById(R.id.tvStatsToday);
        tvStatsWeek = view.findViewById(R.id.tvStatsWeek);
        tvStatsTotalSessions = view.findViewById(R.id.tvStatsTotalSessions);
        tvStatsTotalHours = view.findViewById(R.id.tvStatsTotalHours);
        
        getParentFragmentManager().setFragmentResultListener("session_refresh", getViewLifecycleOwner(), (requestKey, result) -> {
            carregarSessoes();
            carregarEstatisticas();
        });

        carregarSessoes();
        carregarEstatisticas();
        
        return view;
    }

    private void carregarEstatisticas() {
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            try {
                int userId = sessionManager.getUserId();

                // Total de Sessões
                String sqlTotal = "SELECT COUNT(*) FROM tb_sessao WHERE id_usuario = ?";
                PreparedStatement stmtTotal = conn.prepareStatement(sqlTotal);
                stmtTotal.setInt(1, userId);
                ResultSet rsTotal = stmtTotal.executeQuery();
                if (rsTotal.next()) {
                    tvStatsTotalSessions.setText(String.valueOf(rsTotal.getInt(1)));
                }

                // Hoje
                String sqlToday = "SELECT SUM(TIME_TO_SEC(tempo)) FROM tb_sessao WHERE id_usuario = ? AND data_sessao = CURDATE()";
                PreparedStatement stmtToday = conn.prepareStatement(sqlToday);
                stmtToday.setInt(1, userId);
                ResultSet rsToday = stmtToday.executeQuery();
                if (rsToday.next()) {
                    tvStatsToday.setText(formatSeconds(rsToday.getLong(1)));
                }

                // Semana (últimos 7 dias)
                String sqlWeek = "SELECT SUM(TIME_TO_SEC(tempo)) FROM tb_sessao WHERE id_usuario = ? AND data_sessao >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)";
                PreparedStatement stmtWeek = conn.prepareStatement(sqlWeek);
                stmtWeek.setInt(1, userId);
                ResultSet rsWeek = stmtWeek.executeQuery();
                if (rsWeek.next()) {
                    tvStatsWeek.setText(formatSeconds(rsWeek.getLong(1)));
                }

                // Total de Horas
                String sqlTotalHours = "SELECT SUM(TIME_TO_SEC(tempo)) FROM tb_sessao WHERE id_usuario = ?";
                PreparedStatement stmtTotalHours = conn.prepareStatement(sqlTotalHours);
                stmtTotalHours.setInt(1, userId);
                ResultSet rsTotalHours = stmtTotalHours.executeQuery();
                if (rsTotalHours.next()) {
                    long totalSecs = rsTotalHours.getLong(1);
                    tvStatsTotalHours.setText(getString(R.string.stats_hours_format, totalSecs / 3600));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private String formatSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        return String.format(Locale.getDefault(), "%dh %dm", hours, minutes);
    }

    private void carregarSessoes() {
        List<String> displayList = new ArrayList<>();
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            try {
                String sql = "SELECT s.*, d.nome_disciplina FROM tb_sessao s JOIN tb_disciplina d ON s.id_disciplina = d.id_disciplina WHERE s.id_usuario = ? ORDER BY s.data_sessao DESC, s.id_sessao DESC";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, sessionManager.getUserId());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String row = String.format("%s: %s em %s", 
                            rs.getString("nome_disciplina"), 
                            rs.getString("tempo"), 
                            dateFormat.format(rs.getDate("data_sessao")));
                    displayList.add(row);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (displayList.isEmpty()) {
            tvNoSessions.setVisibility(View.VISIBLE);
            rvSessions.setVisibility(View.GONE);
        } else {
            tvNoSessions.setVisibility(View.GONE);
            rvSessions.setVisibility(View.VISIBLE);
            rvSessions.setAdapter(new SessionAdapter(displayList));
        }
    }

    private static class SessionAdapter extends RecyclerView.Adapter<SessionAdapter.ViewHolder> {
        private final List<String> sessions;

        SessionAdapter(List<String> sessions) {
            this.sessions = sessions;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(sessions.get(position));
        }

        @Override
        public int getItemCount() {
            return sessions.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            final TextView textView;
            ViewHolder(View view) {
                super(view);
                textView = view.findViewById(android.R.id.text1);
            }
        }
    }
}