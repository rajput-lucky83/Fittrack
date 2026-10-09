package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
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

/** Admin user management: list, create, edit, activate/deactivate, delete. */
@WebServlet("/admin/users")
public class AdminUserServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("users", userDao.findAll());

            int editId = intParam(req, "edit", 0);
            if (editId > 0) {
                req.setAttribute("editing", userDao.findById(editId));
            }
            req.setAttribute("roles", Role.values());
            render(req, resp, "admin/users.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = currentUser(req);
        String action = param(req, "action");

        try {
            switch (action) {
                case "create":
                    createUser(req, admin);
                    break;
                case "update":
                    updateUser(req, admin);
                    break;
                case "toggle":
                    toggleActive(req, admin);
                    break;
                case "delete":
                    deleteUser(req, admin);
                    break;
                default:
                    throw new ValidationException("Unknown action.");
            }
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
            if ("update".equals(action)) {
                redirect(req, resp, "/admin/users?edit=" + intParam(req, "id", 0));
                return;
            }
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, "/admin/users");
    }

    private void createUser(HttpServletRequest req, User admin) throws ValidationException, SQLException {
        String name = Validator.required(req.getParameter("name"), "Name");
        Validator.maxLength(name, 80, "Name");
        String email = Validator.email(req.getParameter("email"));
        String password = Validator.password(req.getParameter("password"));
        Role role = Role.fromString(req.getParameter("role"));

        if (userDao.emailTaken(email, 0)) {
            throw new DuplicateEmailException(email);
        }
        User u = new User(name, email, PasswordUtil.hash(password), role);
        userDao.save(u);
        activityDao.log(admin.getId(), admin.getName(), "ADMIN_CREATE_USER", name + " (" + role + ")");
        flash(req, "success", "Account created for " + name + ".");
    }

    private void updateUser(HttpServletRequest req, User admin) throws ValidationException, SQLException {
        int id = intParam(req, "id", 0);
        User target = userDao.findById(id);
        if (target == null) {
            throw new ValidationException("That user no longer exists.");
        }

        String name = Validator.required(req.getParameter("name"), "Name");
        Validator.maxLength(name, 80, "Name");
        String email = Validator.email(req.getParameter("email"));
        Role role = Role.fromString(req.getParameter("role"));

        if (userDao.emailTaken(email, id)) {
            throw new DuplicateEmailException(email);
        }
        if (id == admin.getId() && role != Role.ADMIN) {
            throw new ValidationException("You can't remove your own admin role.");
        }

        target.setName(name);
        target.setEmail(email);
        target.setRole(role);
        userDao.update(target);

        // optional: reset password if the admin typed a new one
        String newPassword = req.getParameter("password");
        if (!Validator.isBlank(newPassword)) {
            userDao.updatePassword(id, PasswordUtil.hash(Validator.password(newPassword)));
        }

        activityDao.log(admin.getId(), admin.getName(), "ADMIN_UPDATE_USER", name);
        flash(req, "success", "Details for " + name + " saved.");
    }

    private void toggleActive(HttpServletRequest req, User admin) throws ValidationException, SQLException {
        int id = intParam(req, "id", 0);
        if (id == admin.getId()) {
            throw new ValidationException("You can't deactivate your own account.");
        }
        User target = userDao.findById(id);
        if (target == null) {
            throw new ValidationException("That user no longer exists.");
        }
        target.setActive(!target.isActive());
        userDao.update(target);
        activityDao.log(admin.getId(), admin.getName(),
                target.isActive() ? "ADMIN_ACTIVATE_USER" : "ADMIN_DEACTIVATE_USER", target.getName());
        flash(req, "success", target.getName() + (target.isActive() ? " is active again." : " has been deactivated."));
    }

    private void deleteUser(HttpServletRequest req, User admin) throws ValidationException, SQLException {
        int id = intParam(req, "id", 0);
        if (id == admin.getId()) {
            throw new ValidationException("You can't delete your own account.");
        }
        User target = userDao.findById(id);
        if (target == null) {
            throw new ValidationException("That user no longer exists.");
        }
        userDao.delete(id);   // workouts, goals etc. go with it (ON DELETE CASCADE)
        activityDao.log(admin.getId(), admin.getName(), "ADMIN_DELETE_USER", target.getName());
        flash(req, "success", "Deleted " + target.getName() + " and all of their data.");
    }
}
