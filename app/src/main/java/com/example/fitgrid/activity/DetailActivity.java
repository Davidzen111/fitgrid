package com.example.fitgrid.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.fitgrid.BuildConfig;
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

    public static final String EXTRA_EXERCISE = "extra_exercise";

    private ActivityDetailBinding binding;
    private ExerciseItem exercise;
    private boolean isSaved = false;

    // Factory method pembungkus Intent. Memaksa siapapun yang memanggil Activity ini wajib membawa data objek ExerciseItem.
    public static Intent newIntent(Context context, ExerciseItem item) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_EXERCISE, item);
        return intent;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Tangkap payload Intent dari Fragment. Cek versi SDK untuk handling fungsi serializable yang deprecated di Android 13 (Tiramisu).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            exercise = getIntent().getSerializableExtra(EXTRA_EXERCISE, ExerciseItem.class);
        } else {
            exercise = (ExerciseItem) getIntent().getSerializableExtra(EXTRA_EXERCISE);
        }

        // Fallback jika data gagal terlempar
        if (exercise == null) {
            Toast.makeText(this, "Error loading exercise details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        populateUI();
        checkSavedStatus();
        setupFabSave();
        setupLogWorkoutButton();
        loadInstructions();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
        binding.toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
    }

    // Render data UI dasar dan bypass limitasi render gambar RapidAPI
    private void populateUI() {
        String name = exercise.getName();
        if (name != null && !name.isEmpty())
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        binding.tvDetailName.setText(name);
        binding.tvDetailBodyPart.setText(capitalize(exercise.getBodyPart()));
        binding.tvDetailTarget.setText(capitalize(exercise.getTarget()));
        binding.tvDetailEquipment.setText(capitalize(exercise.getEquipment()));

        // Suntik API Key manual ke dalam header request Glide menggunakan LazyHeaders agar akses gambar (GIF) tidak ditolak (Error 403).
        if (exercise.getId() != null && !exercise.getId().isEmpty()) {
            String imageUrl = "https://exercisedb.p.rapidapi.com/image?exerciseId=" + exercise.getId() + "&resolution=180";

            GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                    .addHeader("x-rapidapi-key", BuildConfig.RAPIDAPI_KEY)
                    .addHeader("x-rapidapi-host", BuildConfig.RAPIDAPI_HOST)
                    .build());

            Glide.with(this)
                    .load(glideUrl)
                    .placeholder(R.drawable.ic_exercise_placeholder)
                    .error(R.drawable.ic_exercise_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.ivDetailExercise);
        } else {
            binding.ivDetailExercise.setImageResource(R.drawable.ic_exercise_placeholder);
        }
    }

    // Hit API endpoint spesifik berdasarkan ID untuk melengkapi data yang belum didapat dari halaman sebelumnya (instruksi & otot sekunder).
    private void loadInstructions() {
        if (!NetworkUtil.isConnected(this)) {
            binding.tvInstructions.setText("Connect to the internet to view full instructions.");
            return;
        }
        binding.tvInstructions.setText("Loading instructions...");

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
                                sb.append("🎯 Secondary muscles: ").append(TextUtils.join(", ", secondary)).append("\n\n");
                            }
                            if (instructions != null && !instructions.isEmpty()) {
                                sb.append("📋 Execution steps:\n\n");
                                for (int i = 0; i < instructions.size(); i++) {
                                    sb.append(i + 1).append(". ").append(instructions.get(i)).append("\n\n");
                                }
                            } else {
                                sb.append("No instructions available.");
                            }
                            binding.tvInstructions.setText(sb.toString().trim());
                        }
                    }
                    @Override
                    public void onFailure(Call<ExerciseItem> call, Throwable t) {
                        binding.tvInstructions.setText("Failed to load instructions. Please check your connection.");
                    }
                });
    }

    // Transisi ke WorkoutLogActivity sekaligus melempar data nama olahraga sebagai parameter 'prefill_name' agar user tidak perlu ngetik manual.
    private void setupLogWorkoutButton() {
        binding.btnLogWorkout.setOnClickListener(v -> {
            Intent intent = new Intent(this, WorkoutLogActivity.class);
            intent.putExtra("prefill_name", exercise.getName());
            startActivity(intent);
        });
    }

    // Verifikasi apakah item ini sudah ada di database lokal saat halaman pertama kali dibuka
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

    // Eksekusi aksi Insert/Delete bookmark ke SQLite menggunakan background thread
    private void toggleSave() {
        AppExecutor.getInstance().diskIO(() -> {
            DatabaseHelper db = DatabaseHelper.getInstance(this);
            if (isSaved) { db.removeExercise(exercise.getId()); isSaved = false; }
            else { db.saveExercise(exercise); isSaved = true; }

            AppExecutor.getInstance().mainThread(() -> {
                updateFabIcon();
                Toast.makeText(this, isSaved ? "Saved to favorites!" : "Removed from favorites", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "-";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}