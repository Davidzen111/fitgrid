package com.example.fitgrid.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class ExerciseItem implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("bodyPart")
    private String bodyPart;

    @SerializedName("equipment")
    private String equipment;

    @SerializedName("gifUrl")
    private String gifUrl;

    @SerializedName("target")
    private String target;

    @SerializedName("secondaryMuscles")
    private List<String> secondaryMuscles;

    @SerializedName("instructions")
    private List<String> instructions;

    // Constructor kosong untuk SQLite/Gson
    public ExerciseItem() {}

    public ExerciseItem(String id, String name, String bodyPart, String equipment,
                        String gifUrl, String target) {
        this.id = id;
        this.name = name;
        this.bodyPart = bodyPart;
        this.equipment = equipment;
        this.gifUrl = gifUrl;
        this.target = target;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBodyPart() { return bodyPart; }
    public void setBodyPart(String bodyPart) { this.bodyPart = bodyPart; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public String getGifUrl() { return gifUrl; }
    public void setGifUrl(String gifUrl) { this.gifUrl = gifUrl; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public List<String> getSecondaryMuscles() { return secondaryMuscles; }
    public void setSecondaryMuscles(List<String> secondaryMuscles) {
        this.secondaryMuscles = secondaryMuscles;
    }

    public List<String> getInstructions() { return instructions; }
    public void setInstructions(List<String> instructions) {
        this.instructions = instructions;
    }
}