package com.example.fitgrid.api;

import com.example.fitgrid.model.ExerciseItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Ambil semua exercise dengan limit & offset (pagination)
    @GET("exercises")
    Call<List<ExerciseItem>> getExercises(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    // Ambil exercise berdasarkan bodyPart
    @GET("exercises/bodyPart/{bodyPart}")
    Call<List<ExerciseItem>> getExercisesByBodyPart(
            @Path("bodyPart") String bodyPart,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    // Ambil daftar semua bodyPart
    @GET("exercises/bodyPartList")
    Call<List<String>> getBodyPartList();

    // Ambil detail exercise by ID
    @GET("exercises/exercise/{id}")
    Call<ExerciseItem> getExerciseById(
            @Path("id") String id
    );
}