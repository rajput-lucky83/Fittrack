package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.ContentDao;
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
import java.util.List;
import java.util.stream.Collectors;

/** Review queue for user-submitted fitness content. */
@WebServlet("/admin/content")
public class AdminContentServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ContentDao contentDao = new ContentDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String filter = param(req, "status").toUpperCase();
        try {
            List<FitnessContent> items = contentDao.findAll();
            if (!filter.isEmpty() && !"ALL".equals(filter)) {
                items = items.stream()
                        .filter(c -> c.getStatus().equals(filter))
                        .collect(Collectors.toList());
            }
            req.setAttribute("items", items);
            req.setAttribute("filter", filter.isEmpty() ? "ALL" : filter);
            req.setAttribute("pendingCount", contentDao.countByStatus(FitnessContent.PENDING));
            render(req, resp, "admin/content.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User admin = currentUser(req);
        String action = param(req, "action");
        String backTo = "/admin/content";
        String filter = param(req, "filter");
        if (!filter.isEmpty()) {
            backTo += "?status=" + filter;
        }

        try {
            FitnessContent item = contentDao.findById(intParam(req, "id", 0));
            if (item == null) {
                throw new ValidationException("That post no longer exists.");
            }

            switch (action) {
                case "approve":
                    item.setStatus(FitnessContent.APPROVED);
                    item.setAdminNote(null);
                    contentDao.update(item);
                    activityDao.log(admin.getId(), admin.getName(), "CONTENT_APPROVED", "\"" + item.getTitle() + "\"");
                    flash(req, "success", "Approved \"" + item.getTitle() + "\".");
                    break;
                case "reject":
                    String note = param(req, "note");
                    Validator.maxLength(note, 255, "Note");
                    item.setStatus(FitnessContent.REJECTED);
                    item.setAdminNote(note.isEmpty() ? null : note);
                    contentDao.update(item);
                    activityDao.log(admin.getId(), admin.getName(), "CONTENT_REJECTED", "\"" + item.getTitle() + "\"");
                    flash(req, "success", "Rejected \"" + item.getTitle() + "\".");
                    break;
                case "delete":
                    contentDao.delete(item.getId());
                    activityDao.log(admin.getId(), admin.getName(), "CONTENT_DELETED", "\"" + item.getTitle() + "\"");
                    flash(req, "success", "Post deleted.");
                    break;
                default:
                    throw new ValidationException("Unknown action.");
            }
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, backTo);
    }
}
