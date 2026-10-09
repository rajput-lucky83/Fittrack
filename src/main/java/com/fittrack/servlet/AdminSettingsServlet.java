package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.SettingsDao;
import com.fittrack.exception.ValidationException;
import com.fittrack.model.User;
import com.fittrack.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/** System-wide settings panel. */
@WebServlet("/admin/settings")
public class AdminSettingsServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final SettingsDao settingsDao = new SettingsDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            req.setAttribute("settings", settingsDao.getAll());
            render(req, resp, "admin/settings.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = currentUser(req);
        try {
            String siteName = Validator.required(req.getParameter("site_name"), "Site name");
            Validator.maxLength(siteName, 40, "Site name");

            int maxMinutes = Validator.positiveInt(req.getParameter("max_workout_minutes"),
                    "Maximum workout length", 1440);
            int defaultWeight = Validator.positiveInt(req.getParameter("default_weight_kg"),
                    "Default weight", 300);

            // checkboxes are only sent when ticked
            boolean allowRegistration = req.getParameter("allow_registration") != null;
            boolean requireApproval = req.getParameter("require_content_approval") != null;

            settingsDao.set("site_name", siteName);
            settingsDao.set("max_workout_minutes", String.valueOf(maxMinutes));
            settingsDao.set("default_weight_kg", String.valueOf(defaultWeight));
            settingsDao.set("allow_registration", String.valueOf(allowRegistration));
            settingsDao.set("require_content_approval", String.valueOf(requireApproval));

            // the name is shown on every page, so refresh the cached copy too
            getServletContext().setAttribute("siteName", siteName);

            activityDao.log(admin.getId(), admin.getName(), "SETTINGS_UPDATED", "System settings changed");
            flash(req, "success", "Settings saved.");
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, "/admin/settings");
    }
}
