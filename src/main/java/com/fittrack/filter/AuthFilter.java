package com.fittrack.filter;

import com.fittrack.model.User;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Guards everything under /user and /admin.
 * - not logged in      -> back to the login page
 * - USER on /admin/*   -> bounced to their own dashboard
 * - ADMIN on /user/*   -> sent to the admin dashboard
 */
@WebFilter(urlPatterns = {"/user/*", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        String ctx = req.getContextPath();
        String path = req.getRequestURI().substring(ctx.length());

        if (user == null) {
            resp.sendRedirect(ctx + "/login");
            return;
        }

        boolean adminArea = path.startsWith("/admin");
        if (adminArea && !user.isAdmin()) {
            resp.sendRedirect(ctx + user.getHomePath());
            return;
        }
        if (!adminArea && user.isAdmin()) {
            resp.sendRedirect(ctx + user.getHomePath());
            return;
        }

        // pages with personal data shouldn't be served from the browser cache after logout
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        chain.doFilter(request, response);
    }
}
