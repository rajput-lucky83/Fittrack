package com.fittrack.model;

import java.time.LocalDate;

/** A weekly target the user sets, e.g. "150 minutes per week". */
public class Goal {

    private int id;
    private int userId;
    private String title;
    private String metric;      // MINUTES, CALORIES or WORKOUTS
    private int targetValue;
    private LocalDate createdOn;

    // filled in by GoalService, not stored in the table
    private int currentValue;

    public int getPercent() {
        if (targetValue <= 0) return 0;
        int p = (int) Math.round(currentValue * 100.0 / targetValue);
        return Math.min(p, 100);
    }

    public String getUnit() {
        switch (metric) {
            case "MINUTES":  return "min";
            case "CALORIES": return "kcal";
            default:         return "workouts";
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }

    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }

    public LocalDate getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDate createdOn) { this.createdOn = createdOn; }

    public int getCurrentValue() { return currentValue; }
    public void setCurrentValue(int currentValue) { this.currentValue = currentValue; }
}
