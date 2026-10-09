package com.fittrack.model;

import java.time.LocalDate;

public class Challenge {

    private int id;
    private String title;
    private String description;
    private String metric;          // MINUTES or WORKOUTS
    private int targetValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private int createdBy;

    // extra fields that come from joins
    private int participantCount;
    private boolean joinedByCurrentUser;
    private int progress;           // only set when we load a user's own entries
    private LocalDate joinedOn;

    public boolean isOver() {
        return endDate.isBefore(LocalDate.now());
    }

    public boolean isUpcoming() {
        return startDate.isAfter(LocalDate.now());
    }

    public boolean isCompleted() {
        return progress >= targetValue;
    }

    public int getPercent() {
        if (targetValue <= 0) return 0;
        return Math.min(100, (int) Math.round(progress * 100.0 / targetValue));
    }

    public String getUnit() {
        return "MINUTES".equals(metric) ? "minutes" : "workouts";
    }

    /** Active / Upcoming / Ended, shown as a badge */
    public String getState() {
        if (isOver()) return "Ended";
        if (isUpcoming()) return "Upcoming";
        return "Active";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }

    public int getTargetValue() { return targetValue; }
    public void setTargetValue(int targetValue) { this.targetValue = targetValue; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }

    public int getParticipantCount() { return participantCount; }
    public void setParticipantCount(int participantCount) { this.participantCount = participantCount; }

    public boolean isJoinedByCurrentUser() { return joinedByCurrentUser; }
    public void setJoinedByCurrentUser(boolean joined) { this.joinedByCurrentUser = joined; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public LocalDate getJoinedOn() { return joinedOn; }
    public void setJoinedOn(LocalDate joinedOn) { this.joinedOn = joinedOn; }
}
