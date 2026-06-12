package com.example.fitgrid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    // PERBAIKAN: Mengubah limit menjadi 1500 agar menarik semua data sekaligus
    private static final int PAGE_LIMIT = 1500;

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
        if (!NetworkUtil.isConnected(requireContext())) return;

        RetrofitInstance.getInstance().getApiService().getBodyPartList()
                .enqueue(new Callback<List<String>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<String>> call, @NonNull Response<List<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryAdapter.setCategories(response.body());
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<String>> call, @NonNull Throwable t) {
                        // Jika gagal, bisa log error di sini
                    }
                });
    }

    private void loadExercises(String bodyPart) {
        showLoading(true);
        hideError();

        if (!NetworkUtil.isConnected(requireContext())) {
            loadFromCache(bodyPart);
            return;
        }

        Call<List<ExerciseItem>> call;
        if (bodyPart == null || bodyPart.equals("all")) {
            call = RetrofitInstance.getInstance().getApiService().getExercises(PAGE_LIMIT, 0);
        } else {
            call = RetrofitInstance.getInstance().getApiService().getExercisesByBodyPart(bodyPart, PAGE_LIMIT, 0);
        }

        call.enqueue(new Callback<List<ExerciseItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ExerciseItem>> c,
                                   @NonNull Response<List<ExerciseItem>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {

                    for (ExerciseItem item : response.body()) {
                        Log.d("GIF_URL", "ID=" + item.getId()
                                + " | GIF=" + item.getGifUrl());
                    }

                    allExercises = response.body();

                    exerciseAdapter.setItems(allExercises);

                    String filter = bodyPart == null ? "all" : bodyPart;
                    AppExecutor.getInstance().diskIO(() ->
                            DatabaseHelper.getInstance(requireContext())
                                    .cacheExercises(allExercises, filter));
                } else {
                    showError("Failed to load data. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ExerciseItem>> c, @NonNull Throwable t) {
                showLoading(false);
                showError("Connection failed. Please check your network.");
                loadFromCache(bodyPart);
            }
        });
    }

    private void loadFromCache(String bodyPart) {
        String filter = (bodyPart == null || bodyPart.isEmpty()) ? "all" : bodyPart;
        AppExecutor.getInstance().diskIO(() -> {
            List<ExerciseItem> cached = DatabaseHelper.getInstance(requireContext()).getCachedExercises(filter);
            AppExecutor.getInstance().mainThread(() -> {
                showLoading(false);
                if (cached.isEmpty()) {
                    showError("No network and no cached data available.");
                } else {
                    allExercises = cached;
                    exerciseAdapter.setItems(allExercises);
                    Toast.makeText(requireContext(), "Showing offline data", Toast.LENGTH_SHORT).show();
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
        binding = null;
    }
}