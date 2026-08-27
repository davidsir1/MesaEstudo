package com.example.mesaestudo.ui.settings;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mesaestudo.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupAccountCard();
        setupDataCard();
        setupRemindersCard();

        return root;
    }

    private void setupAccountCard() {
        // Simulação de estado (Convidado por padrão)
        boolean isGuest = true;

        if (isGuest) {
            binding.tvAccountName.setText("Convidado");
            binding.tvAccountStatus.setText("Somente local - Não conectado");
            binding.btnSignInSync.setVisibility(View.VISIBLE);
        } else {
            binding.tvAccountName.setText("Usuário Logado");
            binding.tvAccountStatus.setText("Local e Nuvem - conectado");
            binding.btnSignInSync.setVisibility(View.GONE);
        }

        binding.btnSignInSync.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Funcionalidade de Login em breve!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupDataCard() {
        binding.btnExportData.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Exportando dados para .json...", Toast.LENGTH_SHORT).show();
        });

        binding.btnDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Apagar Minha Conta")
                    .setMessage("Você realmente deseja apagar sua conta? Esta ação não pode ser desfeita.")
                    .setPositiveButton("Apagar", (dialog, which) -> {
                        Toast.makeText(getContext(), "Conta apagada com sucesso.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void setupRemindersCard() {
        binding.switchReminders.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = isChecked ? "Ligado" : "Desligado";
            Toast.makeText(getContext(), "Lembretes: " + status, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}