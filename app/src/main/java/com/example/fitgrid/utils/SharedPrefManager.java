package com.example.fitgrid.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {

    private static final String PREF_NAME = "fitgrid_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_LAST_BODY_PART = "last_body_part";

    private static SharedPrefManager instance;
    private final SharedPreferences prefs;

    private SharedPrefManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    // Mengelola state tema aplikasi
    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
    }

    // Mengelola persistensi filter kategori terakhir
    public String getLastBodyPart() {
        return prefs.getString(KEY_LAST_BODY_PART, "all");
    }

    public void setLastBodyPart(String bodyPart) {
        prefs.edit().putString(KEY_LAST_BODY_PART, bodyPart).apply();
    }
}