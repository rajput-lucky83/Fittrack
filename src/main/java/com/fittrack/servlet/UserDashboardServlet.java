package com.fittrack.servlet;

import com.fittrack.dao.ChallengeDao;
import com.fittrack.dao.WorkoutDao;
import com.fittrack.model.Challenge;
import com.fittrack.model.User;
import com.fittrack.model.Workout;
import com.fittrack.service.GoalService;
import com.fittrack.service.RecommendationService;
import com.fittrack.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/user/dashboard")
public class UserDashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("EEE");

    private final WorkoutDao workoutDao = new WorkoutDao();
    private final ChallengeDao challengeDao = new ChallengeDao();
    private final GoalService goalService = new GoalService();
    private final RecommendationService recommendationService = new RecommendationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        try {
            Map<String, Integer> totals = workoutDao.lifetimeTotals(user.getId());
            int streak = workoutDao.currentStreak(user.getId());

            List<Workout> lastTwoWeeks = workoutDao.findSince(user.getId(), LocalDate.now().minusDays(13));
            List<Workout> recent = new ArrayList<>(lastTwoWeeks);
            recent.sort(null);   // natural order = newest first (see Workout.compareTo)
            if (recent.size() > 5) {
                recent = recent.subList(0, 5);
            }

            // last 7 days for the small bar chart
            Map<LocalDate, Integer> week = workoutDao.minutesPerDay(user.getId(), 7);
            List<String> labels = week.keySet().stream().map(d -> d.format(DAY)).collect(Collectors.toList());

            // only challenges that are still running
            List<Challenge> running = challengeDao.findJoinedByUser(user.getId()).stream()
                    .filter(c -> !c.isOver())
                    .collect(Collectors.toList());

            req.setAttribute("totals", totals);
            req.setAttribute("streak", streak);
            req.setAttribute("recent", recent);
            req.setAttribute("weekLabels", JsonUtil.stringArray(labels));
            req.setAttribute("weekValues", JsonUtil.numberArray(week.values()));
            req.setAttribute("goals", goalService.goalsWithProgress(user.getId()));
            req.setAttribute("tips", recommendationService.tipsFor(user, lastTwoWeeks, streak));
            req.setAttribute("runningChallenges", running);
            render(req, resp, "user/dashboard.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }
}
