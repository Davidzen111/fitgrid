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
        holder.tvCategory.setText(category);

        // Highlight kategori yang dipilih
        if (position == selectedPosition) {
            holder.tvCategory.setBackgroundResource(R.drawable.bg_category_selected);
            holder.tvCategory.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            holder.tvCategory.setBackgroundResource(R.drawable.bg_category_unselected);
            holder.tvCategory.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
        }

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
        // Tambahkan "All" di posisi pertama
        this.categories = new ArrayList<>();
        this.categories.add("all");
        this.categories.addAll(list);
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

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tv_category);
        }
    }
}
