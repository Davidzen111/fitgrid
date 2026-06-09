package com.example.fitgrid.model;

/**
 * WorkoutLog - Model data catatan latihan yang disimpan lokal (SQLite)
 */
public class WorkoutLog {
    private int id;
    private String exerciseName;
    private String category;
    private int sets;
    private int reps;
    private float weight;       // dalam kg
    private int durationMinutes; // durasi dalam menit
    private String notes;
    private String date;        // format: dd/MM/yyyy

    // Constructor kosong
    public WorkoutLog() {}

    // Constructor lengkap
    public WorkoutLog(int id, String exerciseName, String category, int sets, int reps,
                      float weight, int durationMinutes, String notes, String date) {
        this.id = id;
        this.exerciseName = exerciseName;
        this.category = category;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.date = date;
    }

    // Constructor tanpa ID (untuk insert baru)
    public WorkoutLog(String exerciseName, String category, int sets, int reps,
                      float weight, int durationMinutes, String notes, String date) {
        this.exerciseName = exerciseName;
        this.category = category;
        this.sets = sets;
        this.reps = reps;
        this.weight = weight;
        this.durationMinutes = durationMinutes;
        this.notes = notes;
        this.date = date;
    }

    // ---- Getters & Setters ----

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    // Ringkasan untuk tampilan list
    public String getSummary() {
        return sets + " set × " + reps + " rep" + (weight > 0 ? " @ " + weight + "kg" : "");
    }
}