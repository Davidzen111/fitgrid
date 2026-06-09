package com.example.fitgrid.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.fitgrid.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputEditText;

/**
 * BmiActivity - Kalkulator BMI (Body Mass Index)
 * Menghitung BMI berdasarkan berat dan tinggi badan
 */
public class BmiActivity extends AppCompatActivity {

    private Slider sliderHeight, sliderWeight;
    private TextView tvHeightValue, tvWeightValue;
    private TextView tvBmiResult, tvBmiCategory, tvBmiAdvice;
    private View viewBmiIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);

        setupToolbar();
        initViews();
        setupSliders();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_bmi);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Kalkulator BMI");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        sliderHeight = findViewById(R.id.slider_height);
        sliderWeight = findViewById(R.id.slider_weight);
        tvHeightValue = findViewById(R.id.tv_height_value);
        tvWeightValue = findViewById(R.id.tv_weight_value);
        tvBmiResult = findViewById(R.id.tv_bmi_result);
        tvBmiCategory = findViewById(R.id.tv_bmi_category);
        tvBmiAdvice = findViewById(R.id.tv_bmi_advice);
        viewBmiIndicator = findViewById(R.id.view_bmi_indicator);

        MaterialButton btnCalculate = findViewById(R.id.btn_calculate_bmi);
        btnCalculate.setOnClickListener(v -> calculateBmi());
    }

    private void setupSliders() {
        // Height: 100 - 220 cm, default 170
        sliderHeight.setValueFrom(100f);
        sliderHeight.setValueTo(220f);
        sliderHeight.setValue(170f);
        tvHeightValue.setText("170 cm");

        sliderHeight.addOnChangeListener((slider, value, fromUser) ->
                tvHeightValue.setText((int) value + " cm"));

        // Weight: 30 - 150 kg, default 70
        sliderWeight.setValueFrom(30f);
        sliderWeight.setValueTo(150f);
        sliderWeight.setValue(70f);
        tvWeightValue.setText("70 kg");

        sliderWeight.addOnChangeListener((slider, value, fromUser) ->
                tvWeightValue.setText((int) value + " kg"));
    }

    private void calculateBmi() {
        float heightCm = sliderHeight.getValue();
        float weightKg = sliderWeight.getValue();

        float heightM = heightCm / 100f;
        float bmi = weightKg / (heightM * heightM);

        // Tampilkan hasil
        tvBmiResult.setText(String.format("%.1f", bmi));

        // Kategori BMI (WHO)
        String category, advice, colorKey;
        if (bmi < 18.5) {
            category = "Berat Badan Kurang";
            advice = "Tingkatkan asupan kalori dan latihan kekuatan untuk membangun massa otot.";
            colorKey = "blue";
        } else if (bmi < 25) {
            category = "Normal ✓";
            advice = "Pertahankan pola makan sehat dan olahraga rutin untuk menjaga berat ideal.";
            colorKey = "green";
        } else if (bmi < 30) {
            category = "Kelebihan Berat";
            advice = "Kombinasikan latihan kardio dan diet seimbang untuk menurunkan berat badan.";
            colorKey = "orange";
        } else {
            category = "Obesitas";
            advice = "Konsultasikan dengan dokter dan mulai program olahraga bertahap dengan pantauan.";
            colorKey = "red";
        }

        tvBmiCategory.setText(category);
        tvBmiAdvice.setText(advice);

        // Warna indikator
        int colorRes;
        switch (colorKey) {
            case "blue": colorRes = R.color.bmi_underweight; break;
            case "orange": colorRes = R.color.bmi_overweight; break;
            case "red": colorRes = R.color.bmi_obese; break;
            default: colorRes = R.color.green_primary; break;
        }
        viewBmiIndicator.setBackgroundColor(getColor(colorRes));
        tvBmiCategory.setTextColor(getColor(colorRes));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}