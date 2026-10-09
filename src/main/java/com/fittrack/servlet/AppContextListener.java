package com.fittrack.servlet;

import com.fittrack.dao.SettingsDao;
import com.fittrack.dao.UserDao;
import com.fittrack.model.Role;
import com.fittrack.model.User;
import com.fittrack.util.PasswordUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.SQLException;

/**
 * Runs once when Tomcat starts the app:
 *  - creates the first admin account if there isn't one yet
 *  - loads the site name so every page can show it
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    static final String DEFAULT_ADMIN_EMAIL = "admin@fittrack.com";
    static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext ctx = event.getServletContext();
        ctx.setAttribute("siteName", "FitTrack");

        try {
            UserDao userDao = new UserDao();
            if (userDao.countByRole(Role.ADMIN) == 0) {
                User admin = new User("System Admin", DEFAULT_ADMIN_EMAIL,
                        PasswordUtil.hash(DEFAULT_ADMIN_PASSWORD), Role.ADMIN);
                userDao.save(admin);
                ctx.log("Created default admin account: " + DEFAULT_ADMIN_EMAIL);
            }
            ctx.setAttribute("siteName", new SettingsDao().get("site_name", "FitTrack"));
        } catch (SQLException e) {
            // most likely the schema hasn't been imported yet
            ctx.log("Database not ready at startup - did you run sql/schema.sql? " + e.getMessage());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // nothing to clean up, connections are closed after every query
    }
}
