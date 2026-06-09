package com.example.fitgrid.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitgrid.R;
import com.example.fitgrid.activity.DetailActivity;
import com.example.fitgrid.adapter.CategoryAdapter;
import com.example.fitgrid.adapter.ExerciseAdapter;
import com.example.fitgrid.api.RetrofitInstance;
import com.example.fitgrid.database.DatabaseHelper;
import com.example.fitgrid.model.ExerciseItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * HomeFragment - Menampilkan daftar latihan dari wger API
 * dengan filter kategori dan dukungan offline (cache SQLite)
 */
public class HomeFragment extends Fragment {

    private RecyclerView rvCategories, rvExercises;
    private CategoryAdapter categoryAdapter;
    private ExerciseAdapter exerciseAdapter;
    private CircularProgressIndicator progressBar;
    private LinearLayout layoutError;
    private MaterialButton btnRefresh;
    private TextView tvErrorMessage, tvExerciseCount;

    private List<ExerciseItem.Category> categoryList = new ArrayList<>();
    private List<ExerciseItem.Exercise> exerciseList = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private int selectedCategoryId = 0; // 0 = semua kategori
    private static final int PAGE_LIMIT = 20;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupAdapters();
        loadCategories();
        loadExercises();
    }

    private void initViews(View view) {
        rvCategories = view.findViewById(R.id.rv_categories);
        rvExercises = view.findViewById(R.id.rv_exercises);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutError = view.findViewById(R.id.layout_error);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        tvErrorMessage = view.findViewById(R.id.tv_error_message);
        tvExerciseCount = view.findViewById(R.id.tv_exercise_count);

        btnRefresh.setOnClickListener(v -> {
            layoutError.setVisibility(View.GONE);
            loadExercises();
        });
    }

    private void setupAdapters() {
        // Kategori - horizontal scroll
        categoryAdapter = new CategoryAdapter(requireContext(), categoryList, category -> {
            selectedCategoryId = category.id;
            exerciseList.clear();
            loadExercises();
        });
        rvCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvCategories.setAdapter(categoryAdapter);

        // Latihan - vertical list
        exerciseAdapter = new ExerciseAdapter(requireContext(), exerciseList, exercise -> {
            // Buka DetailActivity
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("exercise_id", exercise.id);
            intent.putExtra("exercise_name", exercise.getCleanName());
            intent.putExtra("exercise_description", exercise.getCleanDescription());
            intent.putExtra("exercise_category", exercise.getCategoryName());
            intent.putExtra("exercise_muscles", exercise.getMuscleNames());
            intent.putExtra("exercise_equipment", exercise.getEquipmentNames());
            startActivity(intent);
        });
        rvExercises.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvExercises.setAdapter(exerciseAdapter);
    }

    private void loadCategories() {
        if (!isNetworkAvailable()) return;

        RetrofitInstance.getApiService()
                .getCategories("json")
                .enqueue(new Callback<ExerciseItem.CategoryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<ExerciseItem.CategoryResponse> call,
                                           @NonNull Response<ExerciseItem.CategoryResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryList.clear();

                            // Tambah opsi "Semua"
                            ExerciseItem.Category allCategory = new ExerciseItem.Category();
                            allCategory.id = 0;
                            allCategory.name = "Semua";
                            categoryList.add(allCategory);

                            categoryList.addAll(response.body().results);
                            categoryAdapter.updateData(categoryList);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ExerciseItem.CategoryResponse> call,
                                          @NonNull Throwable t) {
                        // Silent fail untuk kategori
                    }
                });
    }

    private void loadExercises() {
        showLoading(true);

        if (!isNetworkAvailable()) {
            // Tampilkan data cache dari SQLite
            loadCachedExercises();
            return;
        }

        // Gunakan background thread dengan Executor
        executor.execute(() -> {
            Call<ExerciseItem.ExerciseResponse> call;

            if (selectedCategoryId == 0) {
                call = RetrofitInstance.getApiService()
                        .getExercises("json", 2, PAGE_LIMIT, 0);
            } else {
                call = RetrofitInstance.getApiService()
                        .getExercisesByCategory("json", 2, selectedCategoryId, PAGE_LIMIT, 0);
            }

            call.enqueue(new Callback<ExerciseItem.ExerciseResponse>() {
                @Override
                public void onResponse(@NonNull Call<ExerciseItem.ExerciseResponse> call,
                                       @NonNull Response<ExerciseItem.ExerciseResponse> response) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<ExerciseItem.Exercise> results = response.body().results;
                            exerciseList.clear();
                            exerciseList.addAll(results);
                            exerciseAdapter.updateData(exerciseList);

                            // Update counter
                            tvExerciseCount.setText(results.size() + " Latihan");

                            // Cache ke SQLite di background thread
                            executor.execute(() -> cacheExercisesToDb(results));

                        } else {
                            showError("Gagal memuat data. Coba lagi.");
                        }
                    });
                }

                @Override
                public void onFailure(@NonNull Call<ExerciseItem.ExerciseResponse> call,
                                      @NonNull Throwable t) {
                    if (getActivity() == null) return;
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showError("Tidak dapat terhubung ke server. Periksa koneksi internet.");
                    });
                }
            });
        });
    }

    private void loadCachedExercises() {
        executor.execute(() -> {
            DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
            List<ExerciseItem.Exercise> cached = db.getCachedExercises();

            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                showLoading(false);
                if (!cached.isEmpty()) {
                    exerciseList.clear();
                    exerciseList.addAll(cached);
                    exerciseAdapter.updateData(exerciseList);
                    tvExerciseCount.setText(cached.size() + " Latihan (Offline)");
                    Toast.makeText(requireContext(),
                            "Mode offline - menampilkan data tersimpan", Toast.LENGTH_SHORT).show();
                } else {
                    showError("Tidak ada koneksi internet dan tidak ada data tersimpan.");
                }
            });
        });
    }

    private void cacheExercisesToDb(List<ExerciseItem.Exercise> exercises) {
        DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
        for (ExerciseItem.Exercise ex : exercises) {
            db.cacheExercise(
                    ex.id,
                    ex.getCleanName(),
                    ex.getCategoryName(),
                    ex.getCleanDescription(),
                    ex.getMuscleNames(),
                    ex.getEquipmentNames()
            );
        }
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        rvExercises.setVisibility(show ? View.GONE : View.VISIBLE);
        layoutError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        rvExercises.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        tvErrorMessage.setText(message);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}