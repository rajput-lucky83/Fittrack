package com.fittrack.servlet;

import com.fittrack.dao.GoalDao;
import com.fittrack.exception.ValidationException;
import com.fittrack.model.Goal;
import com.fittrack.model.User;
import com.fittrack.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/** Adds / removes the weekly goals shown on the progress page. */
@WebServlet("/user/goals")
public class GoalServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final List<String> METRICS = Arrays.asList("MINUTES", "CALORIES", "WORKOUTS");

    private final GoalDao goalDao = new GoalDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        redirect(req, resp, "/user/progress");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String action = param(req, "action");

        try {
            if ("delete".equals(action)) {
                boolean gone = goalDao.deleteForUser(intParam(req, "id", 0), user.getId());
                flash(req, gone ? "success" : "error", gone ? "Goal removed." : "Goal not found.");
            } else {
                String title = Validator.required(req.getParameter("title"), "Goal name");
                Validator.maxLength(title, 100, "Goal name");

                String metric = param(req, "metric");
                if (!METRICS.contains(metric)) {
                    throw new ValidationException("Choose what the goal measures.");
                }
                int target = Validator.positiveInt(req.getParameter("target"), "Target", 100000);

                Goal g = new Goal();
                g.setUserId(user.getId());
                g.setTitle(title);
                g.setMetric(metric);
                g.setTargetValue(target);
                g.setCreatedOn(LocalDate.now());
                goalDao.save(g);
                flash(req, "success", "Goal added. It is measured from Monday to Sunday each week.");
            }
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, "/user/progress");
    }
}
