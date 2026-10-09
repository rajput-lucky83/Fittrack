package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.ChallengeDao;
import com.fittrack.exception.ValidationException;
import com.fittrack.model.Challenge;
import com.fittrack.model.User;
import com.fittrack.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/** Admins create and remove the challenges that users can join. */
@WebServlet("/admin/challenges")
public class AdminChallengeServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ChallengeDao challengeDao = new ChallengeDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("challenges", challengeDao.findAll());
            req.setAttribute("today", LocalDate.now().toString());
            render(req, resp, "admin/challenges.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = currentUser(req);
        String action = param(req, "action");

        try {
            if ("delete".equals(action)) {
                Challenge c = challengeDao.findById(intParam(req, "id", 0));
                if (c == null) {
                    throw new ValidationException("That challenge no longer exists.");
                }
                challengeDao.delete(c.getId());
                activityDao.log(admin.getId(), admin.getName(), "CHALLENGE_DELETED", "\"" + c.getTitle() + "\"");
                flash(req, "success", "Challenge deleted.");
            } else {
                Challenge c = readForm(req);
                c.setCreatedBy(admin.getId());
                challengeDao.save(c);
                activityDao.log(admin.getId(), admin.getName(), "CHALLENGE_CREATED", "\"" + c.getTitle() + "\"");
                flash(req, "success", "Challenge \"" + c.getTitle() + "\" created.");
            }
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, "/admin/challenges");
    }

    private Challenge readForm(HttpServletRequest req) throws ValidationException {
        Challenge c = new Challenge();
        String title = Validator.required(req.getParameter("title"), "Title");
        Validator.maxLength(title, 100, "Title");
        c.setTitle(title);

        String desc = param(req, "description");
        Validator.maxLength(desc, 400, "Description");
        c.setDescription(desc.isEmpty() ? null : desc);

        String metric = param(req, "metric");
        if (!"MINUTES".equals(metric) && !"WORKOUTS".equals(metric)) {
            throw new ValidationException("Choose whether the challenge counts minutes or workouts.");
        }
        c.setMetric(metric);
        c.setTargetValue(Validator.positiveInt(req.getParameter("target"), "Target", 100000));

        LocalDate start = Validator.date(req.getParameter("startDate"), "Start date");
        LocalDate end = Validator.date(req.getParameter("endDate"), "End date");
        if (end.isBefore(start)) {
            throw new ValidationException("The end date must be on or after the start date.");
        }
        c.setStartDate(start);
        c.setEndDate(end);
        return c;
    }
}
