package com.example.fitgrid.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ExerciseItem {

    @SerializedName("id")
    private int idInt;

    // Tambahkan 'transient' agar Gson mengabaikan field ini dan tidak bentrok dengan idInt
    private transient String id; // string untuk kompatibilitas SQLite

    @SerializedName("category")
    private Category category;

    @SerializedName("muscles")
    private List<Muscle> muscles;

    @SerializedName("muscles_secondary")
    private List<Muscle> musclesSecondary;

    @SerializedName("equipment")
    private List<Equipment> equipmentList;

    @SerializedName("images")
    private List<ExerciseImage> images;

    @SerializedName("translations")
    private List<Translation> translations;

    // Field helper (diisi manual / dari SQLite cache)
    private String name;
    private String bodyPart;
    private String target;

    // Tambahkan 'transient' agar Gson mengabaikan field ini dan tidak bentrok dengan equipmentList
    private transient String equipment;

    private String gifUrl;
    private List<String> instructions;
    private List<String> secondaryMuscles;

    // ===== Inner classes =====

    public static class Category {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
    }

    public static class Muscle {
        @SerializedName("id")
        public int id;
        @SerializedName("name_en")
        public String nameEn;
        @SerializedName("name")
        public String name;
        public String getDisplayName() {
            return (nameEn != null && !nameEn.isEmpty()) ? nameEn : name;
        }
    }

    public static class Equipment {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
    }

    public static class ExerciseImage {
        @SerializedName("id")
        public int id;
        @SerializedName("image")
        public String imageUrl;   // URL lengkap gambar
        @SerializedName("is_main")
        public boolean isMain;
    }

    public static class Translation {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("description")
        public String description;
        @SerializedName("language")
        public int language; // 2 = English
    }

    public ExerciseItem() {}

    // ===== ID =====
    public String getId() {
        if (id != null && !id.isEmpty()) return id;
        return String.valueOf(idInt);
    }
    public void setId(String id) { this.id = id; }

    // ===== NAME — ambil dari translations bahasa Inggris =====
    public String getName() {
        if (name != null && !name.isEmpty()) return name;
        if (translations != null) {
            // Cari bahasa Inggris (language=2)
            for (Translation t : translations) {
                if (t.language == 2 && t.name != null && !t.name.isEmpty())
                    return t.name;
            }
            // Fallback: ambil translation pertama yang ada nama
            for (Translation t : translations) {
                if (t.name != null && !t.name.isEmpty()) return t.name;
            }
        }
        return "Unknown Exercise";
    }
    public void setName(String name) { this.name = name; }

    // ===== BODY PART dari category =====
    public String getBodyPart() {
        if (bodyPart != null && !bodyPart.isEmpty()) return bodyPart;
        if (category != null && category.name != null) return category.name;
        return "-";
    }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }

    // ===== TARGET dari muscles =====
    public String getTarget() {
        if (target != null && !target.isEmpty()) return target;
        if (muscles != null && !muscles.isEmpty()) return muscles.get(0).getDisplayName();
        return "-";
    }
    public void setTarget(String target) { this.target = target; }

    // ===== EQUIPMENT =====
    public String getEquipment() {
        if (equipment != null && !equipment.isEmpty()) return equipment;
        if (equipmentList != null && !equipmentList.isEmpty()) return equipmentList.get(0).name;
        return "bodyweight";
    }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    // ===== GIF/IMAGE URL — ambil gambar utama =====
    public String getGifUrl() {
        if (gifUrl != null && !gifUrl.isEmpty()) return gifUrl;
        if (images != null) {
            // Prioritas: is_main = true
            for (ExerciseImage img : images) {
                if (img.isMain && img.imageUrl != null && !img.imageUrl.isEmpty())
                    return img.imageUrl;
            }
            // Fallback: gambar pertama
            for (ExerciseImage img : images) {
                if (img.imageUrl != null && !img.imageUrl.isEmpty())
                    return img.imageUrl;
            }
        }
        return null;
    }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }

    // ===== INSTRUCTIONS / DESCRIPTION =====
    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) { this.instructions = instructions; }

    // Ambil deskripsi dari translations bahasa Inggris
    public String getDescription() {
        if (translations != null) {
            for (Translation t : translations) {
                if (t.language == 2 && t.description != null && !t.description.isEmpty())
                    return t.description;
            }
        }
        return null;
    }

    // ===== SECONDARY MUSCLES =====
    public List<String> getSecondaryMuscles() {
        if (secondaryMuscles != null) return secondaryMuscles;
        if (musclesSecondary != null && !musclesSecondary.isEmpty()) {
            List<String> list = new java.util.ArrayList<>();
            for (Muscle m : musclesSecondary) list.add(m.getDisplayName());
            return list;
        }
        return null;
    }
    public void setSecondaryMuscles(List<String> s) { this.secondaryMuscles = s; }

    public List<ExerciseImage> getImages() { return images; }
    public Category getCategory() { return category; }
    public List<Muscle> getMuscles() { return muscles; }
    public List<Translation> getTranslations() { return translations; }
}