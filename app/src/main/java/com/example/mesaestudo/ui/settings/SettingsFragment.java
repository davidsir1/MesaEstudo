package com.example.mesaestudo.ui.settings;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.example.mesaestudo.R;
import com.example.mesaestudo.databinding.FragmentSettingsBinding;
import com.example.mesaestudo.ui.startscreen.RegisterActivity;
import com.example.mesaestudo.utils.SessionManager;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SessionManager sessionManager;
    private static final String CHANNEL_ID = "study_reminders";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sessionManager = new SessionManager(getContext());
        createNotificationChannel();

        setupAccountCard();
        setupDataCard();
        setupRemindersCard();

        return root;
    }

    private void setupAccountCard() {
        if (!sessionManager.isLoggedIn()) {
            binding.tvAccountName.setText("Convidado");
            binding.tvAccountStatus.setText("Offline");
            binding.btnSignInSync.setVisibility(View.VISIBLE);
            binding.tvSyncConnection.setText("Conexão: Offline (somente local)");
            binding.tvSyncQueue.setText("Fila pendente: Requer login");
            binding.tvLastSync.setText("Último envio bem-sucedido: nunca");
        } else {
            binding.tvAccountName.setText("Sincronizado");
            binding.tvAccountStatus.setText("Local e Nuvem - conectado");
            binding.btnSignInSync.setVisibility(View.GONE);
            binding.tvSyncConnection.setText("Conexão: Online (Sincronizado)");
            binding.tvSyncQueue.setText("Fila pendente: Tudo sincronizado");
            binding.tvLastSync.setText("Último envio bem-sucedido: agora");
        }

        binding.btnSignInSync.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RegisterActivity.class);
            startActivity(intent);
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
            if (isChecked) {
                showNotification();
            }
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Lembretes de Estudo";
            String description = "Notificações para trabalhos e provas";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = requireContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_settings_black_24dp)
                .setContentTitle("Lembrete de Estudo")
                .setContentText("Você tem um trabalho ou prova em breve!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
            return;
        }
        notificationManager.notify(1, builder.build());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}