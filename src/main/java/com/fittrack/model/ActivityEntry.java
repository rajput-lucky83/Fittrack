package com.fittrack.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** One row of the system activity log (what the admin sees in the live feed). */
public class ActivityEntry {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM, HH:mm:ss");

    private int id;
    private int userId;
    private String actorName;
    private String action;
    private String details;
    private LocalDateTime createdAt;

    public String getTimeText() {
        return createdAt == null ? "" : createdAt.format(FMT);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getActorName() { return actorName; }
    public void setActorName(String actorName) { this.actorName = actorName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
