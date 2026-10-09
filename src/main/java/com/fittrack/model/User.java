package com.fittrack.model;

import java.time.LocalDate;

public class User extends Person {

    private String passwordHash;
    private Role role = Role.USER;
    private boolean active = true;
    private Double weightKg;
    private Double heightCm;
    private String fitnessGoal = "GENERAL";
    private LocalDate joinedOn;

    public User() { }

    public User(String name, String email, String passwordHash, Role role) {
        super(0, name, email);
        this.passwordHash = passwordHash;
        this.role = role;
        this.joinedOn = LocalDate.now();
    }

    @Override
    public String getHomePath() {
        return role.getHomePath();
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    /** body mass index, or 0 if we don't have both measurements */
    public double getBmi() {
        if (weightKg == null || heightCm == null || heightCm <= 0) return 0;
        double m = heightCm / 100.0;
        return Math.round((weightKg / (m * m)) * 10.0) / 10.0;
    }

    public String getBmiCategory() {
        double bmi = getBmi();
        if (bmi == 0) return "Not available";
        if (bmi < 18.5) return "Underweight";
        if (bmi < 25) return "Normal";
        if (bmi < 30) return "Overweight";
        return "Obese";
    }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public String getFitnessGoal() { return fitnessGoal; }
    public void setFitnessGoal(String fitnessGoal) { this.fitnessGoal = fitnessGoal; }

    public LocalDate getJoinedOn() { return joinedOn; }
    public void setJoinedOn(LocalDate joinedOn) { this.joinedOn = joinedOn; }
}
