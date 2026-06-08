package com.example.fitgrid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.fitgrid.databinding.FragmentSettingsBinding;
import com.example.fitgrid.utils.SharedPrefManager;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set state awal switch sesuai preferensi tersimpan
        boolean isDark = SharedPrefManager.getInstance(requireContext()).isDarkMode();
        binding.switchDarkMode.setChecked(isDark);
        updateThemeLabel(isDark);

        // Listener toggle dark/light mode
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPrefManager.getInstance(requireContext()).setDarkMode(isChecked);
            updateThemeLabel(isChecked);

            // Terapkan tema — Activity akan recreate otomatis
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES
                              : AppCompatDelegate.MODE_NIGHT_NO);
        });
    }

    private void updateThemeLabel(boolean isDark) {
        binding.tvThemeLabel.setText(isDark ? "Dark Mode" : "Light Mode");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
