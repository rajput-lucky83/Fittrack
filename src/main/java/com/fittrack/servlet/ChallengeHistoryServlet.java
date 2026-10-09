package com.fittrack.servlet;

import com.fittrack.dao.ChallengeDao;
import com.fittrack.model.Challenge;
import com.fittrack.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/** Challenges the user took part in that are already finished, with the result. */
@WebServlet("/user/history")
public class ChallengeHistoryServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ChallengeDao challengeDao = new ChallengeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        try {
            List<Challenge> past = challengeDao.findJoinedByUser(user.getId()).stream()
                    .filter(Challenge::isOver)
                    .collect(Collectors.toList());

            long completed = past.stream().filter(Challenge::isCompleted).count();

            req.setAttribute("past", past);
            req.setAttribute("completedCount", completed);
            render(req, resp, "user/history.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }
}
