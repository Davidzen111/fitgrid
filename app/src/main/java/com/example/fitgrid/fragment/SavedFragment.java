package com.example.fitgrid.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitgrid.R;
import com.example.fitgrid.activity.WorkoutLogActivity;
import com.example.fitgrid.adapter.WorkoutLogAdapter;
import com.example.fitgrid.database.DatabaseHelper;
import com.example.fitgrid.model.WorkoutLog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * SavedFragment - Menampilkan riwayat catatan latihan dari SQLite
 */
public class SavedFragment extends Fragment {

    private RecyclerView rvWorkoutLog;
    private WorkoutLogAdapter adapter;
    private LinearLayout layoutEmpty;
    private TextView tvTotalWorkout, tvTotalCalories;
    private FloatingActionButton fabAdd;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_saved, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadWorkoutLogs();
    }

    private void initViews(View view) {
        rvWorkoutLog = view.findViewById(R.id.rv_workout_log);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        tvTotalWorkout = view.findViewById(R.id.tv_total_workout);
        tvTotalCalories = view.findViewById(R.id.tv_total_calories);
        fabAdd = view.findViewById(R.id.fab_add_log);

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), WorkoutLogActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        adapter = new WorkoutLogAdapter(requireContext(), null,
                (log, position) -> showDeleteDialog(log, position));
        rvWorkoutLog.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvWorkoutLog.setAdapter(adapter);
    }

    private void loadWorkoutLogs() {
        executor.execute(() -> {
            DatabaseHelper db = DatabaseHelper.getInstance(requireContext());
            List<WorkoutLog> logs = db.getAllWorkoutLogs();
            int totalWorkout = db.getTotalWorkoutCount();
            int totalCalories = db.getTotalCaloriesBurned();

            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                adapter.updateData(logs);

                // Statistik ringkasan
                tvTotalWorkout.setText(String.valueOf(totalWorkout));
                tvTotalCalories.setText(totalCalories + " kkal");

                // Tampilkan empty state jika tidak ada data
                if (logs.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvWorkoutLog.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvWorkoutLog.setVisibility(View.VISIBLE);
                }
            });
        });
    }

    private void showDeleteDialog(WorkoutLog log, int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Catatan")
                .setMessage("Hapus catatan latihan \"" + log.getExerciseName() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteLog(log, position))
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteLog(WorkoutLog log, int position) {
        executor.execute(() -> {
            DatabaseHelper.getInstance(requireContext()).deleteWorkoutLog(log.getId());
            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                adapter.removeItem(position);
                loadWorkoutLogs(); // refresh statistik
            });
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}