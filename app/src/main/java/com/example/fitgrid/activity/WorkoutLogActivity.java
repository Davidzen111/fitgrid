package com.example.fitgrid.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.fitgrid.R;
import com.example.fitgrid.database.DatabaseHelper;
import com.example.fitgrid.model.WorkoutLog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * WorkoutLogActivity - Form untuk menambah catatan latihan baru
 * Data disimpan ke SQLite via DatabaseHelper
 */
public class WorkoutLogActivity extends AppCompatActivity {

    private TextInputEditText etExerciseName, etCategory, etSets, etReps,
            etWeight, etDuration, etNotes;
    private MaterialButton btnSave;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_log);

        setupToolbar();
        initViews();
        prefillFromIntent();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_log);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Catat Latihan");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initViews() {
        etExerciseName = findViewById(R.id.et_exercise_name);
        etCategory = findViewById(R.id.et_category);
        etSets = findViewById(R.id.et_sets);
        etReps = findViewById(R.id.et_reps);
        etWeight = findViewById(R.id.et_weight);
        etDuration = findViewById(R.id.et_duration);
        etNotes = findViewById(R.id.et_notes);
        btnSave = findViewById(R.id.btn_save_log);

        btnSave.setOnClickListener(v -> saveWorkoutLog());
    }

    /**
     * Isi form otomatis jika dibuka dari DetailActivity
     */
    private void prefillFromIntent() {
        String exerciseName = getIntent().getStringExtra("exercise_name");
        String exerciseCategory = getIntent().getStringExtra("exercise_category");

        if (exerciseName != null) {
            etExerciseName.setText(exerciseName);
        }
        if (exerciseCategory != null) {
            etCategory.setText(exerciseCategory);
        }
    }

    private void saveWorkoutLog() {
        // Validasi input
        String name = getTextOrEmpty(etExerciseName);
        if (name.isEmpty()) {
            etExerciseName.setError("Nama latihan tidak boleh kosong");
            return;
        }

        String category = getTextOrEmpty(etCategory);
        if (category.isEmpty()) category = "Umum";

        int sets = parseIntOrDefault(getTextOrEmpty(etSets), 0);
        int reps = parseIntOrDefault(getTextOrEmpty(etReps), 0);
        float weight = parseFloatOrDefault(getTextOrEmpty(etWeight), 0f);
        int duration = parseIntOrDefault(getTextOrEmpty(etDuration), 0);
        String notes = getTextOrEmpty(etNotes);

        // Tanggal hari ini
        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                .format(new Date());

        WorkoutLog log = new WorkoutLog(name, category, sets, reps, weight, duration, notes, date);

        // Simpan di background thread
        executor.execute(() -> {
            long result = DatabaseHelper.getInstance(this).insertWorkoutLog(log);
            runOnUiThread(() -> {
                if (result > 0) {
                    Toast.makeText(this, "✅ Latihan berhasil dicatat!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Gagal menyimpan. Coba lagi.", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private String getTextOrEmpty(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return def; }
    }

    private float parseFloatOrDefault(String s, float def) {
        try { return Float.parseFloat(s); } catch (NumberFormatException e) { return def; }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}