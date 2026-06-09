package com.example.fitgrid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitgrid.R;
import com.example.fitgrid.listener.OnItemClickListener;
import com.example.fitgrid.model.ExerciseItem;
import java.util.List;

/**
 * ExerciseAdapter - Adapter untuk daftar latihan (grid/list)
 */
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private final Context context;
    private List<ExerciseItem.Exercise> exercises;
    private final OnItemClickListener<ExerciseItem.Exercise> listener;

    // Warna gradien per kategori (index)
    private static final int[] CATEGORY_COLORS = {
            R.color.cat_chest, R.color.cat_back, R.color.cat_legs,
            R.color.cat_arms, R.color.cat_shoulders, R.color.cat_abs,
            R.color.cat_calves, R.color.green_primary
    };

    // Emoji per kategori
    private static final String[] EXERCISE_EMOJIS = {
            "🏋️", "💪", "🦵", "🤸", "🦾", "🔥", "🏃", "⚡"
    };

    public ExerciseAdapter(Context context, List<ExerciseItem.Exercise> exercises,
                           OnItemClickListener<ExerciseItem.Exercise> listener) {
        this.context = context;
        this.exercises = exercises;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseItem.Exercise exercise = exercises.get(position);

        holder.tvName.setText(exercise.getCleanName());
        holder.tvCategory.setText(exercise.getCategoryName());
        holder.tvMuscle.setText("💪 " + exercise.getMuscleNames());
        holder.tvEquipment.setText("🔧 " + exercise.getEquipmentNames());

        // Set emoji berdasarkan index
        holder.tvEmoji.setText(EXERCISE_EMOJIS[position % EXERCISE_EMOJIS.length]);

        // Set warna aksen per kategori
        int colorRes = CATEGORY_COLORS[position % CATEGORY_COLORS.length];
        holder.viewAccent.setBackgroundColor(context.getColor(colorRes));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(exercise));
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    public void updateData(List<ExerciseItem.Exercise> newExercises) {
        this.exercises = newExercises;
        notifyDataSetChanged();
    }

    public void addData(List<ExerciseItem.Exercise> moreExercises) {
        if (moreExercises == null || moreExercises.isEmpty()) return;
        int startPos = exercises != null ? exercises.size() : 0;
        if (exercises != null) exercises.addAll(moreExercises);
        notifyItemRangeInserted(startPos, moreExercises.size());
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        View viewAccent;
        TextView tvEmoji, tvName, tvCategory, tvMuscle, tvEquipment;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_exercise);
            viewAccent = itemView.findViewById(R.id.view_accent);
            tvEmoji = itemView.findViewById(R.id.tv_exercise_emoji);
            tvName = itemView.findViewById(R.id.tv_exercise_name);
            tvCategory = itemView.findViewById(R.id.tv_exercise_category);
            tvMuscle = itemView.findViewById(R.id.tv_exercise_muscle);
            tvEquipment = itemView.findViewById(R.id.tv_exercise_equipment);
        }
    }
}