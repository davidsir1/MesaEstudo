package com.example.mesaestudo.ui.plan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mesaestudo.R;
import com.example.mesaestudo.models.Disciplina;
import com.example.mesaestudo.models.Prova;
import com.example.mesaestudo.models.Trabalho;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PlanFragment extends Fragment {

    private Button btnAddWork, btnAddExam, btnAddDiscipline;
    private ListView lvWorks, lvExams, lvDisciplines, lvCalendar;
    private SessionManager sessionManager;
    private List<Disciplina> listDisciplinas = new ArrayList<>();
    private List<Trabalho> listTrabalhos = new ArrayList<>();
    private List<Prova> listProvas = new ArrayList<>();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        sessionManager = new SessionManager(getContext());

        btnAddWork = view.findViewById(R.id.btnAddWork);
        btnAddExam = view.findViewById(R.id.btnAddExam);
        btnAddDiscipline = view.findViewById(R.id.btnAddDiscipline);

        lvWorks = view.findViewById(R.id.listViewPlanWorks);
        lvExams = view.findViewById(R.id.listViewPlanExams);
        lvDisciplines = view.findViewById(R.id.listViewPlanDisciplines);
        lvCalendar = view.findViewById(R.id.listViewCalendar);

        btnAddWork.setOnClickListener(v -> showAddWorkDialog(null));
        btnAddExam.setOnClickListener(v -> showAddExamDialog(null));
        btnAddDiscipline.setOnClickListener(v -> showAddDisciplineDialog(null));

        lvWorks.setOnItemClickListener((parent, v, position, id) -> showOptionsDialog(listTrabalhos.get(position)));
        lvExams.setOnItemClickListener((parent, v, position, id) -> showOptionsDialog(listProvas.get(position)));
        lvDisciplines.setOnItemClickListener((parent, v, position, id) -> showOptionsDialog(listDisciplinas.get(position)));

        carregarDados();

        return view;
    }

    private void showOptionsDialog(Object item) {
        String title = "Opções";
        if (item instanceof Trabalho) title = ((Trabalho) item).getTitulo();
        else if (item instanceof Prova) title = ((Prova) item).getTitulo();
        else if (item instanceof Disciplina) title = ((Disciplina) item).getNome();

        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setItems(new CharSequence[]{"Editar", "Excluir", "Cancelar"}, (dialog, which) -> {
                    if (which == 0) { // Editar
                        if (item instanceof Trabalho) showAddWorkDialog((Trabalho) item);
                        else if (item instanceof Prova) showAddExamDialog((Prova) item);
                        else if (item instanceof Disciplina) showAddDisciplineDialog((Disciplina) item);
                    } else if (which == 1) { // Excluir
                        confirmarExclusao(item);
                    }
                }).show();
    }

    private void confirmarExclusao(Object item) {
        String msg = "Deseja realmente excluir?";
        if (item instanceof Disciplina) msg += "\n(Isso removerá todos os trabalhos e provas vinculados!)";

        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar Exclusão")
                .setMessage(msg)
                .setPositiveButton("Excluir", (dialog, which) -> excluirItem(item))
                .setNegativeButton("Voltar", null)
                .show();
    }

    private void excluirItem(Object item) {
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "";
            int id = -1;
            if (item instanceof Trabalho) {
                sql = "DELETE FROM tb_trabalho WHERE id_trabalho = ?";
                id = ((Trabalho) item).getId();
            } else if (item instanceof Prova) {
                sql = "DELETE FROM tb_prova WHERE id_prova = ?";
                id = ((Prova) item).getId();
            } else if (item instanceof Disciplina) {
                sql = "DELETE FROM tb_disciplina WHERE id_disciplina = ?";
                id = ((Disciplina) item).getId();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                Toast.makeText(getContext(), "Excluído com sucesso!", Toast.LENGTH_SHORT).show();
                carregarDados();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void carregarDados() {
        carregarDisciplinas();
        carregarTrabalhos();
        carregarProvas();
        carregarCalendario();
    }

    private void carregarCalendario() {
        List<CalendarItem> calendarItems = new ArrayList<>();
        
        // Add works with dates
        for (Trabalho t : listTrabalhos) {
            if (t.getDataEntrega() != null) {
                calendarItems.add(new CalendarItem(t.getTitulo(), t.getDataEntrega(), "Trabalho"));
            }
        }
        
        // Add exams
        for (Prova p : listProvas) {
            if (p.getDataProva() != null) {
                calendarItems.add(new CalendarItem(p.getTitulo(), p.getDataProva(), "Prova"));
            }
        }
        
        // Sort by date
        Collections.sort(calendarItems, (o1, o2) -> o1.date.compareTo(o2.date));
        
        List<String> displayList = new ArrayList<>();
        for (CalendarItem item : calendarItems) {
            displayList.add(dateFormat.format(item.date) + " - " + item.type + ": " + item.title);
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayList);
        lvCalendar.setAdapter(adapter);
    }

    private static class CalendarItem {
        String title;
        Date date;
        String type;

        CalendarItem(String title, Date date, String type) {
            this.title = title;
            this.date = date;
            this.type = type;
        }
    }

    private void carregarDisciplinas() {
        listDisciplinas.clear();
        List<String> displayList = new ArrayList<>();
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "SELECT * FROM tb_disciplina WHERE id_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionManager.getUserId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Disciplina d = new Disciplina(rs.getInt("id_disciplina"), rs.getString("nome_disciplina"), rs.getString("cor"));
                        listDisciplinas.add(d);
                        displayList.add(d.getNome());
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayList);
        lvDisciplines.setAdapter(adapter);
    }

    private void carregarTrabalhos() {
        listTrabalhos.clear();
        List<String> displayList = new ArrayList<>();
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "SELECT t.*, d.nome_disciplina FROM tb_trabalho t JOIN tb_disciplina d ON t.id_disciplina = d.id_disciplina WHERE t.id_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionManager.getUserId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Trabalho t = new Trabalho(
                                rs.getInt("id_trabalho"),
                                rs.getInt("id_disciplina"),
                                rs.getString("titulo_trabalho"),
                                rs.getString("tipo"),
                                rs.getDate("data_de_entrega"),
                                rs.getString("notas")
                        );
                        listTrabalhos.add(t);
                        displayList.add(t.getTitulo() + " (" + rs.getString("nome_disciplina") + ")");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayList);
        lvWorks.setAdapter(adapter);
    }

    private void carregarProvas() {
        listProvas.clear();
        List<String> displayList = new ArrayList<>();
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = "SELECT p.*, d.nome_disciplina FROM tb_prova p JOIN tb_disciplina d ON p.id_disciplina = d.id_disciplina WHERE p.id_usuario = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sessionManager.getUserId());
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Prova p = new Prova(
                                rs.getInt("id_prova"),
                                rs.getInt("id_disciplina"),
                                rs.getString("titulo_prova"),
                                rs.getDate("data_prova"),
                                rs.getString("dificuldade"),
                                rs.getInt("dias_estudo"),
                                rs.getString("comentarios")
                        );
                        listProvas.add(p);
                        displayList.add(p.getTitulo() + " (" + rs.getString("nome_disciplina") + ")");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, displayList);
        lvExams.setAdapter(adapter);
    }

    private void showAddWorkDialog(Trabalho itemParaEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_work, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etWorkTitle);
        Spinner spinnerType = dialogView.findViewById(R.id.spinnerWorkType);
        Spinner spinnerDisc = dialogView.findViewById(R.id.spinnerWorkDiscipline);
        EditText etDate = dialogView.findViewById(R.id.etWorkDueDate);
        EditText etNotes = dialogView.findViewById(R.id.etWorkNotes);

        etDate.setOnClickListener(v -> showDatePicker(etDate));

        String[] types = {"Redação", "Lista de Exercícios", "Laboratório", "Leitura", "Prova", "Projeto", "Teste", "Outro"};
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapterType);

        ArrayAdapter<Disciplina> adapterDisc = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listDisciplinas);
        adapterDisc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisc.setAdapter(adapterDisc);

        if (itemParaEditar != null) {
            etTitle.setText(itemParaEditar.getTitulo());
            etNotes.setText(itemParaEditar.getNotas());
            if (itemParaEditar.getDataEntrega() != null) etDate.setText(dateFormat.format(itemParaEditar.getDataEntrega()));
            // Selecionar tipo e disciplina nos spinners
            for (int i = 0; i < types.length; i++) if (types[i].equals(itemParaEditar.getTipo())) spinnerType.setSelection(i);
            for (int i = 0; i < listDisciplinas.size(); i++) if (listDisciplinas.get(i).getId() == itemParaEditar.getIdDisciplina()) spinnerDisc.setSelection(i);
        }

        builder.setPositiveButton(itemParaEditar == null ? "Adicionar Trabalho" : "Salvar Alterações", (dialog, which) -> {
            String titulo = etTitle.getText().toString();
            Disciplina disc = (Disciplina) spinnerDisc.getSelectedItem();
            String tipo = spinnerType.getSelectedItem().toString();
            String data = etDate.getText().toString();
            String notas = etNotes.getText().toString();

            if (titulo.isEmpty() || disc == null) {
                Toast.makeText(getContext(), "Título e Disciplina são obrigatórios!", Toast.LENGTH_SHORT).show();
                return;
            }

            salvarTrabalho(itemParaEditar, titulo, disc.getId(), tipo, data, notas);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void salvarTrabalho(Trabalho item, String titulo, int discId, String tipo, String data, String notas) {
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = item == null ? 
                    "INSERT INTO tb_trabalho (id_disciplina, id_usuario, titulo_trabalho, tipo, data_de_entrega, notas) VALUES (?, ?, ?, ?, ?, ?)" :
                    "UPDATE tb_trabalho SET id_disciplina = ?, titulo_trabalho = ?, tipo = ?, data_de_entrega = ?, notas = ? WHERE id_trabalho = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (item == null) {
                    stmt.setInt(1, discId);
                    stmt.setInt(2, sessionManager.getUserId());
                    stmt.setString(3, titulo);
                    stmt.setString(4, tipo);
                    if (!data.isEmpty()) stmt.setDate(5, new java.sql.Date(dateFormat.parse(data).getTime()));
                    else stmt.setNull(5, java.sql.Types.DATE);
                    stmt.setString(6, notas);
                } else {
                    stmt.setInt(1, discId);
                    stmt.setString(2, titulo);
                    stmt.setString(3, tipo);
                    if (!data.isEmpty()) stmt.setDate(4, new java.sql.Date(dateFormat.parse(data).getTime()));
                    else stmt.setNull(4, java.sql.Types.DATE);
                    stmt.setString(5, notas);
                    stmt.setInt(6, item.getId());
                }
                stmt.executeUpdate();
                carregarTrabalhos();
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAddExamDialog(Prova itemParaEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_exam, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etExamTitle);
        Spinner spinnerDisc = dialogView.findViewById(R.id.spinnerExamDiscipline);
        EditText etDate = dialogView.findViewById(R.id.etExamDate);
        Spinner spinnerDiff = dialogView.findViewById(R.id.spinnerExamDifficulty);
        EditText etNotes = dialogView.findViewById(R.id.etExamNotes);

        etDate.setOnClickListener(v -> showDatePicker(etDate));

        String[] difficulties = {"Fácil (3 dias)", "Médio (7 dias)", "Difícil (14 dias)", "Extremo (21 dias)"};
        ArrayAdapter<String> adapterDiff = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, difficulties);
        spinnerDiff.setAdapter(adapterDiff);

        ArrayAdapter<Disciplina> adapterDisc = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listDisciplinas);
        adapterDisc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDisc.setAdapter(adapterDisc);

        if (itemParaEditar != null) {
            etTitle.setText(itemParaEditar.getTitulo());
            etNotes.setText(itemParaEditar.getNotas());
            if (itemParaEditar.getDataProva() != null) etDate.setText(dateFormat.format(itemParaEditar.getDataProva()));
            for (int i = 0; i < difficulties.length; i++) if (difficulties[i].startsWith(itemParaEditar.getDificuldade())) spinnerDiff.setSelection(i);
            for (int i = 0; i < listDisciplinas.size(); i++) if (listDisciplinas.get(i).getId() == itemParaEditar.getIdDisciplina()) spinnerDisc.setSelection(i);
        }

        builder.setPositiveButton(itemParaEditar == null ? "Adicionar Prova" : "Salvar Alterações", (dialog, which) -> {
            String titulo = etTitle.getText().toString();
            Disciplina disc = (Disciplina) spinnerDisc.getSelectedItem();
            String data = etDate.getText().toString();
            String diff = spinnerDiff.getSelectedItem().toString();
            String notas = etNotes.getText().toString();

            if (titulo.isEmpty() || disc == null || data.isEmpty()) {
                Toast.makeText(getContext(), "Título, Disciplina e Data são obrigatórios!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            int diasEstudo = 3;
            if (diff.contains("7")) diasEstudo = 7;
            else if (diff.contains("14")) diasEstudo = 14;
            else if (diff.contains("21")) diasEstudo = 21;

            salvarProva(itemParaEditar, titulo, disc.getId(), data, diff.split(" ")[0], diasEstudo, notas);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void salvarProva(Prova item, String titulo, int discId, String data, String diff, int dias, String notas) {
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = item == null ?
                    "INSERT INTO tb_prova (id_disciplina, id_usuario, titulo_prova, data_prova, dificuldade, dias_estudo, comentarios) VALUES (?, ?, ?, ?, ?, ?, ?)" :
                    "UPDATE tb_prova SET id_disciplina = ?, titulo_prova = ?, data_prova = ?, dificuldade = ?, dias_estudo = ?, comentarios = ? WHERE id_prova = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (item == null) {
                    stmt.setInt(1, discId);
                    stmt.setInt(2, sessionManager.getUserId());
                    stmt.setString(3, titulo);
                    stmt.setDate(4, new java.sql.Date(dateFormat.parse(data).getTime()));
                    stmt.setString(5, diff);
                    stmt.setInt(6, dias);
                    stmt.setString(7, notas);
                } else {
                    stmt.setInt(1, discId);
                    stmt.setString(2, titulo);
                    stmt.setDate(3, new java.sql.Date(dateFormat.parse(data).getTime()));
                    stmt.setString(4, diff);
                    stmt.setInt(5, dias);
                    stmt.setString(6, notas);
                    stmt.setInt(7, item.getId());
                }
                stmt.executeUpdate();
                carregarProvas();
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void showAddDisciplineDialog(Disciplina itemParaEditar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_discipline, null);
        builder.setView(dialogView);

        EditText etName = dialogView.findViewById(R.id.etDisciplineName);
        if (itemParaEditar != null) etName.setText(itemParaEditar.getNome());

        final String[] selectedColor = {itemParaEditar != null ? itemParaEditar.getCor() : "#9C27B0"};

        builder.setPositiveButton(itemParaEditar == null ? "Adicionar Disciplina" : "Salvar Alterações", (dialog, which) -> {
            String nome = etName.getText().toString();
            if (nome.isEmpty()) return;
            salvarDisciplina(itemParaEditar, nome, selectedColor[0]);
        });
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }

    private void salvarDisciplina(Disciplina item, String nome, String cor) {
        Connection conn = DatabaseConnection.conectar();
        if (conn != null) {
            String sql = item == null ?
                    "INSERT INTO tb_disciplina (id_usuario, nome_disciplina, cor) VALUES (?, ?, ?)" :
                    "UPDATE tb_disciplina SET nome_disciplina = ?, cor = ? WHERE id_disciplina = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (item == null) {
                    stmt.setInt(1, sessionManager.getUserId());
                    stmt.setString(2, nome);
                    stmt.setString(3, cor);
                } else {
                    stmt.setString(1, nome);
                    stmt.setString(2, cor);
                    stmt.setInt(3, item.getId());
                }
                stmt.executeUpdate();
                carregarDados(); // Recarregar tudo pois mudou o nome da disciplina
            } catch (SQLException e) {
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