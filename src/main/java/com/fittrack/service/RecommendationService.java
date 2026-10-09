package com.fittrack.service;

import com.fittrack.model.Intensity;
import com.fittrack.model.User;
import com.fittrack.model.Workout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds the "personalised guidance" tips on the dashboard. It is plain rule-based
 * logic on the user's profile and their last two weeks of workouts - no external service.
 */
public class RecommendationService {

    private static final int MAX_TIPS = 5;

    public List<String> tipsFor(User user, List<Workout> lastTwoWeeks, int streak) {
        List<String> tips = new ArrayList<>();

        LocalDate weekAgo = LocalDate.now().minusDays(6);
        List<Workout> thisWeek = lastTwoWeeks.stream()
                .filter(w -> !w.getWorkoutDate().isBefore(weekAgo))
                .collect(Collectors.toList());

        int weekMinutes = thisWeek.stream().mapToInt(Workout::getDurationMin).sum();

        // 1. activity level
        if (lastTwoWeeks.isEmpty()) {
            tips.add("You have no workouts in the last two weeks. Start small: a 20 minute walk today is enough to restart the habit.");
        } else if (thisWeek.isEmpty()) {
            tips.add("Nothing logged in the past 7 days. Try to fit in one short session before the weekend.");
        } else if (weekMinutes < 150) {
            tips.add("You did " + weekMinutes + " minutes this week. The usual target for adults is 150 minutes of moderate activity, so about "
                    + (150 - weekMinutes) + " more to go.");
        } else {
            tips.add("Great week - " + weekMinutes + " minutes is above the 150 minute guideline. Keep the same rhythm.");
        }

        // 2. streak
        if (streak >= 3) {
            tips.add("You are on a " + streak + " day streak. Don't forget that planned rest days help recovery too.");
        }

        // 3. too many hard sessions in a row
        long hardSessions = thisWeek.stream().filter(w -> w.getIntensity() == Intensity.HIGH).count();
        if (hardSessions >= 4) {
            tips.add("Four or more high intensity sessions in a week can lead to overtraining. Swap one for yoga or an easy walk.");
        }

        // 4. variety
        Set<String> types = new HashSet<>();
        for (Workout w : lastTwoWeeks) {
            types.add(w.getType());
        }
        if (lastTwoWeeks.size() >= 4 && types.size() == 1) {
            tips.add("All your recent sessions are " + types.iterator().next()
                    + ". Mixing in a different activity works different muscles and keeps things interesting.");
        }

        // 5. based on the goal chosen in the profile
        switch (user.getFitnessGoal() == null ? "GENERAL" : user.getFitnessGoal()) {
            case "WEIGHT_LOSS":
                tips.add("For weight loss, combine 3 cardio sessions (running, cycling, HIIT) with 2 days of strength work each week.");
                break;
            case "MUSCLE_GAIN":
                tips.add("For muscle gain aim for 3-4 weight training sessions a week and give each muscle group a day off before you train it again.");
                break;
            case "ENDURANCE":
                tips.add("To build endurance, increase your longest weekly session by about 10% at a time instead of jumping up quickly.");
                break;
            default:
                tips.add("Keeping a mix of cardio, strength and flexibility work gives the best all-round fitness.");
        }

        // 6. BMI note (only if we have the numbers)
        double bmi = user.getBmi();
        if (bmi > 0 && bmi >= 30) {
            tips.add("Your BMI is " + bmi + ". Low-impact options like swimming or cycling are easy on the joints while you build up.");
        } else if (bmi > 0 && bmi < 18.5) {
            tips.add("Your BMI is " + bmi + ", which is on the low side. Focus on strength training and eat enough to support your workouts.");
        }

        return tips.size() > MAX_TIPS ? tips.subList(0, MAX_TIPS) : tips;
    }
}
