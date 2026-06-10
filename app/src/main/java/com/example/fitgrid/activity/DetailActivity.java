package com.example.fitgrid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.fitgrid.R;
import com.example.fitgrid.api.RetrofitInstance;
import com.example.fitgrid.database.DatabaseHelper;
import com.example.fitgrid.databinding.ActivityDetailBinding;
import com.example.fitgrid.model.ExerciseItem;
import com.example.fitgrid.utils.AppExecutor;
import com.example.fitgrid.utils.NetworkUtil;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_EXERCISE_ID    = "extra_exercise_id";
    public static final String EXTRA_EXERCISE_NAME  = "extra_exercise_name";
    public static final String EXTRA_BODY_PART      = "extra_body_part";
    public static final String EXTRA_EQUIPMENT      = "extra_equipment";
    public static final String EXTRA_GIF_URL        = "extra_gif_url";
    public static final String EXTRA_TARGET         = "extra_target";

    private ActivityDetailBinding binding;
    private ExerciseItem exercise;
    private boolean isSaved = false;

    public static Intent newIntent(Context context, ExerciseItem item) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_EXERCISE_ID,   item.getId());
        intent.putExtra(EXTRA_EXERCISE_NAME, item.getName());
        intent.putExtra(EXTRA_BODY_PART,     item.getBodyPart());
        intent.putExtra(EXTRA_EQUIPMENT,     item.getEquipment());
        intent.putExtra(EXTRA_GIF_URL,       item.getGifUrl());
        intent.putExtra(EXTRA_TARGET,        item.getTarget());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        exercise = extractFromIntent(getIntent());

        setupToolbar();
        populateUI();
        checkSavedStatus();
        setupFabSave();
        setupLogWorkoutButton();
        loadInstructions();
    }

    private ExerciseItem extractFromIntent(Intent intent) {
        ExerciseItem item = new ExerciseItem();
        item.setId(intent.getStringExtra(EXTRA_EXERCISE_ID));
        item.setName(intent.getStringExtra(EXTRA_EXERCISE_NAME));
        item.setBodyPart(intent.getStringExtra(EXTRA_BODY_PART));
        item.setEquipment(intent.getStringExtra(EXTRA_EQUIPMENT));
        item.setGifUrl(intent.getStringExtra(EXTRA_GIF_URL));
        item.setTarget(intent.getStringExtra(EXTRA_TARGET));
        return item;
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void populateUI() {
        String name = exercise.getName();
        if (name != null && !name.isEmpty())
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        binding.tvDetailName.setText(name);
        binding.tvDetailBodyPart.setText(capitalize(exercise.getBodyPart()));
        binding.tvDetailTarget.setText(capitalize(exercise.getTarget()));
        binding.tvDetailEquipment.setText(capitalize(exercise.getEquipment()));

        Glide.with(this)
                .asGif()
                .load(exercise.getGifUrl())
                .placeholder(R.drawable.ic_exercise_placeholder)
                .error(R.drawable.ic_exercise_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.ivDetailExercise);
    }

    private void loadInstructions() {
        if (!NetworkUtil.isConnected(this)) {
            binding.tvInstructions.setText("Hubungkan ke internet untuk melihat instruksi lengkap.");
            return;
        }
        binding.tvInstructions.setText("Memuat instruksi...");

        RetrofitInstance.getInstance().getApiService()
                .getExerciseById(exercise.getId())
                .enqueue(new Callback<ExerciseItem>() {
                    @Override
                    public void onResponse(Call<ExerciseItem> call, Response<ExerciseItem> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ExerciseItem full = response.body();
                            List<String> instructions = full.getInstructions();
                            List<String> secondary = full.getSecondaryMuscles();

                            StringBuilder sb = new StringBuilder();
                            if (secondary != null && !secondary.isEmpty()) {
                                sb.append("🎯 Otot sekunder: ").append(String.join(", ", secondary)).append("\n\n");
                            }
                            if (instructions != null && !instructions.isEmpty()) {
                                sb.append("📋 Langkah-langkah:\n\n");
                                for (int i = 0; i < instructions.size(); i++) {
                                    sb.append(i + 1).append(". ").append(instructions.get(i)).append("\n\n");
                                }
                            } else {
                                sb.append("Tidak ada instruksi tersedia.");
                            }
                            binding.tvInstructions.setText(sb.toString().trim());
                        }
                    }
                    @Override
                    public void onFailure(Call<ExerciseItem> call, Throwable t) {
                        binding.tvInstructions.setText("Gagal memuat instruksi. Periksa koneksimu.");
                    }
                });
    }

    private void setupLogWorkoutButton() {
        binding.btnLogWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutLogActivity.class);
            // Pre-fill nama exercise di WorkoutLogActivity
            intent.putExtra("prefill_name", exercise.getName());
            startActivity(intent);
        });
    }

    private void checkSavedStatus() {
        AppExecutor.getInstance().diskIO(() -> {
            isSaved = DatabaseHelper.getInstance(this).isSaved(exercise.getId());
            AppExecutor.getInstance().mainThread(this::updateFabIcon);
        });
    }

    private void updateFabIcon() {
        binding.fabSave.setImageResource(
                isSaved ? R.drawable.ic_bookmark_filled : R.drawable.ic_bookmark_outline);
    }

    private void setupFabSave() {
        binding.fabSave.setOnClickListener(v -> toggleSave());
    }

    private void toggleSave() {
        AppExecutor.getInstance().diskIO(() -> {
            DatabaseHelper db = DatabaseHelper.getInstance(this);
            if (isSaved) { db.removeExercise(exercise.getId()); isSaved = false; }
            else { db.saveExercise(exercise); isSaved = true; }
            AppExecutor.getInstance().mainThread(() -> {
                updateFabIcon();
                Toast.makeText(this, isSaved ? "Disimpan ke favorit!" : "Dihapus dari favorit", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "-";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
