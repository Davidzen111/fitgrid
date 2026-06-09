package com.example.fitgrid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.fitgrid.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

/**
 * DetailActivity - Menampilkan detail latihan
 * Menerima data via Intent dari HomeFragment
 */
public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setupToolbar();
        displayExerciseData();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void displayExerciseData() {
        Intent intent = getIntent();
        int exerciseId = intent.getIntExtra("exercise_id", 0);
        String name = intent.getStringExtra("exercise_name");
        String description = intent.getStringExtra("exercise_description");
        String category = intent.getStringExtra("exercise_category");
        String muscles = intent.getStringExtra("exercise_muscles");
        String equipment = intent.getStringExtra("exercise_equipment");

        // Set judul toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(name);
        }

        // Set konten
        TextView tvName = findViewById(R.id.tv_detail_name);
        TextView tvCategory = findViewById(R.id.tv_detail_category);
        TextView tvDescription = findViewById(R.id.tv_detail_description);
        TextView tvMuscles = findViewById(R.id.tv_detail_muscles);
        TextView tvEquipment = findViewById(R.id.tv_detail_equipment);
        TextView tvId = findViewById(R.id.tv_detail_id);

        tvName.setText(name);
        tvCategory.setText(category);
        tvId.setText("ID Latihan: #" + exerciseId);

        // Deskripsi
        if (description != null && !description.isEmpty()) {
            tvDescription.setText(description);
        } else {
            tvDescription.setText("Tidak ada deskripsi tersedia untuk latihan ini. " +
                    "Latihan ini berfokus pada penguatan otot-otot tubuh secara efektif.");
        }

        tvMuscles.setText(muscles != null ? muscles : "Berbagai otot");
        tvEquipment.setText(equipment != null ? equipment : "Tanpa alat khusus");

        // FAB tambah ke log
        ExtendedFloatingActionButton fabAddLog = findViewById(R.id.fab_add_to_log);
        fabAddLog.setOnClickListener(v -> {
            Intent logIntent = new Intent(this, WorkoutLogActivity.class);
            logIntent.putExtra("exercise_name", name);
            logIntent.putExtra("exercise_category", category);
            startActivity(logIntent);
        });
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