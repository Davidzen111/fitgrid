package com.example.fitgrid.api;

import com.example.fitgrid.model.ExerciseItem;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    String API_KEY = "e131227e75mshf316288356799f1p1be85fjsn8b88ec5699ba";
    String API_HOST = "exercisedb.p.rapidapi.com";

    @Headers({
            "X-RapidAPI-Key: " + API_KEY,
            "X-RapidAPI-Host: " + API_HOST
    })
    @GET("exercises")
    Call<List<ExerciseItem>> getExercises(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @Headers({
            "X-RapidAPI-Key: " + API_KEY,
            "X-RapidAPI-Host: " + API_HOST
    })
    @GET("exercises/bodyPart/{bodyPart}")
    Call<List<ExerciseItem>> getExercisesByCategory(
            @Path("bodyPart") String bodyPart,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @Headers({
            "X-RapidAPI-Key: " + API_KEY,
            "X-RapidAPI-Host: " + API_HOST
    })
    @GET("exercises/exercise/{id}")
    Call<ExerciseItem> getExerciseById(
            @Path("id") String id
    );

    @Headers({
            "X-RapidAPI-Key: " + API_KEY,
            "X-RapidAPI-Host: " + API_HOST
    })
    @GET("exercises/bodyPartList")
    Call<List<String>> getCategories();

    @Headers({
            "X-RapidAPI-Key: " + API_KEY,
            "X-RapidAPI-Host: " + API_HOST
    })
    @GET("exercises/name/{name}")
    Call<List<ExerciseItem>> searchExercises(
            @Path("name") String name,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}