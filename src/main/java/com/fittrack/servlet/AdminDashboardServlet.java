package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.ContentDao;
import com.fittrack.dao.StatsDao;
import com.fittrack.model.FitnessContent;
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

/** Admin home: headline numbers, charts (fitness statistics) and the live activity feed. */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter SHORT = DateTimeFormatter.ofPattern("dd MMM");

    private final StatsDao statsDao = new StatsDao();
    private final ContentDao contentDao = new ContentDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("members", statsDao.totalMembers());
            req.setAttribute("activeMembers", statsDao.activeMembers());
            req.setAttribute("activeThisWeek", statsDao.activeThisWeek());
            req.setAttribute("totalWorkouts", statsDao.totalWorkouts());
            req.setAttribute("totalMinutes", statsDao.totalMinutes());
            req.setAttribute("runningChallenges", statsDao.runningChallenges());
            req.setAttribute("pendingContent", contentDao.countByStatus(FitnessContent.PENDING));

            Map<LocalDate, Integer> perDay = statsDao.workoutsPerDay(14);
            List<String> dayLabels = new ArrayList<>();
            for (LocalDate d : perDay.keySet()) {
                dayLabels.add(d.format(SHORT));
            }
            req.setAttribute("dayLabels", JsonUtil.stringArray(dayLabels));
            req.setAttribute("dayValues", JsonUtil.numberArray(perDay.values()));

            Map<String, Integer> types = statsDao.popularWorkoutTypes(6);
            req.setAttribute("typeLabels", JsonUtil.stringArray(types.keySet()));
            req.setAttribute("typeValues", JsonUtil.numberArray(types.values()));

            Map<String, Integer> participation = statsDao.challengeParticipation(6);
            req.setAttribute("participation", participation);
            req.setAttribute("challengeLabels", JsonUtil.stringArray(participation.keySet()));
            req.setAttribute("challengeValues", JsonUtil.numberArray(participation.values()));

            req.setAttribute("topMembers", statsDao.topMembers(5));
            req.setAttribute("activity", activityDao.recent(12));
            render(req, resp, "admin/dashboard.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }
}
