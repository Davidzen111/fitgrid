package com.example.fitgrid.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.fitgrid.R;
import com.example.fitgrid.activity.BmiActivity;
import com.example.fitgrid.activity.MainActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * SettingsFragment - Pengaturan aplikasi
 * Fitur: Dark/Light theme, BMI Calculator, info app
 */
public class SettingsFragment extends Fragment {

    private SwitchMaterial switchDarkMode;
    private MaterialCardView cardBmi, cardAbout;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = requireActivity().getSharedPreferences(
                MainActivity.PREFS_NAME, AppCompatActivity.MODE_PRIVATE);

        initViews(view);
        loadCurrentSettings();
    }

    private void initViews(View view) {
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);
        cardBmi = view.findViewById(R.id.card_bmi);
        cardAbout = view.findViewById(R.id.card_about);

        // Dark mode toggle
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(MainActivity.KEY_DARK_MODE, isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Buka BMI Calculator
        cardBmi.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BmiActivity.class);
            startActivity(intent);
        });

        // Info tentang aplikasi
        cardAbout.setOnClickListener(v -> showAboutDialog());
    }

    private void loadCurrentSettings() {
        boolean isDarkMode = prefs.getBoolean(MainActivity.KEY_DARK_MODE, false);
        switchDarkMode.setChecked(isDarkMode);
    }

    private void showAboutDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Tentang FitGrid")
                .setMessage("FitGrid v1.0\n\n" +
                        "Aplikasi panduan latihan kebugaran yang membantu kamu mencapai target kesehatan.\n\n" +
                        "Data latihan dari: wger REST API\n" +
                        "Dibuat dengan ❤️ untuk Final Lab Mobile 2026")
                .setPositiveButton("OK", null)
                .show();
    }
}