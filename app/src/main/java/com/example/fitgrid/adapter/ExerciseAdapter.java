package com.example.fitgrid.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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

        // Capitalize nama exercise
        String name = item.getName();
        if (name != null && !name.isEmpty()) {
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        holder.tvName.setText(name);

        // Capitalize body part & target
        holder.tvBodyPart.setText(capitalize(item.getBodyPart()));
        holder.tvTarget.setText("Target: " + capitalize(item.getTarget()));
        holder.tvEquipment.setText(capitalize(item.getEquipment()));

        // MENCEGAH CRASH: Cek apakah URL valid atau null
        String gifUrlString = item.getGifUrl();
        Object modelToLoad; // Variabel penampung untuk dimasukkan ke Glide

        if (gifUrlString != null && !gifUrlString.trim().isEmpty()) {
            // Jika URL ada, gunakan GlideUrl dengan Header
            modelToLoad = new GlideUrl(gifUrlString, new LazyHeaders.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build());
        } else {
            // Jika URL kosong, biarkan null agar Glide langsung memunculkan gambar error/placeholder
            modelToLoad = null;
        }

        // Load GIF dengan Glide (Disempurnakan dengan Header, Listener & Null Check)
        Glide.with(holder.itemView.getContext())
                .load(modelToLoad) // Dihapus .asGif() dan menggunakan variabel penampung
                .placeholder(R.drawable.ic_exercise_placeholder)
                .error(R.drawable.ic_exercise_placeholder) // Ditambahkan fallback jika URL gambar gagal dimuat
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Diubah ke ALL agar caching animasi jauh lebih optimal
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Jika masih gagal, cek tab "Logcat" di bawah Android Studio, cari kata "GLIDE_ERROR"
                        Log.e("GLIDE_ERROR", "Gagal load gambar: " + item.getGifUrl(), e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.ivExercise);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<ExerciseItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    public void addItems(List<ExerciseItem> moreItems) {
        int start = this.items.size();
        this.items.addAll(moreItems);
        notifyItemRangeInserted(start, moreItems.size());
    }

    public void clearItems() {
        this.items.clear();
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
            ivExercise = itemView.findViewById(R.id.iv_exercise);
            tvName = itemView.findViewById(R.id.tv_name);
            tvBodyPart = itemView.findViewById(R.id.tv_body_part);
            tvTarget = itemView.findViewById(R.id.tv_target);
            tvEquipment = itemView.findViewById(R.id.tv_equipment);
        }
    }
}