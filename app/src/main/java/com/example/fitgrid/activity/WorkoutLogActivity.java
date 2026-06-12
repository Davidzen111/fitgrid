package com.example.fitgrid.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgrid.adapter.WorkoutLogAdapter;
import com.example.fitgrid.database.DatabaseHelper;
import com.example.fitgrid.databinding.ActivityWorkoutLogBinding;
import com.example.fitgrid.utils.AppExecutor;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import com.example.fitgrid.model.WorkoutLog;

public class WorkoutLogActivity extends AppCompatActivity {

    private ActivityWorkoutLogBinding binding;
    private WorkoutLogAdapter adapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWorkoutLogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set tanggal hari ini sebagai default
        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Calendar.getInstance().getTime());
        binding.tvSelectedDate.setText(selectedDate);

        // Menangkap "prefill_name" jika Activity ini dibuka dari halaman DetailActivity
        String prefillName = getIntent().getStringExtra("prefill_name");
        if (prefillName != null && !prefillName.isEmpty()) {
            binding.etExerciseName.setText(prefillName);
        }

        setupToolbar();
        setupRecyclerView();
        setupAddButton();
        setupDatePicker();
        loadLogs();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Workout Log");
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        adapter = new WorkoutLogAdapter(log -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Log")
                    .setMessage("Are you sure you want to delete the log for " + log.getExerciseName() + "?")
                    .setPositiveButton("Delete", (d, w) -> deleteLog(log))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        binding.rvWorkoutLog.setLayoutManager(new LinearLayoutManager(this));
        binding.rvWorkoutLog.setAdapter(adapter);
    }

    private void setupDatePicker() {
        binding.tvSelectedDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, day);
                binding.tvSelectedDate.setText(selectedDate);
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupAddButton() {
        binding.btnAddLog.setOnClickListener(v -> {
            String name = binding.etExerciseName.getText().toString().trim();
            String setsStr = binding.etSets.getText().toString().trim();
            String repsStr = binding.etReps.getText().toString().trim();
            String note = binding.etNote.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(setsStr) || TextUtils.isEmpty(repsStr)) {
                Toast.makeText(this, "Name, sets, and reps are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Urutan parameter diperbaiki sesuai dengan model: note dulu, baru date
            WorkoutLog log = new WorkoutLog(
                    "", name,
                    Integer.parseInt(setsStr),
                    Integer.parseInt(repsStr),
                    note, selectedDate
            );

            AppExecutor.getInstance().diskIO(() -> {
                DatabaseHelper.getInstance(this).addWorkoutLog(log);
                AppExecutor.getInstance().mainThread(() -> {
                    // Hanya bersihkan sets, reps, dan notes agar user bisa log latihan yang sama lagi jika perlu
                    binding.etSets.setText("");
                    binding.etReps.setText("");
                    binding.etNote.setText("");
                    Toast.makeText(this, "Log added successfully!", Toast.LENGTH_SHORT).show();
                    loadLogs();
                });
            });
        });
    }

    private void loadLogs() {
        AppExecutor.getInstance().diskIO(() -> {
            List<WorkoutLog> logs = DatabaseHelper.getInstance(this).getAllWorkoutLogs();
            AppExecutor.getInstance().mainThread(() -> {
                adapter.setItems(logs);
                binding.tvLogEmpty.setVisibility(logs.isEmpty() ? View.VISIBLE : View.GONE);
                binding.tvTotalWorkouts.setText("Total: " + logs.size() + " sessions recorded");
            });
        });
    }

    private void deleteLog(WorkoutLog log) {
        AppExecutor.getInstance().diskIO(() -> {
            DatabaseHelper.getInstance(this).deleteWorkoutLog(log.getId());
            AppExecutor.getInstance().mainThread(() -> {
                Toast.makeText(this, "Log deleted", Toast.LENGTH_SHORT).show();
                loadLogs();
            });
        });
    }
}