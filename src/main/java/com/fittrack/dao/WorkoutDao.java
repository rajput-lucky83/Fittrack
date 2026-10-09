package com.fittrack.dao;

import com.fittrack.model.Intensity;
import com.fittrack.model.Workout;
import com.fittrack.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class WorkoutDao implements CrudDao<Workout> {

    private static final String COLUMNS =
            "id, user_id, workout_type, duration_min, intensity, calories, workout_date, notes";

    private Workout map(ResultSet rs) throws SQLException {
        Workout w = new Workout();
        w.setId(rs.getInt("id"));
        w.setUserId(rs.getInt("user_id"));
        w.setType(rs.getString("workout_type"));
        w.setDurationMin(rs.getInt("duration_min"));
        w.setIntensity(Intensity.fromString(rs.getString("intensity")));
        w.setCalories(rs.getInt("calories"));
        w.setWorkoutDate(rs.getDate("workout_date").toLocalDate());
        w.setNotes(rs.getString("notes"));
        return w;
    }

    @Override
    public List<Workout> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM workouts ORDER BY workout_date DESC, id DESC";
        List<Workout> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    @Override
    public Workout findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM workouts WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public List<Workout> findByUser(int userId) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM workouts WHERE user_id = ? ORDER BY workout_date DESC, id DESC";
        List<Workout> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<Workout> findSince(int userId, LocalDate from) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM workouts WHERE user_id = ? AND workout_date >= ? "
                + "ORDER BY workout_date DESC, id DESC";
        List<Workout> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(from));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /** only returns the workout if it really belongs to this user */
    public Workout findByIdForUser(int id, int userId) throws SQLException {
        Workout w = findById(id);
        return (w != null && w.getUserId() == userId) ? w : null;
    }

    @Override
    public boolean save(Workout w) throws SQLException {
        String sql = "INSERT INTO workouts (user_id, workout_type, duration_min, intensity, calories, "
                + "workout_date, notes) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, w.getUserId());
            ps.setString(2, w.getType());
            ps.setInt(3, w.getDurationMin());
            ps.setString(4, w.getIntensity().name());
            ps.setInt(5, w.getCalories());
            ps.setDate(6, Date.valueOf(w.getWorkoutDate()));
            ps.setString(7, w.getNotes());
            boolean ok = ps.executeUpdate() == 1;
            if (ok) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        w.setId(keys.getInt(1));
                    }
                }
            }
            return ok;
        }
    }

    @Override
    public boolean update(Workout w) throws SQLException {
        // user_id in the WHERE clause so nobody can edit someone else's row
        String sql = "UPDATE workouts SET workout_type = ?, duration_min = ?, intensity = ?, calories = ?, "
                + "workout_date = ?, notes = ? WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, w.getType());
            ps.setInt(2, w.getDurationMin());
            ps.setString(3, w.getIntensity().name());
            ps.setInt(4, w.getCalories());
            ps.setDate(5, Date.valueOf(w.getWorkoutDate()));
            ps.setString(6, w.getNotes());
            ps.setInt(7, w.getId());
            ps.setInt(8, w.getUserId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM workouts WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteForUser(int id, int userId) throws SQLException {
        String sql = "DELETE FROM workouts WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    // ------------------------------------------------------------------
    // numbers for progress reports
    // ------------------------------------------------------------------

    /** metric is MINUTES, CALORIES or WORKOUTS; both dates are inclusive */
    public int metricBetween(int userId, String metric, LocalDate from, LocalDate to) throws SQLException {
        String expr;
        switch (metric) {
            case "MINUTES":  expr = "COALESCE(SUM(duration_min),0)"; break;
            case "CALORIES": expr = "COALESCE(SUM(calories),0)"; break;
            default:         expr = "COUNT(*)";
        }
        String sql = "SELECT " + expr + " FROM workouts WHERE user_id = ? AND workout_date BETWEEN ? AND ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** lifetime totals: workouts, minutes, calories */
    public Map<String, Integer> lifetimeTotals(int userId) throws SQLException {
        String sql = "SELECT COUNT(*), COALESCE(SUM(duration_min),0), COALESCE(SUM(calories),0) "
                + "FROM workouts WHERE user_id = ?";
        Map<String, Integer> totals = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                totals.put("workouts", rs.getInt(1));
                totals.put("minutes", rs.getInt(2));
                totals.put("calories", rs.getInt(3));
            }
        }
        return totals;
    }

    /** minutes per day for the last N days (days with nothing logged are 0) */
    public Map<LocalDate, Integer> minutesPerDay(int userId, int days) throws SQLException {
        return dailyTotals(userId, days, "duration_min");
    }

    public Map<LocalDate, Integer> caloriesPerDay(int userId, int days) throws SQLException {
        return dailyTotals(userId, days, "calories");
    }

    private Map<LocalDate, Integer> dailyTotals(int userId, int days, String column) throws SQLException {
        LocalDate start = LocalDate.now().minusDays(days - 1L);
        Map<LocalDate, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            result.put(start.plusDays(i), 0);
        }
        // column is never user input, only one of two constants from this class
        String sql = "SELECT workout_date, SUM(" + column + ") FROM workouts "
                + "WHERE user_id = ? AND workout_date >= ? GROUP BY workout_date";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(start));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getDate(1).toLocalDate(), rs.getInt(2));
                }
            }
        }
        return result;
    }

    public Map<String, Integer> minutesByType(int userId) throws SQLException {
        String sql = "SELECT workout_type, SUM(duration_min) AS total FROM workouts WHERE user_id = ? "
                + "GROUP BY workout_type ORDER BY total DESC";
        Map<String, Integer> result = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getString(1), rs.getInt(2));
                }
            }
        }
        return result;
    }

    /**
     * Number of days in a row (ending today or yesterday) with at least one workout.
     */
    public int currentStreak(int userId) throws SQLException {
        String sql = "SELECT DISTINCT workout_date FROM workouts WHERE user_id = ? ORDER BY workout_date DESC";
        TreeSet<LocalDate> dates = new TreeSet<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dates.add(rs.getDate(1).toLocalDate());
                }
            }
        }
        LocalDate day = LocalDate.now();
        if (!dates.contains(day)) {
            // the streak is still alive if they trained yesterday
            day = day.minusDays(1);
        }
        int streak = 0;
        while (dates.contains(day)) {
            streak++;
            day = day.minusDays(1);
        }
        return streak;
    }
}
