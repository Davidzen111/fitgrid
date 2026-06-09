package com.example.fitgrid.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * ExerciseItem - Model data latihan dari wger API
 */
public class ExerciseItem {

    // ---- Exercise Response Wrapper ----
    public static class ExerciseResponse {
        @SerializedName("count")
        public int count;

        @SerializedName("next")
        public String next;

        @SerializedName("results")
        public List<Exercise> results;
    }

    // ---- Exercise Object ----
    public static class Exercise {
        @SerializedName("id")
        public int id;

        @SerializedName("uuid")
        public String uuid;

        @SerializedName("name")
        public String name;

        @SerializedName("category")
        public CategoryRef category;

        @SerializedName("muscles")
        public List<MuscleRef> muscles;

        @SerializedName("muscles_secondary")
        public List<MuscleRef> musclesSecondary;

        @SerializedName("equipment")
        public List<EquipmentRef> equipment;

        @SerializedName("language")
        public int language;

        @SerializedName("description")
        public String description;

        // Nama tampilan - bersih dari HTML tags
        public String getCleanName() {
            if (name == null) return "Latihan";
            return name.replaceAll("<[^>]*>", "").trim();
        }

        // Deskripsi bersih dari HTML tags
        public String getCleanDescription() {
            if (description == null || description.isEmpty()) {
                return "Tidak ada deskripsi tersedia untuk latihan ini.";
            }
            return description.replaceAll("<[^>]*>", "").trim();
        }

        // Nama kategori
        public String getCategoryName() {
            if (category != null) return category.name;
            return "Umum";
        }

        // Daftar otot yang dilatih
        public String getMuscleNames() {
            if (muscles == null || muscles.isEmpty()) return "Berbagai otot";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < muscles.size(); i++) {
                sb.append(muscles.get(i).name_en);
                if (i < muscles.size() - 1) sb.append(", ");
            }
            return sb.toString();
        }

        // Nama peralatan
        public String getEquipmentNames() {
            if (equipment == null || equipment.isEmpty()) return "Tanpa alat";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < equipment.size(); i++) {
                sb.append(equipment.get(i).name);
                if (i < equipment.size() - 1) sb.append(", ");
            }
            return sb.toString();
        }
    }

    // ---- Category Response ----
    public static class CategoryResponse {
        @SerializedName("count")
        public int count;

        @SerializedName("results")
        public List<Category> results;
    }

    // ---- Category Object ----
    public static class Category {
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;
    }

    // ---- Category Reference (inside Exercise) ----
    public static class CategoryRef {
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;
    }

    // ---- Muscle Response ----
    public static class MuscleResponse {
        @SerializedName("count")
        public int count;

        @SerializedName("results")
        public List<Muscle> results;
    }

    // ---- Muscle Object ----
    public static class Muscle {
        @SerializedName("id")
        public int id;

        @SerializedName("name_en")
        public String name_en;

        @SerializedName("is_front")
        public boolean is_front;
    }

    // ---- Muscle Reference ----
    public static class MuscleRef {
        @SerializedName("id")
        public int id;

        @SerializedName("name_en")
        public String name_en;
    }

    // ---- Equipment Reference ----
    public static class EquipmentRef {
        @SerializedName("id")
        public int id;

        @SerializedName("name")
        public String name;
    }

    // ---- Exercise Info (untuk detail) ----
    public static class ExerciseInfo {
        @SerializedName("id")
        public int id;

        @SerializedName("uuid")
        public String uuid;

        @SerializedName("name")
        public String name;

        @SerializedName("category")
        public CategoryRef category;

        @SerializedName("muscles")
        public List<Muscle> muscles;

        @SerializedName("equipment")
        public List<EquipmentRef> equipment;

        @SerializedName("images")
        public List<ExerciseImage> images;

        // Terjemahan latihan (termasuk deskripsi)
        @SerializedName("translations")
        public List<Translation> translations;

        public String getEnglishDescription() {
            if (translations == null) return "";
            for (Translation t : translations) {
                if (t.language == 2) { // English
                    return t.description != null ?
                            t.description.replaceAll("<[^>]*>", "").trim() : "";
                }
            }
            return "";
        }
    }

    // ---- Translation Object ----
    public static class Translation {
        @SerializedName("language")
        public int language;

        @SerializedName("name")
        public String name;

        @SerializedName("description")
        public String description;
    }

    // ---- Exercise Image ----
    public static class ExerciseImage {
        @SerializedName("id")
        public int id;

        @SerializedName("image")
        public String image;

        @SerializedName("is_main")
        public boolean is_main;
    }
}