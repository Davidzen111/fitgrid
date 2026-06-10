package com.example.fitgrid.fragment;

import android.content.Intent;
import android.os.Bundle;
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

    private String selectedBodyPart = "all";
    private static final int PAGE_LIMIT = 20;

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

        selectedBodyPart = SharedPrefManager.getInstance(requireContext()).getLastBodyPart();

        setupCategoryRecyclerView();
        setupExerciseRecyclerView();
        setupRefreshButton();
        setupSearch();
        setupQuickActions();

        loadBodyParts();
        loadExercises(selectedBodyPart);
    }

    private void setupCategoryRecyclerView() {
        categoryAdapter = new CategoryAdapter(category -> {
            selectedBodyPart = category;
            binding.etSearch.setText("");
            SharedPrefManager.getInstance(requireContext()).setLastBodyPart(category);
            loadExercises(category);
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
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterExercises(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterExercises(String query) {
        if (query.isEmpty()) {
            exerciseAdapter.setItems(allExercises);
            return;
        }
        List<ExerciseItem> filtered = new ArrayList<>();
        for (ExerciseItem item : allExercises) {
            if (item.getName().toLowerCase().contains(query.toLowerCase())
                    || item.getTarget().toLowerCase().contains(query.toLowerCase())
                    || item.getBodyPart().toLowerCase().contains(query.toLowerCase())) {
                filtered.add(item);
            }
        }
        exerciseAdapter.setItems(filtered);
    }

    private void setupQuickActions() {
        binding.cardBmi.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), BmiActivity.class)));
        binding.cardLog.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), WorkoutLogActivity.class)));
    }

    private void loadBodyParts() {
        // === MODIFIKASI SEMENTARA: ISI DATA MOCK KATEGORI ===
        List<String> mockCategories = new ArrayList<>();
        mockCategories.add("all");
        mockCategories.add("chest");
        mockCategories.add("back");
        mockCategories.add("legs");
        mockCategories.add("shoulders");
        categoryAdapter.setCategories(mockCategories);

        /* KODE ASLI API DI-KOMEN SEMENTARA
        if (!NetworkUtil.isConnected(requireContext())) return;
        RetrofitInstance.getInstance().getApiService().getBodyPartList()
                .enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                        if (response.isSuccessful() && response.body() != null)
                            categoryAdapter.setCategories(response.body());
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {}
                });
        */
    }

    private void loadExercises(String bodyPart) {
        showLoading(true);
        hideError();

        // === MODIFIKASI SEMENTARA: ISI DATA MOCK EXERCISE ===
        AppExecutor.getInstance().mainThread(() -> {
            showLoading(false);
            allExercises = getMockExercises(bodyPart);
            exerciseAdapter.setItems(allExercises);
        });

        /* KODE ASLI API & CACHE DI-KOMEN SEMENTARA
        if (!NetworkUtil.isConnected(requireContext())) { loadFromCache(bodyPart); return; }

        Call<List<ExerciseItem>> call;
        if (bodyPart == null || bodyPart.equals("all"))
            call = RetrofitInstance.getInstance().getApiService().getExercises(PAGE_LIMIT, 0);
        else
            call = RetrofitInstance.getInstance().getApiService().getExercisesByBodyPart(bodyPart, PAGE_LIMIT, 0);

        call.enqueue(new Callback<List<ExerciseItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ExerciseItem>> c, @NonNull Response<List<ExerciseItem>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    allExercises = response.body();
                    exerciseAdapter.setItems(allExercises);
                    String filter = bodyPart == null ? "all" : bodyPart;
                    AppExecutor.getInstance().diskIO(() ->
                            DatabaseHelper.getInstance(requireContext()).cacheExercises(allExercises, filter));
                } else {
                    showError("Gagal memuat data. Kode: " + response.code());
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<ExerciseItem>> c, @NonNull Throwable t) {
                showLoading(false);
                showError("Koneksi gagal. Periksa jaringanmu.");
                loadFromCache(bodyPart);
            }
        });
        */
    }

    private void loadFromCache(String bodyPart) {
        String filter = (bodyPart == null || bodyPart.isEmpty()) ? "all" : bodyPart;
        AppExecutor.getInstance().diskIO(() -> {
            List<ExerciseItem> cached = DatabaseHelper.getInstance(requireContext()).getCachedExercises(filter);
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

    // === METHOD BARU: GENERATE DATA DUMMY & LOGIKA FILTER LOKAL ===
    private List<ExerciseItem> getMockExercises(String bodyPart) {
        List<ExerciseItem> list = new ArrayList<>();

        // Diubah menjadi 6 parameter: id, name, bodyPart, target, equipment, gifUrl
        list.add(new ExerciseItem("1", "Push Up", "chest", "pectorals", "body weight", "https://via.placeholder.com/150"));
        list.add(new ExerciseItem("2", "Bench Press", "chest", "pectorals", "barbell", "https://via.placeholder.com/150"));
        list.add(new ExerciseItem("3", "Pull Up", "back", "lats", "body weight", "https://via.placeholder.com/150"));
        list.add(new ExerciseItem("4", "Squat", "legs", "glutes", "body weight", "https://via.placeholder.com/150"));
        list.add(new ExerciseItem("5", "Shoulder Press", "shoulders", "deltoids", "dumbbell", "https://via.placeholder.com/150"));

        if (bodyPart == null || bodyPart.equalsIgnoreCase("all")) {
            return list;
        }

        List<ExerciseItem> filteredList = new ArrayList<>();
        for (ExerciseItem item : list) {
            if (item.getBodyPart().equalsIgnoreCase(bodyPart)) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}