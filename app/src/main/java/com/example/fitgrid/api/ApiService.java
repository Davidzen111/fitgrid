package com.example.fitgrid.api;

import com.example.fitgrid.model.ExerciseItem;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("exercises")
    Call<List<ExerciseItem>> getExercises(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("exercises/bodyPart/{bodyPart}")
    Call<List<ExerciseItem>> getExercisesByBodyPart(
            @Path("bodyPart") String bodyPart,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("exercises/bodyPartList")
    Call<List<String>> getBodyPartList();

    @GET("exercises/exercise/{id}")
    Call<ExerciseItem> getExerciseById(
            @Path("id") String id
    );
}