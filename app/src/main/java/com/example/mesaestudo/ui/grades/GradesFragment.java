package com.example.mesaestudo.ui.grades;

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

public class GradesFragment extends Fragment {

    private Button btnAddGrade;

    public GradesFragment() {
        // Required empty public constructor
    }

    public static GradesFragment newInstance(String param1, String param2) {
        GradesFragment fragment = new GradesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grades, container, false);

        btnAddGrade = view.findViewById(R.id.button4);
        btnAddGrade.setOnClickListener(v -> showAddGradeDialog());

        return view;
    }

    private void showAddGradeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_grade, null);
        builder.setView(dialogView);

        // Spinner Disciplina (Placeholder)
        Spinner spinnerGradeDiscipline = dialogView.findViewById(R.id.spinnerGradeDiscipline);
        List<String> disciplines = new ArrayList<>();
        disciplines.add("Selecione uma disciplina...");
        ArrayAdapter<String> adapterDisc = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, disciplines);
        adapterDisc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGradeDiscipline.setAdapter(adapterDisc);

        EditText etGradeDate = dialogView.findViewById(R.id.etGradeDate);
        etGradeDate.setOnClickListener(v -> showDatePicker(etGradeDate));

        builder.setPositiveButton("Adicionar Nota", (dialog, which) -> {
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