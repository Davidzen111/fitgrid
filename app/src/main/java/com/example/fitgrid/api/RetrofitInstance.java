package com.example.fitgrid.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    // --- KODE LAMA (wger API) DIMATIKAN ---
    // wger API — gratis, tanpa API key, tanpa limit
    // private static final String BASE_URL = "https://wger.de/";

    // --- KODE BARU (ExerciseDB API) DIKATIFKAN ---
    private static final String BASE_URL = "https://exercisedb.p.rapidapi.com/";

    private static RetrofitInstance instance;
    private final ApiService apiService;

    private RetrofitInstance() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                /* Catatan:
                   ExerciseDB membutuhkan API Key dari RapidAPI.
                   Jika Anda menaruh API Key-nya menggunakan anotasi @Headers di ApiService.java,
                   maka konfigurasi OkHttpClient ini sudah cukup dan tidak perlu diubah.
                */
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitInstance getInstance() {
        if (instance == null) {
            instance = new RetrofitInstance();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}