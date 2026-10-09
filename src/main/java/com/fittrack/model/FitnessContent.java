package com.fittrack.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FitnessContent {

    public static final String PENDING = "PENDING";
    public static final String APPROVED = "APPROVED";
    public static final String REJECTED = "REJECTED";

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private int id;
    private String title;
    private String category;
    private String body;
    private int submittedBy;
    private String submitterName;
    private String status = PENDING;
    private String adminNote;
    private LocalDateTime createdAt;

    public String getCreatedText() {
        return createdAt == null ? "" : createdAt.format(FMT);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public int getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(int submittedBy) { this.submittedBy = submittedBy; }

    public String getSubmitterName() { return submitterName; }
    public void setSubmitterName(String submitterName) { this.submitterName = submitterName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
