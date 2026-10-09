package com.fittrack.model;

import java.time.LocalDate;

public class Workout implements Comparable<Workout> {

    private int id;
    private int userId;
    private String type;
    private int durationMin;
    private Intensity intensity = Intensity.MEDIUM;
    private int calories;
    private LocalDate workoutDate;
    private String notes;

    public Workout() { }

    public Workout(int userId, String type, int durationMin, Intensity intensity, LocalDate date, String notes) {
        this.userId = userId;
        this.type = type;
        this.durationMin = durationMin;
        this.intensity = intensity;
        this.workoutDate = date;
        this.notes = notes;
    }

    // newest first
    @Override
    public int compareTo(Workout other) {
        int c = other.workoutDate.compareTo(this.workoutDate);
        return c != 0 ? c : Integer.compare(other.id, this.id);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

    public Intensity getIntensity() { return intensity; }
    public void setIntensity(Intensity intensity) { this.intensity = intensity; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public LocalDate getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(LocalDate workoutDate) { this.workoutDate = workoutDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
