package com.example.fitgrid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.fitgrid.R;
import com.example.fitgrid.listener.OnItemClickListener;
import com.example.fitgrid.model.ExerciseItem;
import java.util.List;

/**
 * CategoryAdapter - Adapter untuk menampilkan kategori latihan (chip/card horizontal)
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private final Context context;
    private List<ExerciseItem.Category> categories;
    private final OnItemClickListener<ExerciseItem.Category> listener;
    private int selectedPosition = 0; // "Semua" dipilih default

    // Ikon emoji per kategori (wger categories)
    private static final String[] CATEGORY_ICONS = {"💪", "🏋️", "🦵", "🤸", "🦾", "🔥", "🏃", "⚡"};

    public CategoryAdapter(Context context, List<ExerciseItem.Category> categories,
                           OnItemClickListener<ExerciseItem.Category> listener) {
        this.context = context;
        this.categories = categories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseItem.Category category = categories.get(position);

        // Set emoji icon berdasarkan posisi
        String icon = CATEGORY_ICONS[position % CATEGORY_ICONS.length];
        holder.tvIcon.setText(icon);
        holder.tvName.setText(category.name);

        // Highlight kategori yang dipilih
        boolean isSelected = (position == selectedPosition);
        holder.cardView.setCardBackgroundColor(
                context.getColor(isSelected ? R.color.green_primary : R.color.card_background));
        holder.tvName.setTextColor(
                context.getColor(isSelected ? R.color.white : R.color.text_primary));

        holder.itemView.setOnClickListener(v -> {
            int prevSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevSelected);
            notifyItemChanged(selectedPosition);
            listener.onItemClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    public void updateData(List<ExerciseItem.Category> newCategories) {
        this.categories = newCategories;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvIcon, tvName;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_category);
            tvIcon = itemView.findViewById(R.id.tv_category_icon);
            tvName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}