package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.UserDao;
import com.fittrack.model.User;
import com.fittrack.util.PasswordUtil;
import com.fittrack.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User already = currentUser(req);
        if (already != null) {
            redirect(req, resp, already.getHomePath());
            return;
        }
        render(req, resp, "login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = param(req, "email").toLowerCase();
        String password = req.getParameter("password");

        if (Validator.isBlank(email) || Validator.isBlank(password)) {
            req.setAttribute("error", "Enter both email and password.");
            req.setAttribute("email", email);
            render(req, resp, "login.jsp");
            return;
        }

        try {
            User user = userDao.findByEmail(email);

            // same message for "no such user" and "wrong password" on purpose
            if (user == null || !PasswordUtil.verify(password, user.getPasswordHash())) {
                req.setAttribute("error", "Incorrect email or password.");
                req.setAttribute("email", email);
                render(req, resp, "login.jsp");
                return;
            }
            if (!user.isActive()) {
                req.setAttribute("error", "This account has been deactivated. Please contact an administrator.");
                req.setAttribute("email", email);
                render(req, resp, "login.jsp");
                return;
            }

            // fresh session id after login (protects against session fixation)
            HttpSession old = req.getSession(false);
            if (old != null) {
                old.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);

            activityDao.log(user.getId(), user.getName(), "LOGIN", user.getRole() + " signed in");
            redirect(req, resp, user.getHomePath());

        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }
}
