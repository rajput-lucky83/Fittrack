package com.fittrack.service;

import com.fittrack.dao.GoalDao;
import com.fittrack.dao.WorkoutDao;
import com.fittrack.model.Goal;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/** Works out how far along each weekly goal is (week = Monday to today). */
public class GoalService {

    private final GoalDao goalDao = new GoalDao();
    private final WorkoutDao workoutDao = new WorkoutDao();

    public List<Goal> goalsWithProgress(int userId) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        List<Goal> goals = goalDao.findByUser(userId);
        for (Goal g : goals) {
            g.setCurrentValue(workoutDao.metricBetween(userId, g.getMetric(), monday, today));
        }
        return goals;
    }
}
