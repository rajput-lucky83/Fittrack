package com.fittrack.servlet;

import com.fittrack.dao.ActivityDao;
import com.fittrack.dao.SettingsDao;
import com.fittrack.dao.WorkoutDao;
import com.fittrack.exception.ValidationException;
import com.fittrack.model.Intensity;
import com.fittrack.model.User;
import com.fittrack.model.Workout;
import com.fittrack.util.CalorieCalculator;
import com.fittrack.util.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/** Workout log: list, add, edit and delete. */
@WebServlet("/user/workouts")
public class WorkoutServlet extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private final WorkoutDao workoutDao = new WorkoutDao();
    private final SettingsDao settingsDao = new SettingsDao();
    private final ActivityDao activityDao = new ActivityDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        try {
            List<Workout> workouts = workoutDao.findByUser(user.getId());

            int editId = intParam(req, "edit", 0);
            if (editId > 0) {
                Workout editing = workoutDao.findByIdForUser(editId, user.getId());
                req.setAttribute("editing", editing);   // null if it isn't theirs
            }

            req.setAttribute("workouts", workouts);
            req.setAttribute("types", CalorieCalculator.workoutTypes());
            req.setAttribute("intensities", Intensity.values());
            req.setAttribute("today", LocalDate.now().toString());
            req.setAttribute("maxMinutes", settingsDao.getInt("max_workout_minutes", 300));
            render(req, resp, "user/workouts.jsp");
        } catch (SQLException e) {
            fail(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = currentUser(req);
        String action = param(req, "action");

        try {
            switch (action) {
                case "add": {
                    Workout w = readForm(req, user);
                    workoutDao.save(w);
                    activityDao.log(user.getId(), user.getName(), "LOG_WORKOUT",
                            w.getType() + ", " + w.getDurationMin() + " min");
                    flash(req, "success", "Workout logged. Estimated burn: " + w.getCalories() + " kcal.");
                    break;
                }
                case "update": {
                    int id = intParam(req, "id", 0);
                    if (workoutDao.findByIdForUser(id, user.getId()) == null) {
                        throw new ValidationException("That workout was not found.");
                    }
                    Workout w = readForm(req, user);
                    w.setId(id);
                    workoutDao.update(w);
                    flash(req, "success", "Workout updated.");
                    break;
                }
                case "delete": {
                    int id = intParam(req, "id", 0);
                    if (workoutDao.deleteForUser(id, user.getId())) {
                        flash(req, "success", "Workout deleted.");
                    } else {
                        flash(req, "error", "That workout was not found.");
                    }
                    break;
                }
                default:
                    flash(req, "error", "Unknown action.");
            }
        } catch (ValidationException e) {
            flash(req, "error", e.getMessage());
            // send them back to the edit form if that is where they came from
            if ("update".equals(action)) {
                redirect(req, resp, "/user/workouts?edit=" + intParam(req, "id", 0));
                return;
            }
        } catch (SQLException e) {
            fail(req, resp, e);
            return;
        }
        redirect(req, resp, "/user/workouts");
    }

    private Workout readForm(HttpServletRequest req, User user) throws ValidationException, SQLException {
        String type = Validator.required(req.getParameter("type"), "Workout type");
        if (!CalorieCalculator.isKnownType(type)) {
            throw new ValidationException("Please pick a workout type from the list.");
        }

        int maxMinutes = settingsDao.getInt("max_workout_minutes", 300);
        int minutes = Validator.positiveInt(req.getParameter("duration"), "Duration", maxMinutes);
        Intensity intensity = Intensity.fromString(req.getParameter("intensity"));

        LocalDate date = Validator.date(req.getParameter("date"), "Date");
        if (date.isAfter(LocalDate.now())) {
            throw new ValidationException("The workout date can't be in the future.");
        }

        String notes = param(req, "notes");
        Validator.maxLength(notes, 255, "Notes");

        Workout w = new Workout(user.getId(), type, minutes, intensity, date, notes.isEmpty() ? null : notes);

        double weight = user.getWeightKg() != null
                ? user.getWeightKg()
                : settingsDao.getInt("default_weight_kg", 65);
        w.setCalories(CalorieCalculator.estimate(type, minutes, intensity, weight));
        return w;
    }
}
