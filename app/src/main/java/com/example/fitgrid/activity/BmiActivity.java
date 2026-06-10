package com.example.fitgrid.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitgrid.databinding.ActivityBmiBinding;

public class BmiActivity extends AppCompatActivity {

    private ActivityBmiBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBmiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupCalculate();
        setupReset();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("BMI Calculator");
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupCalculate() {
        binding.btnCalculate.setOnClickListener(v -> {
            String weightStr = binding.etWeight.getText().toString().trim();
            String heightStr = binding.etHeight.getText().toString().trim();

            if (TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr)) {
                Toast.makeText(this, "Isi berat dan tinggi badan dulu!", Toast.LENGTH_SHORT).show();
                return;
            }

            double weight = Double.parseDouble(weightStr);
            double heightCm = Double.parseDouble(heightStr);

            if (weight <= 0 || heightCm <= 0) {
                Toast.makeText(this, "Nilai tidak valid!", Toast.LENGTH_SHORT).show();
                return;
            }

            double heightM = heightCm / 100.0;
            double bmi = weight / (heightM * heightM);

            String category;
            int colorRes;
            String advice;

            if (bmi < 18.5) {
                category = "Underweight";
                colorRes = com.google.android.material.R.color.design_default_color_secondary;
                advice = "Kamu perlu menambah asupan kalori dan latihan kekuatan untuk menambah massa otot.";
            } else if (bmi < 25.0) {
                category = "Normal";
                colorRes = com.google.android.material.R.color.design_default_color_primary;
                advice = "BMI kamu ideal! Pertahankan dengan olahraga rutin dan pola makan seimbang.";
            } else if (bmi < 30.0) {
                category = "Overweight";
                colorRes = com.google.android.material.R.color.design_default_color_error;
                advice = "Coba tingkatkan aktivitas kardio dan kurangi asupan kalori berlebih.";
            } else {
                category = "Obese";
                colorRes = com.google.android.material.R.color.design_default_color_error;
                advice = "Disarankan konsultasi dengan dokter atau ahli gizi untuk program penurunan berat badan.";
            }

            binding.tvBmiResult.setText(String.format("%.1f", bmi));
            binding.tvBmiCategory.setText(category);
            binding.tvBmiAdvice.setText(advice);
            binding.cardResult.setVisibility(android.view.View.VISIBLE);
        });
    }

    private void setupReset() {
        binding.btnReset.setOnClickListener(v -> {
            binding.etWeight.setText("");
            binding.etHeight.setText("");
            binding.cardResult.setVisibility(android.view.View.GONE);
        });
    }
}
