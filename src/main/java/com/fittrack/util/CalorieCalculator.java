package com.fittrack.util;

import com.fittrack.model.Intensity;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rough calorie estimate using MET values:
 *   kcal = MET x weight(kg) x hours x intensity factor
 * It's an estimate, not a medical figure.
 */
public final class CalorieCalculator {

    // keeps insertion order so the dropdown on the form looks sensible
    private static final Map<String, Double> MET_BY_TYPE = new LinkedHashMap<>();

    static {
        MET_BY_TYPE.put("Walking", 3.5);
        MET_BY_TYPE.put("Running", 9.8);
        MET_BY_TYPE.put("Cycling", 7.5);
        MET_BY_TYPE.put("Swimming", 8.0);
        MET_BY_TYPE.put("Weight Training", 5.0);
        MET_BY_TYPE.put("HIIT", 10.0);
        MET_BY_TYPE.put("Yoga", 2.5);
        MET_BY_TYPE.put("Skipping Rope", 11.0);
        MET_BY_TYPE.put("Football", 7.0);
        MET_BY_TYPE.put("Other", 4.0);
    }

    private CalorieCalculator() { }

    public static java.util.Set<String> workoutTypes() {
        return MET_BY_TYPE.keySet();
    }

    public static boolean isKnownType(String type) {
        return MET_BY_TYPE.containsKey(type);
    }

    public static int estimate(String type, int minutes, Intensity intensity, double weightKg) {
        double met = MET_BY_TYPE.getOrDefault(type, 4.0);
        double hours = minutes / 60.0;
        return (int) Math.round(met * weightKg * hours * intensity.getFactor());
    }
}
