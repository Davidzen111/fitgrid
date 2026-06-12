package com.example.fitgrid.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.fitgrid.model.ExerciseItem;

import java.util.ArrayList;
import java.util.List;
import com.example.fitgrid.model.WorkoutLog;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fitgrid.db";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_EXERCISES = "exercises";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_BODY_PART = "body_part";
    public static final String COL_EQUIPMENT = "equipment";
    public static final String COL_GIF_URL = "gif_url";
    public static final String COL_TARGET = "target";
    public static final String COL_BODY_PART_FILTER = "body_part_filter";
    public static final String TABLE_SAVED = "saved_exercises";
    public static final String TABLE_WORKOUT_LOG = "workout_log";
    public static final String COL_LOG_ID = "log_id";
    public static final String COL_LOG_EXERCISE_ID = "exercise_id";
    public static final String COL_LOG_EXERCISE_NAME = "exercise_name";
    public static final String COL_LOG_SETS = "sets";
    public static final String COL_LOG_REPS = "reps";
    public static final String COL_LOG_DATE = "date";
    public static final String COL_LOG_NOTE = "note";
    public static final String TABLE_SETTINGS = "settings";
    public static final String COL_SETTING_KEY = "key";
    public static final String COL_SETTING_VALUE = "value";

    private static final String CREATE_TABLE_EXERCISES =
            "CREATE TABLE " + TABLE_EXERCISES + " ("
                    + COL_ID + " TEXT PRIMARY KEY, "
                    + COL_NAME + " TEXT, "
                    + COL_BODY_PART + " TEXT, "
                    + COL_EQUIPMENT + " TEXT, "
                    + COL_GIF_URL + " TEXT, "
                    + COL_TARGET + " TEXT, "
                    + COL_BODY_PART_FILTER + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_SAVED =
            "CREATE TABLE " + TABLE_SAVED + " ("
                    + COL_ID + " TEXT PRIMARY KEY, "
                    + COL_NAME + " TEXT, "
                    + COL_BODY_PART + " TEXT, "
                    + COL_EQUIPMENT + " TEXT, "
                    + COL_GIF_URL + " TEXT, "
                    + COL_TARGET + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_WORKOUT_LOG =
            "CREATE TABLE " + TABLE_WORKOUT_LOG + " ("
                    + COL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_LOG_EXERCISE_ID + " TEXT, "
                    + COL_LOG_EXERCISE_NAME + " TEXT, "
                    + COL_LOG_SETS + " INTEGER, "
                    + COL_LOG_REPS + " INTEGER, "
                    + COL_LOG_DATE + " TEXT, "
                    + COL_LOG_NOTE + " TEXT"
                    + ")";

    private static final String CREATE_TABLE_SETTINGS =
            "CREATE TABLE " + TABLE_SETTINGS + " ("
                    + COL_SETTING_KEY + " TEXT PRIMARY KEY, "
                    + COL_SETTING_VALUE + " TEXT"
                    + ")";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_EXERCISES);
        db.execSQL(CREATE_TABLE_SAVED);
        db.execSQL(CREATE_TABLE_WORKOUT_LOG);
        db.execSQL(CREATE_TABLE_SETTINGS); // ← TAMBAHAN BARU
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop semua tabel lama, buat ulang
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKOUT_LOG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void saveSetting(String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_SETTING_KEY, key);
        cv.put(COL_SETTING_VALUE, value);
        db.insertWithOnConflict(TABLE_SETTINGS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public String getSetting(String key, String defaultValue) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SETTINGS,
                new String[]{COL_SETTING_VALUE},
                COL_SETTING_KEY + " = ?",
                new String[]{key},
                null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }
        return defaultValue;
    }


    public void cacheExercises(List<ExerciseItem> exercises, String bodyPartFilter) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (ExerciseItem ex : exercises) {
                ContentValues cv = new ContentValues();
                cv.put(COL_ID, ex.getId());
                cv.put(COL_NAME, ex.getName());
                cv.put(COL_BODY_PART, ex.getBodyPart());
                cv.put(COL_EQUIPMENT, ex.getEquipment());
                cv.put(COL_GIF_URL, ex.getGifUrl());
                cv.put(COL_TARGET, ex.getTarget());
                cv.put(COL_BODY_PART_FILTER, bodyPartFilter);
                db.insertWithOnConflict(TABLE_EXERCISES, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public List<ExerciseItem> getCachedExercises(String bodyPartFilter) {
        List<ExerciseItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String selection = (bodyPartFilter != null && !bodyPartFilter.isEmpty() && !bodyPartFilter.equals("all"))
                ? COL_BODY_PART_FILTER + " = ?" : null;
        String[] selectionArgs = selection != null ? new String[]{bodyPartFilter} : null;
        Cursor cursor = db.query(TABLE_EXERCISES, null, selection, selectionArgs, null, null, COL_NAME + " ASC");
        if (cursor != null) {
            while (cursor.moveToNext()) { list.add(cursorToExercise(cursor)); }
            cursor.close();
        }
        return list;
    }

    public boolean hasCachedExercises(String bodyPartFilter) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EXERCISES
                + " WHERE " + COL_BODY_PART_FILTER + " = ?", new String[]{bodyPartFilter});
        int count = 0;
        if (cursor != null) { cursor.moveToFirst(); count = cursor.getInt(0); cursor.close(); }
        return count > 0;
    }


    public boolean saveExercise(ExerciseItem ex) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, ex.getId()); cv.put(COL_NAME, ex.getName());
        cv.put(COL_BODY_PART, ex.getBodyPart()); cv.put(COL_EQUIPMENT, ex.getEquipment());
        cv.put(COL_GIF_URL, ex.getGifUrl()); cv.put(COL_TARGET, ex.getTarget());
        long result = db.insertWithOnConflict(TABLE_SAVED, null, cv, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    public boolean removeExercise(String id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_SAVED, COL_ID + " = ?", new String[]{id}) > 0;
    }

    public boolean isSaved(String id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SAVED, new String[]{COL_ID}, COL_ID + " = ?", new String[]{id}, null, null, null);
        boolean saved = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return saved;
    }

    public List<ExerciseItem> getAllSaved() {
        List<ExerciseItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SAVED, null, null, null, null, null, COL_NAME + " ASC");
        if (cursor != null) { while (cursor.moveToNext()) { list.add(cursorToExercise(cursor)); } cursor.close(); }
        return list;
    }

    public long addWorkoutLog(WorkoutLog log) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_LOG_EXERCISE_ID, log.getExerciseId());
        cv.put(COL_LOG_EXERCISE_NAME, log.getExerciseName());
        cv.put(COL_LOG_SETS, log.getSets());
        cv.put(COL_LOG_REPS, log.getReps());
        cv.put(COL_LOG_DATE, log.getDate());
        cv.put(COL_LOG_NOTE, log.getNote());
        return db.insert(TABLE_WORKOUT_LOG, null, cv);
    }

    public boolean deleteWorkoutLog(int logId) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_WORKOUT_LOG, COL_LOG_ID + " = ?",
                new String[]{String.valueOf(logId)}) > 0;
    }

    public List<WorkoutLog> getAllWorkoutLogs() {
        List<WorkoutLog> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_WORKOUT_LOG, null, null, null, null, null,
                COL_LOG_DATE + " DESC, " + COL_LOG_ID + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                WorkoutLog log = new WorkoutLog();
                log.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_ID)));
                log.setExerciseId(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_EXERCISE_ID)));
                log.setExerciseName(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_EXERCISE_NAME)));
                log.setSets(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_SETS)));
                log.setReps(cursor.getInt(cursor.getColumnIndexOrThrow(COL_LOG_REPS)));
                log.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_DATE)));
                log.setNote(cursor.getString(cursor.getColumnIndexOrThrow(COL_LOG_NOTE)));
                list.add(log);
            }
            cursor.close();
        }
        return list;
    }

    public int getTotalWorkoutCount() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_WORKOUT_LOG, null);
        int count = 0;
        if (cursor != null) { cursor.moveToFirst(); count = cursor.getInt(0); cursor.close(); }
        return count;
    }

    private ExerciseItem cursorToExercise(Cursor cursor) {
        ExerciseItem item = new ExerciseItem();
        item.setId(cursor.getString(cursor.getColumnIndexOrThrow(COL_ID)));
        item.setName(cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME)));
        item.setBodyPart(cursor.getString(cursor.getColumnIndexOrThrow(COL_BODY_PART)));
        item.setEquipment(cursor.getString(cursor.getColumnIndexOrThrow(COL_EQUIPMENT)));
        item.setGifUrl(cursor.getString(cursor.getColumnIndexOrThrow(COL_GIF_URL)));
        item.setTarget(cursor.getString(cursor.getColumnIndexOrThrow(COL_TARGET)));
        return item;
    }
}