package com.example.mesaestudo.ui.grades;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mesaestudo.R;
import com.example.mesaestudo.models.Disciplina;
import com.example.mesaestudo.models.Nota;
import com.example.mesaestudo.utils.DatabaseConnection;
import com.example.mesaestudo.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GradesFragment extends Fragment {

    private Button btnAddGrade;
    private ListView lvGrades;
    private TextView tvAverage;
    private SessionManager sessionManager;
    private List<Disciplina> listDisciplinas = new ArrayList<>();
    private List<Nota> listNotas = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public GradesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        sessionManager = new SessionManager(getContext());

        btnAddGrade = view.findViewById(R.id.button4);
        lvGrades = view.findViewById(R.id.listViewGrades);
        tvAverage = view.findViewById(R.id.lbGrade);

        btnAddGrade.setOnClickListener(v -> showAddGradeDialog(null));

        lvGrades.setOnItemClickListener((parent, v, position, id) -> showOptionsDialog(listNotas.get(position)));

        carregarDados();

        return view;
    }

    private void carregarDados() {
        carregarDisciplinas();
        carregarNotas();
    }

    private void carregarDisciplinas() {
        listDisciplinas.clear();
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "SELECT * FROM tb_disciplina WHERE id_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionManager.getUserId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        listDisciplinas.add(new Disciplina(rs.getInt("id_disciplina"), rs.getString("nome_disciplina"), rs.getString("cor")));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void carregarNotas() {
        listNotas.clear();
        List<String> displayList = new ArrayList<>();
        float somaNotasPesadas = 0;
        float somaPesos = 0;

        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "SELECT n.*, d.nome_disciplina FROM tb_nota n JOIN tb_disciplina d ON n.id_disciplina = d.id_disciplina WHERE n.id_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionManager.getUserId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Nota n = new Nota(
                                rs.getInt("id_nota"),
                                rs.getInt("id_disciplina"),
                                rs.getInt("id_usuario"),
                                rs.getFloat("nota"),
                                rs.getDate("data_da_nota"),
                                rs.getFloat("peso")
                        );
                        listNotas.add(n);
                        displayList.add(n.getValor() + " - " + rs.getString("nome_disciplina") + " (Peso: " + n.getPeso() + ")");
                        
                        somaNotasPesadas += (n.getValor() * n.getPeso());
                        somaPesos += n.getPeso();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayList);
        lvGrades.setAdapter(adapter);

        // Calcular e exibir a média
        if (somaPesos > 0) {
            float media = somaNotasPesadas / somaPesos;
            tvAverage.setText(String.format(Locale.getDefault(), "%.1f", media));
        } else {
            tvAverage.setText("0.0");
        }
    }

    private void showOptionsDialog(Nota nota) {
        new AlertDialog.Builder(getContext())
                .setTitle("Opções")
                .setItems(new CharSequence[]{"Editar", "Excluir", "Cancelar"}, (dialog, which) -> {
                    if (which == 0) showAddGradeDialog(nota);
                    else if (which == 1) confirmarExclusao(nota);
                }).show();
    }

    private void confirmarExclusao(Nota nota) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Exclusão")
                .setMessage("Deseja realmente excluir esta nota?")
                .setPositiveButton("Excluir", (dialog, which) -> excluirNota(nota))
                .setNegativeButton("Voltar", null)
                .show();
    }

    private void excluirNota(Nota nota) {
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "DELETE FROM tb_nota WHERE id_nota = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, nota.getId());
                stmt.executeUpdate();
                Toast.makeText(getContext(), "Nota excluída!", Toast.LENGTH_SHORT).show();
                carregarNotas();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAddGradeDialog(Nota itemParaEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_grade, null);
        builder.setView(dialogView);

        Spinner spinnerDisc = dialogView.findViewById(R.id.spinnerGradeDiscipline);
        EditText etValue = dialogView.findViewById(R.id.etGradeValue);
        EditText etWeight = dialogView.findViewById(R.id.etGradeWeight);
        EditText etDate = dialogView.findViewById(R.id.etGradeDate);

        etDate.setOnClickListener(v -> showDatePicker(etDate));

        ArrayAdapter<Disciplina> adapterDisc = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listDisciplinas);
        adapterDisc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisc.setAdapter(adapterDisc);

        if (itemParaEditar != null) {
            etValue.setText(String.valueOf(itemParaEditar.getValor()));
            etWeight.setText(String.valueOf(itemParaEditar.getPeso()));
            if (itemParaEditar.getData() != null) etDate.setText(dateFormat.format(itemParaEditar.getData()));
            for (int i = 0; i < listDisciplinas.size(); i++) {
                if (listDisciplinas.get(i).getId() == itemParaEditar.getIdDisciplina()) {
                    spinnerDisc.setSelection(i);
                    break;
                }
            }
        }

        builder.setPositiveButton(itemParaEditar == null ? "Adicionar Nota" : "Salvar Alterações", (dialog, which) -> {
            String valorStr = etValue.getText().toString();
            String pesoStr = etWeight.getText().toString();
            String data = etDate.getText().toString();
            Disciplina disc = (Disciplina) spinnerDisc.getSelectedItem();

            if (valorStr.isEmpty() || disc == null) {
                Toast.makeText(getContext(), "Nota e Disciplina são obrigatórios!", Toast.LENGTH_SHORT).show();
                return;
            }

            float valor = Float.parseFloat(valorStr);
            float peso = pesoStr.isEmpty() ? 1.0f : Float.parseFloat(pesoStr);

            salvarNota(itemParaEditar, disc.getId(), valor, peso, data);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void salvarNota(Nota item, int discId, float valor, float peso, String data) {
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = item == null ?
                    "INSERT INTO tb_nota (id_disciplina, id_usuario, nota, data_da_nota, peso) VALUES (?, ?, ?, ?, ?)" :
                    "UPDATE tb_nota SET id_disciplina = ?, nota = ?, data_da_nota = ?, peso = ? WHERE id_nota = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (item == null) {
                    stmt.setInt(1, discId);
                    stmt.setInt(2, sessionManager.getUserId());
                    stmt.setFloat(3, valor);
                    if (!data.isEmpty()) stmt.setDate(4, new java.sql.Date(dateFormat.parse(data).getTime()));
                    else stmt.setNull(4, java.sql.Types.DATE);
                    stmt.setFloat(5, peso);
                } else {
                    stmt.setInt(1, discId);
                    stmt.setFloat(2, valor);
                    if (!data.isEmpty()) stmt.setDate(3, new java.sql.Date(dateFormat.parse(data).getTime()));
                    else stmt.setNull(3, java.sql.Types.DATE);
                    stmt.setFloat(4, peso);
                    stmt.setInt(5, item.getId());
                }
                stmt.executeUpdate();
                carregarNotas();
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year1, month1, dayOfMonth) -> {
            String date = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
            editText.setText(date);
        }, year, month, day);
        datePickerDialog.show();
    }
}