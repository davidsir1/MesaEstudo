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
import android.widget.Spinner;

import com.example.mesaestudo.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlanFragment extends Fragment {

    private Button btnAddWork, btnAddExam, btnAddDiscipline;

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        btnAddWork = view.findViewById(R.id.btnAddWork);
        btnAddExam = view.findViewById(R.id.btnAddExam);
        btnAddDiscipline = view.findViewById(R.id.btnAddDiscipline);

        btnAddWork.setOnClickListener(v -> showAddWorkDialog());
        btnAddExam.setOnClickListener(v -> showAddExamDialog());
        btnAddDiscipline.setOnClickListener(v -> showAddDisciplineDialog());

        return view;
    }

    private void showAddWorkDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_work, null);
        builder.setView(dialogView);

        Spinner spinnerWorkType = dialogView.findViewById(R.id.spinnerWorkType);
        String[] types = {"Redação", "Lista de Exercícios", "Laboratório", "Leitura", "Prova", "Projeto", "Teste", "Outros"};
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, types);
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkType.setAdapter(adapterType);

        // Spinner Disciplina (Placeholder por enquanto)
        Spinner spinnerWorkDiscipline = dialogView.findViewById(R.id.spinnerWorkDiscipline);
        List<String> disciplines = new ArrayList<>();
        disciplines.add("Selecione uma disciplina...");
        ArrayAdapter<String> adapterDisc = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, disciplines);
        adapterDisc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerWorkDiscipline.setAdapter(adapterDisc);

        EditText etWorkDueDate = dialogView.findViewById(R.id.etWorkDueDate);
        etWorkDueDate.setOnClickListener(v -> showDatePicker(etWorkDueDate));

        builder.setPositiveButton("Adicionar Trabalho", (dialog, which) -> {
            // Lógica de adicionar será implementada futuramente
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showAddExamDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_exam, null);
        builder.setView(dialogView);

        Spinner spinnerExamDifficulty = dialogView.findViewById(R.id.spinnerExamDifficulty);
        String[] difficulties = {
                "Fácil (Estudar 3 dias antes)",
                "Médio (Estudar 7 dias antes)",
                "Difícil (Estudar 14 dias antes)",
                "Extremo (Estudar 21 dias antes)"
        };
        ArrayAdapter<String> adapterDiff = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, difficulties);
        adapterDiff.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExamDifficulty.setAdapter(adapterDiff);

        // Spinner Disciplina (Placeholder por enquanto)
        Spinner spinnerExamDiscipline = dialogView.findViewById(R.id.spinnerExamDiscipline);
        List<String> disciplines = new ArrayList<>();
        disciplines.add("Selecione uma disciplina...");
        ArrayAdapter<String> adapterDisc = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, disciplines);
        adapterDisc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExamDiscipline.setAdapter(adapterDisc);

        EditText etExamDate = dialogView.findViewById(R.id.etExamDate);
        etExamDate.setOnClickListener(v -> showDatePicker(etExamDate));

        builder.setPositiveButton("Adicionar Prova", (dialog, which) -> {
            // Lógica de adicionar será implementada futuramente
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void showAddDisciplineDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_discipline, null);
        builder.setView(dialogView);

        builder.setPositiveButton("Adicionar Disciplina", (dialog, which) -> {
            // Lógica de adicionar será implementada futuramente
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
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