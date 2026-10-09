package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.model.ActivityEntry;
import com.fittrack.util.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * JSON feed polled by the admin dashboard every few seconds, which is how
 * the "real time" activity panel stays up to date without a page reload.
 */
@WebServlet("/admin/activity")
public class ActivityFeedServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            List<ActivityEntry> entries = activityDao.recent(12);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < entries.size(); i++) {
                ActivityEntry e = entries.get(i);
                if (i > 0) {
                    json.append(',');
                }
                json.append("{\"id\":").append(e.getId())
                    .append(",\"time\":").append(JsonUtil.quote(e.getTimeText()))
                    .append(",\"actor\":").append(JsonUtil.quote(e.getActorName()))
                    .append(",\"action\":").append(JsonUtil.quote(e.getAction()))
                    .append(",\"details\":").append(JsonUtil.quote(e.getDetails()))
                    .append('}');
            }
            json.append(']');
            resp.getWriter().write(json.toString());
        } catch (SQLException e) {
            log("Activity feed failed", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("[]");
        }
    }
}
