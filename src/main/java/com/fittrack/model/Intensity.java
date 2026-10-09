package com.fittrack.model;

public enum Intensity {
    LOW("Low", 0.8),
    MEDIUM("Medium", 1.0),
    HIGH("High", 1.25);

    private final String label;
    private final double factor;   // used when estimating calories

    Intensity(String label, double factor) {
        this.label = label;
        this.factor = factor;
    }

    public String getLabel() { return label; }
    public double getFactor() { return factor; }

    public static Intensity fromString(String s) {
        for (Intensity i : values()) {
            if (i.name().equalsIgnoreCase(s)) return i;
        }
        return MEDIUM;
    }
}
