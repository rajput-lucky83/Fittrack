package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        if (user != null) {
            activityDao.log(user.getId(), user.getName(), "LOGOUT", "Signed out");
        }
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        // flash needs a session, so start a new one just for the message
        flash(req, "success", "You have been signed out.");
        redirect(req, resp, "/login");
    }
}
