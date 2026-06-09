package com.example.fitgrid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitgrid.R;
import com.example.fitgrid.model.WorkoutLog;
import java.util.List;

/**
 * WorkoutLogAdapter - Adapter untuk daftar catatan latihan tersimpan
 */
public class WorkoutLogAdapter extends RecyclerView.Adapter<WorkoutLogAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(WorkoutLog log, int position);
    }

    private final Context context;
    private List<WorkoutLog> logs;
    private final OnDeleteClickListener deleteListener;

    public WorkoutLogAdapter(Context context, List<WorkoutLog> logs,
                             OnDeleteClickListener deleteListener) {
        this.context = context;
        this.logs = logs;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_workout_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WorkoutLog log = logs.get(position);

        holder.tvExerciseName.setText(log.getExerciseName());
        holder.tvDate.setText("📅 " + log.getDate());
        holder.tvCategory.setText(log.getCategory() != null ? log.getCategory() : "Umum");
        holder.tvSummary.setText(log.getSummary());

        // Durasi
        if (log.getDurationMinutes() > 0) {
            holder.tvDuration.setVisibility(View.VISIBLE);
            holder.tvDuration.setText("⏱ " + log.getDurationMinutes() + " menit");
        } else {
            holder.tvDuration.setVisibility(View.GONE);
        }

        // Catatan
        if (log.getNotes() != null && !log.getNotes().isEmpty()) {
            holder.tvNotes.setVisibility(View.VISIBLE);
            holder.tvNotes.setText("📝 " + log.getNotes());
        } else {
            holder.tvNotes.setVisibility(View.GONE);
        }

        // Tombol hapus
        holder.btnDelete.setOnClickListener(v ->
                deleteListener.onDeleteClick(log, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return logs != null ? logs.size() : 0;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < logs.size()) {
            logs.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void updateData(List<WorkoutLog> newLogs) {
        this.logs = newLogs;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvExerciseName, tvDate, tvCategory, tvSummary, tvDuration, tvNotes;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExerciseName = itemView.findViewById(R.id.tv_log_exercise_name);
            tvDate = itemView.findViewById(R.id.tv_log_date);
            tvCategory = itemView.findViewById(R.id.tv_log_category);
            tvSummary = itemView.findViewById(R.id.tv_log_summary);
            tvDuration = itemView.findViewById(R.id.tv_log_duration);
            tvNotes = itemView.findViewById(R.id.tv_log_notes);
            btnDelete = itemView.findViewById(R.id.btn_delete_log);
        }
    }
}