package com.example.fitgrid.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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

        // Nama exercise
        String name = item.getName();
        if (name != null && !name.isEmpty())
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        holder.tvName.setText(name);
        holder.tvBodyPart.setText(capitalize(item.getBodyPart()));
        holder.tvTarget.setText("Target: " + capitalize(item.getTarget()));
        holder.tvEquipment.setText(capitalize(item.getEquipment()));

        // Load gambar dari wger (HTTPS PNG)
        String imgUrl = item.getGifUrl();
        Log.d("EXERCISE_IMG", "ID=" + item.getId() + " url=" + imgUrl);

        if (imgUrl != null && !imgUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_exercise_placeholder)
                    .error(R.drawable.ic_exercise_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.ivExercise);
        } else {
            holder.ivExercise.setImageResource(R.drawable.ic_exercise_placeholder);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void setItems(List<ExerciseItem> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addItems(List<ExerciseItem> more) {
        int start = items.size();
        items.addAll(more);
        notifyItemRangeInserted(start, more.size());
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
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
            ivExercise   = itemView.findViewById(R.id.iv_exercise);
            tvName       = itemView.findViewById(R.id.tv_name);
            tvBodyPart   = itemView.findViewById(R.id.tv_body_part);
            tvTarget     = itemView.findViewById(R.id.tv_target);
            tvEquipment  = itemView.findViewById(R.id.tv_equipment);
        }
    }
}
