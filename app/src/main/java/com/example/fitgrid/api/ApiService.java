package com.example.fitgrid.api;

import com.example.fitgrid.model.ExerciseItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * ApiService - Interface Retrofit untuk wger REST API
 * Base URL: https://wger.de/api/v2/
 *
 * wger adalah API gratis, open source, tanpa API key untuk endpoint publik.
 * Dokumentasi: https://wger.de/en/software/api
 */
public interface ApiService {

    /**
     * Ambil daftar latihan (exercises)
     * language=2 = Bahasa Inggris
     * format=json
     * limit = jumlah data per halaman
     * offset = mulai dari data ke-n
     */
    @GET("exerciseinfo/")
    Call<ExerciseItem.ExerciseResponse> getExercises(
            @Query("format") String format,
            @Query("language") int language,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    /**
     * Ambil daftar kategori latihan
     * Contoh: Chest, Back, Legs, Arms, Shoulders, Abs, Calves
     */
    @GET("exercisecategory/")
    Call<ExerciseItem.CategoryResponse> getCategories(
            @Query("format") String format
    );

    /**
     * Filter latihan berdasarkan kategori
     */
    @GET("exercise/")
    Call<ExerciseItem.ExerciseResponse> getExercisesByCategory(
            @Query("format") String format,
            @Query("language") int language,
            @Query("category") int categoryId,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    /**
     * Ambil daftar otot
     */
    @GET("muscle/")
    Call<ExerciseItem.MuscleResponse> getMuscles(
            @Query("format") String format
    );

    /**
     * Ambil detail latihan berdasarkan ID
     */
    @GET("exerciseinfo/{id}/")
    Call<ExerciseItem.ExerciseInfo> getExerciseDetail(
            @retrofit2.http.Path("id") int exerciseId,
            @Query("format") String format
    );
}