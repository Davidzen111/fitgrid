package com.example.fitgrid.listener;

/**
 * OnItemClickListener - Interface generik untuk klik item RecyclerView
 */
public interface OnItemClickListener<T> {
    void onItemClick(T item);
}