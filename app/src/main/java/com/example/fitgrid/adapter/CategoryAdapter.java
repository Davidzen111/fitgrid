package com.example.fitgrid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitgrid.R;
import com.example.fitgrid.listener.OnItemClickListener;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private List<String> categories = new ArrayList<>();
    private int selectedPosition = 0;
    private final OnItemClickListener<String> listener;

    public CategoryAdapter(OnItemClickListener<String> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);

        holder.tvCategory.setText(category.substring(0, 1).toUpperCase() + category.substring(1));

        // Styling dinamis via code untuk minimalisasi overhead XML drawable
        if (position == selectedPosition) {
            holder.cardCategory.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.fitgrid_blue));
            holder.cardCategory.setStrokeWidth(0);
            holder.tvCategory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            holder.cardCategory.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.transparent));
            holder.cardCategory.setStrokeWidth(3);
            holder.cardCategory.setStrokeColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.surface_variant));
            holder.tvCategory.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
        }

        // Kalkulasi asinkron untuk dynamic pill-shape radius berdasarkan tinggi view aktual
        holder.cardCategory.post(() -> {
            int height = holder.cardCategory.getHeight();
            if (height > 0) {
                holder.cardCategory.setRadius(height / 2f);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);

            listener.onItemClick(category);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void setCategories(List<String> list) {
        this.categories.clear();

        if (list != null) {
            for (String item : list) {
                if (!this.categories.contains(item)) {
                    this.categories.add(item);
                }
            }
        }

        // Proteksi struktur data API: pastikan 'all' selalu menjadi base root filter di index 0
        if (this.categories.isEmpty() || !this.categories.get(0).equalsIgnoreCase("all")) {
            if(this.categories.contains("all")) this.categories.remove("all");
            this.categories.add(0, "all");
        }

        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int prev = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(prev);
        notifyItemChanged(selectedPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        MaterialCardView cardCategory;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
            cardCategory = (MaterialCardView) tvCategory.getParent();
            tvCategory.setBackground(null);
        }
    }
}