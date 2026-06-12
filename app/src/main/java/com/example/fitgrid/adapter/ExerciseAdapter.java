package com.example.fitgrid.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.fitgrid.BuildConfig;
import com.example.fitgrid.R;
import com.example.fitgrid.listener.OnItemClickListener;
import com.example.fitgrid.model.ExerciseItem;

import java.util.ArrayList;
import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private List<ExerciseItem> items = new ArrayList<>();
    private final OnItemClickListener<ExerciseItem> listener;

    public ExerciseAdapter(OnItemClickListener<ExerciseItem> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseItem item = items.get(position);

        holder.tvName.setText(capitalize(item.getName()));
        holder.tvBodyPart.setText(capitalize(item.getBodyPart()));
        holder.tvTarget.setText("Target: " + capitalize(item.getTarget()));
        holder.tvEquipment.setText(capitalize(item.getEquipment()));

        loadImage(holder, item);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    // Logic untuk mem-bypass RapidAPI restriction via header injection di Glide
    private void loadImage(@NonNull ViewHolder holder, ExerciseItem item) {
        if (item.getId() == null || item.getId().isEmpty()) {
            holder.ivExercise.setImageResource(R.drawable.ic_exercise_placeholder);
            return;
        }

        String imageUrl = "https://exercisedb.p.rapidapi.com/image?exerciseId=" + item.getId() + "&resolution=180";

        GlideUrl glideUrl = new GlideUrl(imageUrl, new LazyHeaders.Builder()
                .addHeader("x-rapidapi-key", BuildConfig.RAPIDAPI_KEY)
                .addHeader("x-rapidapi-host", BuildConfig.RAPIDAPI_HOST)
                .build());

        Glide.with(holder.itemView.getContext())
                .load(glideUrl)
                .placeholder(R.drawable.ic_exercise_placeholder)
                .error(R.drawable.ic_exercise_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.ivExercise);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // Reset dan update total list
    public void setItems(List<ExerciseItem> newItems) {
        this.items = (newItems != null) ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    // Append data baru (Pagination/Lazy Loading)
    public void addItems(List<ExerciseItem> moreItems) {
        if (moreItems == null || moreItems.isEmpty()) return;
        int start = this.items.size();
        this.items.addAll(moreItems);
        notifyItemRangeInserted(start, moreItems.size());
    }

    public void clearItems() {
        int size = this.items.size();
        this.items.clear();
        notifyItemRangeRemoved(0, size);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "-";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivExercise;
        TextView tvName, tvBodyPart, tvTarget, tvEquipment;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivExercise = itemView.findViewById(R.id.iv_exercise);
            tvName = itemView.findViewById(R.id.tv_name);
            tvBodyPart = itemView.findViewById(R.id.tv_body_part);
            tvTarget = itemView.findViewById(R.id.tv_target);
            tvEquipment = itemView.findViewById(R.id.tv_equipment);
        }
    }
}