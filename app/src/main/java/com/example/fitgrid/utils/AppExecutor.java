package com.example.fitgrid.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AppExecutor - mengelola background thread & main thread
 * Memenuhi spesifikasi: Background Thread menggunakan Executor & Handler
 */
public class AppExecutor {

    private static AppExecutor instance;

    // Thread pool untuk operasi background (DB, file I/O)
    private final ExecutorService diskIO;

    // Thread pool untuk operasi network
    private final ExecutorService networkIO;

    // Handler untuk kembali ke main thread (UI)
    private final Handler mainThread;

    private AppExecutor() {
        diskIO = Executors.newSingleThreadExecutor();   // serial agar DB aman
        networkIO = Executors.newFixedThreadPool(3);    // 3 thread paralel
        mainThread = new Handler(Looper.getMainLooper());
    }

    public static synchronized AppExecutor getInstance() {
        if (instance == null) {
            instance = new AppExecutor();
        }
        return instance;
    }

    /** Jalankan di background thread (operasi DB / file) */
    public void diskIO(Runnable runnable) {
        diskIO.execute(runnable);
    }

    /** Jalankan di network thread */
    public void networkIO(Runnable runnable) {
        networkIO.execute(runnable);
    }

    /** Post ke main/UI thread */
    public void mainThread(Runnable runnable) {
        mainThread.post(runnable);
    }

    /** Post ke main thread dengan delay (ms) */
    public void mainThreadDelayed(Runnable runnable, long delayMs) {
        mainThread.postDelayed(runnable, delayMs);
    }
}
