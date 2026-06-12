package com.example.fitgrid.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutor {

    private static AppExecutor instance;

    private final ExecutorService diskIO;
    private final ExecutorService networkIO;
    private final Handler mainThread;

    private AppExecutor() {
        diskIO = Executors.newSingleThreadExecutor();
        networkIO = Executors.newFixedThreadPool(3);
        mainThread = new Handler(Looper.getMainLooper());
    }

    public static synchronized AppExecutor getInstance() {
        if (instance == null) {
            instance = new AppExecutor();
        }
        return instance;
    }

    public void diskIO(Runnable runnable) {
        diskIO.execute(runnable);
    }

    public void networkIO(Runnable runnable) {
        networkIO.execute(runnable);
    }

    public void mainThread(Runnable runnable) {
        mainThread.post(runnable);
    }

    public void mainThreadDelayed(Runnable runnable, long delayMs) {
        mainThread.postDelayed(runnable, delayMs);
    }
}