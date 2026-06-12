package com.example.fitgrid.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitgrid.databinding.ActivityBmiBinding;

import java.util.Locale;

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

    // Eksekusi validasi form, konversi tinggi dari cm ke meter, dan kalkulasi rumus BMI statis
    private void setupCalculate() {
        binding.btnCalculate.setOnClickListener(v -> {
            String weightStr = binding.etWeight.getText().toString().trim();
            String heightStr = binding.etHeight.getText().toString().trim();

            if (TextUtils.isEmpty(weightStr) || TextUtils.isEmpty(heightStr)) {
                Toast.makeText(this, "Please enter your weight and height!", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double weight = Double.parseDouble(weightStr);
                double heightCm = Double.parseDouble(heightStr);

                if (weight <= 0 || heightCm <= 0) {
                    Toast.makeText(this, "Invalid values entered!", Toast.LENGTH_SHORT).show();
                    return;
                }

                double heightM = heightCm / 100.0;
                double bmi = weight / (heightM * heightM);

                String category;
                String advice;

                if (bmi < 18.5) {
                    category = "Underweight";
                    advice = "You need to increase your calorie intake and do strength training to build muscle mass.";
                } else if (bmi < 25.0) {
                    category = "Normal";
                    advice = "Your BMI is ideal! Maintain it with regular exercise and a balanced diet.";
                } else if (bmi < 30.0) {
                    category = "Overweight";
                    advice = "Try increasing your cardio activity and reducing excess calorie intake.";
                } else {
                    category = "Obese";
                    advice = "It is recommended to consult a doctor or nutritionist for a weight loss program.";
                }

                binding.tvBmiResult.setText(String.format(Locale.US, "%.1f", bmi));
                binding.tvBmiCategory.setText(category);
                binding.tvBmiAdvice.setText(advice);

                binding.cardResult.setVisibility(android.view.View.VISIBLE);

            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter valid numbers!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupReset() {
        binding.btnReset.setOnClickListener(v -> {
            binding.etWeight.setText("");
            binding.etHeight.setText("");
            binding.cardResult.setVisibility(android.view.View.GONE);

            binding.etWeight.requestFocus();
        });
    }
}