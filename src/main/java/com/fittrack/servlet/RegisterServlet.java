package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.SettingsDao;
import com.fittrack.dao.UserDao;
import com.fittrack.exception.DuplicateEmailException;
import com.fittrack.exception.ValidationException;
import com.fittrack.model.Role;
import com.fittrack.model.User;
import com.fittrack.util.PasswordUtil;
import com.fittrack.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();
    private final SettingsDao settingsDao = new SettingsDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("registrationOpen", settingsDao.getBoolean("allow_registration", true));
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        render(req, resp, "register.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            boolean open = settingsDao.getBoolean("allow_registration", true);
            req.setAttribute("registrationOpen", open);
            if (!open) {
                req.setAttribute("error", "New registrations are switched off right now.");
                render(req, resp, "register.jsp");
                return;
            }

            try {
                String name = Validator.required(req.getParameter("name"), "Name");
                Validator.maxLength(name, 80, "Name");
                String email = Validator.email(req.getParameter("email"));
                String password = Validator.password(req.getParameter("password"));
                if (!password.equals(req.getParameter("confirm"))) {
                    throw new ValidationException("The two passwords don't match.");
                }
                if (userDao.emailTaken(email, 0)) {
                    throw new DuplicateEmailException(email);
                }

                User user = new User(name, email, PasswordUtil.hash(password), Role.USER);
                userDao.save(user);
                activityDao.log(user.getId(), user.getName(), "REGISTER", "New member registered");

                flash(req, "success", "Account created. You can sign in now.");
                redirect(req, resp, "/login");

            } catch (ValidationException e) {
                req.setAttribute("error", e.getMessage());
                req.setAttribute("name", param(req, "name"));
                req.setAttribute("email", param(req, "email"));
                render(req, resp, "register.jsp");
            }
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }
}
