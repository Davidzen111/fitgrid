package com.example.fitgrid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgrid.R;

import java.util.ArrayList;
import java.util.List;
import com.example.fitgrid.model.WorkoutLog;

public class WorkoutLogAdapter extends RecyclerView.Adapter<WorkoutLogAdapter.ViewHolder> {

    public interface OnDeleteListener {
        void onDelete(WorkoutLog log);
    }

    private List<WorkoutLog> items = new ArrayList<>();
    private final OnDeleteListener deleteListener;

    public WorkoutLogAdapter(OnDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_log, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutLog log = items.get(position);
        holder.tvName.setText(capitalize(log.getExerciseName()));
        holder.tvSetsReps.setText(log.getSets() + " sets × " + log.getReps() + " reps");
        holder.tvDate.setText(log.getDate());
        if (log.getNote() != null && !log.getNote().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText(log.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(log));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void setItems(List<WorkoutLog> list) {
        this.items = list;
        notifyDataSetChanged();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSetsReps, tvDate, tvNote;
        ImageButton btnDelete;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_log_name);
            tvSetsReps = itemView.findViewById(R.id.tv_log_sets_reps);
            tvDate = itemView.findViewById(R.id.tv_log_date);
            tvNote = itemView.findViewById(R.id.tv_log_note);
            btnDelete = itemView.findViewById(R.id.btn_delete_log);
        }
    }
}
