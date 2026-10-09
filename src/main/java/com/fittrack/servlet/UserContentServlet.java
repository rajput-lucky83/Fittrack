package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.ContentDao;
import com.fittrack.dao.SettingsDao;
import com.fittrack.exception.ValidationException;
import com.fittrack.model.FitnessContent;
import com.fittrack.model.User;
import com.fittrack.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Fitness guidance library. Members read approved tips and can submit their own;
 * submissions wait for an admin unless approval is switched off in the settings.
 */
@WebServlet("/user/content")
public class UserContentServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    static final List<String> CATEGORIES =
            Arrays.asList("Workout Plan", "Nutrition", "Recovery", "Motivation", "Technique");

    private final ContentDao contentDao = new ContentDao();
    private final SettingsDao settingsDao = new SettingsDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        try {
            req.setAttribute("approved", contentDao.findApproved());
            req.setAttribute("mine", contentDao.findBySubmitter(user.getId()));
            req.setAttribute("categories", CATEGORIES);
            render(req, resp, "user/content.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        try {
            String title = Validator.required(req.getParameter("title"), "Title");
            Validator.maxLength(title, 120, "Title");
            String body = Validator.required(req.getParameter("body"), "Content");
            Validator.maxLength(body, 3000, "Content");

            String category = param(req, "category");
            if (!CATEGORIES.contains(category)) {
                throw new ValidationException("Pick a category.");
            }

            boolean needsApproval = settingsDao.getBoolean("require_content_approval", true);

            FitnessContent item = new FitnessContent();
            item.setTitle(title);
            item.setCategory(category);
            item.setBody(body);
            item.setSubmittedBy(user.getId());
            item.setStatus(needsApproval ? FitnessContent.PENDING : FitnessContent.APPROVED);
            contentDao.save(item);

            activityDao.log(user.getId(), user.getName(), "SUBMIT_CONTENT", "\"" + title + "\"");
            flash(req, "success", needsApproval
                    ? "Thanks! Your post was sent to the admin for review."
                    : "Your post is now live.");
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, "/user/content");
    }
}
