package com.fittrack.servlet;

import com.fittrack.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/** Shared helpers so the real servlets stay short. */
public abstract class BaseServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected User currentUser(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        return session == null ? null : (User) session.getAttribute("user");
    }

    /** one-shot message shown on the next page (success / error) */
    protected void flash(HttpServletRequest req, String type, String message) {
        HttpSession session = req.getSession();
        session.setAttribute("flashType", type);
        session.setAttribute("flashMsg", message);
    }

    protected void render(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/" + view).forward(req, resp);
    }

    protected void redirect(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        resp.sendRedirect(req.getContextPath() + path);
    }

    /** used when a database call blows up */
    protected void fail(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        log("Request failed: " + req.getRequestURI(), e);
        req.setAttribute("errorMessage", "Something went wrong while talking to the database. Please try again.");
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        render(req, resp, "common/error.jsp");
    }

    protected int intParam(HttpServletRequest req, String name, int fallback) {
        try {
            return Integer.parseInt(req.getParameter(name).trim());
        } catch (NumberFormatException | NullPointerException e) {
            return fallback;
        }
    }

    protected String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return v == null ? "" : v.trim();
    }
}
