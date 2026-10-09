package com.fittrack.servlet;

import com.fittrack.dao.WorkoutDao;
import com.fittrack.model.User;
import com.fittrack.service.GoalService;
import com.fittrack.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet("/user/progress")
public class ProgressServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("dd MMM");

    private final WorkoutDao workoutDao = new WorkoutDao();
    private final GoalService goalService = new GoalService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        int days = intParam(req, "days", 14);
        if (days != 7 && days != 14 && days != 30) {
            days = 14;
        }

        try {
            Map<LocalDate, Integer> minutes = workoutDao.minutesPerDay(user.getId(), days);
            Map<LocalDate, Integer> calories = workoutDao.caloriesPerDay(user.getId(), days);
            Map<String, Integer> byType = workoutDao.minutesByType(user.getId());

            List<String> dayLabels = new ArrayList<>();
            for (LocalDate d : minutes.keySet()) {
                dayLabels.add(d.format(SHORT));
            }

            // this week vs last week (Monday based)
            LocalDate today = LocalDate.now();
            LocalDate thisMonday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate lastMonday = thisMonday.minusWeeks(1);
            int thisWeek = workoutDao.metricBetween(user.getId(), "MINUTES", thisMonday, today);
            int lastWeek = workoutDao.metricBetween(user.getId(), "MINUTES", lastMonday, thisMonday.minusDays(1));

            req.setAttribute("days", days);
            req.setAttribute("totals", workoutDao.lifetimeTotals(user.getId()));
            req.setAttribute("streak", workoutDao.currentStreak(user.getId()));
            req.setAttribute("thisWeek", thisWeek);
            req.setAttribute("lastWeek", lastWeek);
            req.setAttribute("weekDiff", thisWeek - lastWeek);
            req.setAttribute("goals", goalService.goalsWithProgress(user.getId()));

            req.setAttribute("dayLabels", JsonUtil.stringArray(dayLabels));
            req.setAttribute("minuteValues", JsonUtil.numberArray(minutes.values()));
            req.setAttribute("calorieValues", JsonUtil.numberArray(calories.values()));
            req.setAttribute("typeLabels", JsonUtil.stringArray(byType.keySet()));
            req.setAttribute("typeValues", JsonUtil.numberArray(byType.values()));
            req.setAttribute("hasData", !byType.isEmpty());
            render(req, resp, "user/progress.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }
}
