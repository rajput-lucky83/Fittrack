package com.fittrack.servlet;

import com.fittrack.dao.ChallengeDao;
import com.fittrack.exception.ValidationException;
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

/** Browse open challenges and join / leave them. */
@WebServlet("/user/challenges")
public class ChallengeServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final ChallengeDao challengeDao = new ChallengeDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        try {
            // finished challenges live on the history page
            List<Challenge> open = challengeDao.findAllForUser(user.getId()).stream()
                    .filter(c -> !c.isOver())
                    .collect(Collectors.toList());
            List<Challenge> mine = challengeDao.findJoinedByUser(user.getId()).stream()
                    .filter(c -> !c.isOver())
                    .collect(Collectors.toList());

            req.setAttribute("available", open);
            req.setAttribute("joined", mine);
            render(req, resp, "user/challenges.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String action = param(req, "action");
        int challengeId = intParam(req, "challengeId", 0);

        try {
            Challenge challenge = challengeDao.findById(challengeId);
            if (challenge == null) {
                throw new ValidationException("That challenge does not exist any more.");
            }
            if (challenge.isOver()) {
                throw new ValidationException("This challenge has already ended.");
            }

            if ("join".equals(action)) {
                if (challengeDao.hasJoined(user.getId(), challengeId)) {
                    throw new ValidationException("You are already part of this challenge.");
                }
                challengeDao.join(user.getId(), user.getName(), challenge);
                flash(req, "success", "You joined \"" + challenge.getTitle() + "\". Your logged workouts between "
                        + challenge.getStartDate() + " and " + challenge.getEndDate() + " count towards it.");
            } else if ("leave".equals(action)) {
                challengeDao.leave(user.getId(), challengeId);
                flash(req, "success", "You left \"" + challenge.getTitle() + "\".");
            } else {
                throw new ValidationException("Unknown action.");
            }
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, "/user/challenges");
    }
}
