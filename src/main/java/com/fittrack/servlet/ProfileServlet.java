package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.UserDao;
import com.fittrack.exception.DuplicateEmailException;
import com.fittrack.exception.ValidationException;
import com.fittrack.model.User;
import com.fittrack.util.PasswordUtil;
import com.fittrack.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet("/user/profile")
public class ProfileServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;
    private static final List<String> GOALS = Arrays.asList("GENERAL", "WEIGHT_LOSS", "MUSCLE_GAIN", "ENDURANCE");

    private final UserDao userDao = new UserDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = currentUser(req);
        try {
            // reload so the form always shows what is really stored
            req.setAttribute("profile", userDao.findById(sessionUser.getId()));
            render(req, resp, "user/profile.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User sessionUser = currentUser(req);
        String action = param(req, "action");

        try {
            User stored = userDao.findById(sessionUser.getId());

            if ("password".equals(action)) {
                changePassword(req, stored);
                flash(req, "success", "Password changed.");
            } else {
                updateDetails(req, stored);
                // keep the session copy in sync (name shows in the sidebar, weight is used for calories)
                req.getSession().setAttribute("user", userDao.findById(stored.getId()));
                flash(req, "success", "Profile updated.");
            }
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, "/user/profile");
    }

    private void updateDetails(HttpServletRequest req, User user) throws ValidationException, SQLException {
        String name = Validator.required(req.getParameter("name"), "Name");
        Validator.maxLength(name, 80, "Name");
        String email = Validator.email(req.getParameter("email"));
        if (userDao.emailTaken(email, user.getId())) {
            throw new DuplicateEmailException(email);
        }

        Double weight = Validator.optionalDecimal(req.getParameter("weight"), "Weight", 20, 300);
        Double height = Validator.optionalDecimal(req.getParameter("height"), "Height", 80, 250);

        String goal = param(req, "goal");
        if (!GOALS.contains(goal)) {
            goal = "GENERAL";
        }

        user.setName(name);
        user.setEmail(email);
        user.setWeightKg(weight);
        user.setHeightCm(height);
        user.setFitnessGoal(goal);
        userDao.updateProfile(user);
        activityDao.log(user.getId(), user.getName(), "UPDATE_PROFILE", "Profile details changed");
    }

    private void changePassword(HttpServletRequest req, User user) throws ValidationException, SQLException {
        String current = req.getParameter("currentPassword");
        if (!PasswordUtil.verify(current == null ? "" : current, user.getPasswordHash())) {
            throw new ValidationException("Your current password is not correct.");
        }
        String newPass = Validator.password(req.getParameter("newPassword"));
        if (!newPass.equals(req.getParameter("confirmPassword"))) {
            throw new ValidationException("The new passwords don't match.");
        }
        userDao.updatePassword(user.getId(), PasswordUtil.hash(newPass));
        activityDao.log(user.getId(), user.getName(), "CHANGE_PASSWORD", "Password changed");
    }
}
