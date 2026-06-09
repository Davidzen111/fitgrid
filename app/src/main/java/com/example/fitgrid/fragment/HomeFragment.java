package com.example.fitgrid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fitgrid.activity.BmiActivity;
import com.example.fitgrid.activity.DetailActivity;
import com.example.fitgrid.activity.WorkoutLogActivity;
import com.example.fitgrid.adapter.CategoryAdapter;
import com.example.fitgrid.adapter.ExerciseAdapter;
import com.example.fitgrid.api.RetrofitInstance;
import com.example.fitgrid.database.DatabaseHelper;
import com.example.fitgrid.databinding.FragmentHomeBinding;
import com.example.fitgrid.model.ExerciseItem;
import com.example.fitgrid.utils.AppExecutor;
import com.example.fitgrid.utils.NetworkUtil;
import com.example.fitgrid.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ExerciseAdapter exerciseAdapter;
    private CategoryAdapter categoryAdapter;
    private List<ExerciseItem> allExercises = new ArrayList<>();

    // Menggunakan nama bodyPart secara langsung (String)
    private String selectedBodyPart = "all";
    private static final int PAGE_LIMIT = 100;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCategoryRecyclerView();
        setupExerciseRecyclerView();
        setupRefreshButton();
        setupSearch();
        setupQuickActions();

        loadCategories();
        loadExercises("all");
    }

    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter(category -> {
            binding.etSearch.setText("");
            SharedPrefManager.getInstance(requireContext()).setLastBodyPart(category);

            selectedBodyPart = category;
            loadExercises(selectedBodyPart);
        });
        binding.rvCategories.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupExerciseRecyclerView() {
        exerciseAdapter = new ExerciseAdapter(item ->
                startActivity(DetailActivity.newIntent(requireContext(), item)));
        binding.rvExercises.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvExercises.setAdapter(exerciseAdapter);
    }

    private void setupRefreshButton() {
        binding.btnRefresh.setOnClickListener(v -> loadExercises(selectedBodyPart));
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> filterExercises(s.toString().trim());
                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filterExercises(String query) {
        if (query.isEmpty()) {
            exerciseAdapter.setItems(allExercises);
            return;
        }

        // Filter lokal dulu (cepat)
        List<ExerciseItem> filtered = new ArrayList<>();
        for (ExerciseItem item : allExercises) {
            String name = item.getName() != null ? item.getName().toLowerCase() : "";
            String target = item.getTarget() != null ? item.getTarget().toLowerCase() : "";
            String bp = item.getBodyPart() != null ? item.getBodyPart().toLowerCase() : "";

            if (name.contains(query.toLowerCase()) || target.contains(query.toLowerCase())
                    || bp.contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        exerciseAdapter.setItems(filtered);

        // Jika tidak ada hasil lokal + ada internet → search API
        if (filtered.isEmpty() && NetworkUtil.isConnected(requireContext())) {
            showLoading(true);
            RetrofitInstance.getInstance().getApiService()
                    .searchExercises(query, PAGE_LIMIT, 0)
                    .enqueue(new Callback<List<ExerciseItem>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<ExerciseItem>> call,
                                               @NonNull Response<List<ExerciseItem>> response) {
                            showLoading(false);
                            if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                                exerciseAdapter.setItems(response.body());
                                Toast.makeText(requireContext(),
                                        "Ditemukan " + response.body().size() + " hasil", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(),
                                        "Exercise \"" + query + "\" tidak ditemukan", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<List<ExerciseItem>> call, @NonNull Throwable t) {
                            showLoading(false);
                        }
                    });
        }
    }

    private void setupQuickActions() {
        binding.cardBmi.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BmiActivity.class)));
        binding.cardLog.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), WorkoutLogActivity.class)));
    }

    // ===== LOAD KATEGORI =====
    private void loadCategories() {
        if (!NetworkUtil.isConnected(requireContext())) return;
        RetrofitInstance.getInstance().getApiService().getCategories()
                .enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<String>> call,
                                           @NonNull Response<List<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryAdapter.setCategories(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                        // Handle error jika perlu
                    }
                });
    }

    // ===== LOAD EXERCISES =====
    private void loadExercises(String bodyPart) {
        showLoading(true);
        hideError();
        if (!NetworkUtil.isConnected(requireContext())) {
            loadFromCache();
            return;
        }

        Call<List<ExerciseItem>> call;
        if (bodyPart.equals("all")) {
            call = RetrofitInstance.getInstance().getApiService().getExercises(PAGE_LIMIT, 0);
        } else {
            call = RetrofitInstance.getInstance().getApiService().getExercisesByCategory(bodyPart, PAGE_LIMIT, 0);
        }

        call.enqueue(new Callback<List<ExerciseItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ExerciseItem>> call,
                                   @NonNull Response<List<ExerciseItem>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    allExercises = response.body();
                    exerciseAdapter.setItems(allExercises);

                    // Cache ke SQLite
                    AppExecutor.getInstance().diskIO(() ->
                            DatabaseHelper.getInstance(requireContext())
                                    .cacheExercises(allExercises, bodyPart));
                } else {
                    showError("Gagal memuat data (kode: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ExerciseItem>> call, @NonNull Throwable t) {
                showLoading(false);
                showError("Koneksi gagal. Periksa jaringanmu.");
                loadFromCache();
            }
        });
    }

    private void loadFromCache() {
        AppExecutor.getInstance().diskIO(() -> {
            List<ExerciseItem> cached = DatabaseHelper.getInstance(requireContext())
                    .getCachedExercises(selectedBodyPart);
            AppExecutor.getInstance().mainThread(() -> {
                showLoading(false);
                if (cached.isEmpty()) {
                    showError("Tidak ada jaringan & belum ada cache tersimpan.");
                } else {
                    allExercises = cached;
                    exerciseAdapter.setItems(allExercises);
                    Toast.makeText(requireContext(), "Menampilkan data offline", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.rvExercises.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        binding.layoutError.setVisibility(View.VISIBLE);
        binding.tvError.setText(message);
        binding.rvExercises.setVisibility(View.GONE);
    }

    private void hideError() {
        binding.layoutError.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
        binding = null;
    }
}