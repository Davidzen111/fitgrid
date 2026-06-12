package com.example.fitgrid.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.fitgrid.activity.DetailActivity;
import com.example.fitgrid.adapter.ExerciseAdapter;
import com.example.fitgrid.database.DatabaseHelper;
import com.example.fitgrid.databinding.FragmentSavedBinding;
import com.example.fitgrid.model.ExerciseItem;
import com.example.fitgrid.utils.AppExecutor;

import java.util.List;

public class SavedFragment extends Fragment {

    private FragmentSavedBinding binding;
    private ExerciseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSavedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    // Inisialisasi list dan adapter grid 2 kolom
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ExerciseAdapter(item ->
                startActivity(DetailActivity.newIntent(requireContext(), item)));

        binding.rvSaved.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvSaved.setAdapter(adapter);
    }

    // Refresh list otomatis saat user kembali (back) dari DetailActivity
    @Override
    public void onResume() {
        super.onResume();
        loadSaved();
    }

    // Eksekusi query SQLite di background thread untuk mencegah UI freeze (ANR)
    private void loadSaved() {
        AppExecutor.getInstance().diskIO(() -> {
            List<ExerciseItem> saved = DatabaseHelper.getInstance(requireContext())
                    .getAllSaved();

            // Push update UI kembali ke Main Thread
            AppExecutor.getInstance().mainThread(() -> {
                adapter.setItems(saved);
                binding.tvEmpty.setVisibility(saved.isEmpty() ? View.VISIBLE : View.GONE);
                binding.rvSaved.setVisibility(saved.isEmpty() ? View.GONE : View.VISIBLE);
            });
        });
    }

    // Hapus binding saat view hancur untuk mencegah memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}