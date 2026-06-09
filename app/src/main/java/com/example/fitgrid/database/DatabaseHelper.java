package com.example.fitgrid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.fitgrid.model.WorkoutLog;
import java.util.ArrayList;
import java.util.List;

/**
 * DatabaseHelper - SQLite helper untuk menyimpan data lokal
 * Tabel: workout_log, cached_exercises
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "fitgrid.db";
    // REVISI UTAMA: Versi dinaikkan menjadi 3 untuk mencegah error "Can't downgrade"
    private static final int DB_VERSION = 3;

    // Tabel Workout Log
    public static final String TABLE_WORKOUT_LOG = "workout_log";
    public static final String COL_ID = "id";
    public static final String COL_EXERCISE_NAME = "exercise_name";
    public static final String COL_CATEGORY = "category";
    public static final String COL_SETS = "sets";
    public static final String COL_REPS = "reps";
    public static final String COL_WEIGHT = "weight";
    public static final String COL_DURATION = "duration_minutes";
    public static final String COL_NOTES = "notes";
    public static final String COL_DATE = "date";

    // Tabel Cache Latihan (offline)
    public static final String TABLE_EXERCISE_CACHE = "exercise_cache";
    public static final String COL_EX_ID = "exercise_id";
    public static final String COL_EX_NAME = "name";
    public static final String COL_EX_CATEGORY = "category";
    public static final String COL_EX_DESCRIPTION = "description";
    public static final String COL_EX_MUSCLES = "muscles";
    public static final String COL_EX_EQUIPMENT = "equipment";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Buat tabel workout log
        String createWorkoutLog = "CREATE TABLE " + TABLE_WORKOUT_LOG + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_EXERCISE_NAME + " TEXT NOT NULL, " +
                COL_CATEGORY + " TEXT, " +
                COL_SETS + " INTEGER DEFAULT 0, " +
                COL_REPS + " INTEGER DEFAULT 0, " +
                COL_WEIGHT + " REAL DEFAULT 0, " +
                COL_DURATION + " INTEGER DEFAULT 0, " +
                COL_NOTES + " TEXT, " +
                COL_DATE + " TEXT NOT NULL)";
        db.execSQL(createWorkoutLog);

        // Buat tabel cache latihan
        String createExerciseCache = "CREATE TABLE " + TABLE_EXERCISE_CACHE + " (" +
                COL_EX_ID + " INTEGER PRIMARY KEY, " +
                COL_EX_NAME + " TEXT, " +
                COL_EX_CATEGORY + " TEXT, " +
                COL_EX_DESCRIPTION + " TEXT, " +
                COL_EX_MUSCLES + " TEXT, " +
                COL_EX_EQUIPMENT + " TEXT)";
        db.execSQL(createExerciseCache);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_CACHE);
        onCreate(db);
    }

    // ==================== WORKOUT LOG CRUD ====================

    /**
     * Simpan catatan latihan baru
     */
    public long insertWorkoutLog(WorkoutLog log) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EXERCISE_NAME, log.getExerciseName());
        cv.put(COL_CATEGORY, log.getCategory());
        cv.put(COL_SETS, log.getSets());
        cv.put(COL_REPS, log.getReps());
        cv.put(COL_WEIGHT, log.getWeight());
        cv.put(COL_DURATION, log.getDurationMinutes());
        cv.put(COL_NOTES, log.getNotes());
        cv.put(COL_DATE, log.getDate());
        return db.insert(TABLE_WORKOUT_LOG, null, cv);
    }

    /**
     * Ambil semua catatan latihan (terbaru dulu)
     */
    public List<WorkoutLog> getAllWorkoutLogs() {
        List<WorkoutLog> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUT_LOG, null, null, null,
                null, null, COL_DATE + " DESC, " + COL_ID + " DESC");

        if (cursor.moveToFirst()) {
            do {
                WorkoutLog log = cursorToWorkoutLog(cursor);
                list.add(log);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Hapus catatan latihan berdasarkan ID
     */
    public int deleteWorkoutLog(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_WORKOUT_LOG, COL_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    /**
     * Update catatan latihan
     */
    public int updateWorkoutLog(WorkoutLog log) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EXERCISE_NAME, log.getExerciseName());
        cv.put(COL_CATEGORY, log.getCategory());
        cv.put(COL_SETS, log.getSets());
        cv.put(COL_REPS, log.getReps());
        cv.put(COL_WEIGHT, log.getWeight());
        cv.put(COL_DURATION, log.getDurationMinutes());
        cv.put(COL_NOTES, log.getNotes());
        cv.put(COL_DATE, log.getDate());
        return db.update(TABLE_WORKOUT_LOG, cv, COL_ID + "=?",
                new String[]{String.valueOf(log.getId())});
    }

    /**
     * Hitung jumlah total workout
     */
    public int getTotalWorkoutCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_WORKOUT_LOG, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    /**
     * Hitung total kalori terbakar (estimasi sederhana)
     * Estimasi: 5 kalori per menit latihan
     */
    public int getTotalCaloriesBurned() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_DURATION + ") FROM " + TABLE_WORKOUT_LOG, null);
        int totalMinutes = 0;
        if (cursor.moveToFirst()) {
            totalMinutes = cursor.getInt(0);
        }
        cursor.close();
        return totalMinutes * 5; // estimasi 5 kal/menit
    }

    private WorkoutLog cursorToWorkoutLog(Cursor cursor) {
        return new WorkoutLog(
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_EXERCISE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_SETS)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_REPS)),
                cursor.getFloat(cursor.getColumnIndexOrThrow(COL_WEIGHT)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COL_DURATION)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_NOTES)),
                cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE))
        );
    }

    // ==================== EXERCISE CACHE ====================

    /**
     * Cache latihan untuk mode offline
     */
    public void cacheExercise(int id, String name, String category,
                              String description, String muscles, String equipment) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_EX_ID, id);
        cv.put(COL_EX_NAME, name);
        cv.put(COL_EX_CATEGORY, category);
        cv.put(COL_EX_DESCRIPTION, description);
        cv.put(COL_EX_MUSCLES, muscles);
        cv.put(COL_EX_EQUIPMENT, equipment);
        // Insert or replace jika sudah ada
        db.insertWithOnConflict(TABLE_EXERCISE_CACHE, null, cv,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Ambil semua cache latihan
     */
    public List<com.example.fitgrid.model.ExerciseItem.Exercise> getCachedExercises() {
        List<com.example.fitgrid.model.ExerciseItem.Exercise> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_EXERCISE_CACHE, null, null,
                null, null, null, COL_EX_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                com.example.fitgrid.model.ExerciseItem.Exercise ex =
                        new com.example.fitgrid.model.ExerciseItem.Exercise();
                ex.id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_EX_ID));
                ex.name = cursor.getString(cursor.getColumnIndexOrThrow(COL_EX_NAME));
                ex.description = cursor.getString(cursor.getColumnIndexOrThrow(COL_EX_DESCRIPTION));
                list.add(ex);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * Cek apakah ada cache tersimpan
     */
    public boolean hasCachedExercises() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EXERCISE_CACHE, null);
        boolean hasData = false;
        if (cursor.moveToFirst()) {
            hasData = cursor.getInt(0) > 0;
        }
        cursor.close();
        return hasData;
    }
}