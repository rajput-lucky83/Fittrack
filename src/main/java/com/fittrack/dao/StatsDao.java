package com.fittrack.dao;

import com.fittrack.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

/** Read-only queries for the admin statistics page. */
public class StatsDao {

    private int single(String sql) throws SQLException {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int totalMembers() throws SQLException {
        return single("SELECT COUNT(*) FROM users WHERE role = 'USER'");
    }

    public int activeMembers() throws SQLException {
        return single("SELECT COUNT(*) FROM users WHERE role = 'USER' AND active = 1");
    }

    public int totalWorkouts() throws SQLException {
        return single("SELECT COUNT(*) FROM workouts");
    }

    public int totalMinutes() throws SQLException {
        return single("SELECT COALESCE(SUM(duration_min),0) FROM workouts");
    }

    public int runningChallenges() throws SQLException {
        return single("SELECT COUNT(*) FROM challenges WHERE CURDATE() BETWEEN start_date AND end_date");
    }

    /** members who logged at least one workout in the last 7 days */
    public int activeThisWeek() throws SQLException {
        return single("SELECT COUNT(DISTINCT user_id) FROM workouts WHERE workout_date >= CURDATE() - INTERVAL 6 DAY");
    }

    /** workouts logged per day for the last N days */
    public Map<LocalDate, Integer> workoutsPerDay(int days) throws SQLException {
        LocalDate start = LocalDate.now().minusDays(days - 1L);
        Map<LocalDate, Integer> result = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            result.put(start.plusDays(i), 0);
        }
        String sql = "SELECT workout_date, COUNT(*) FROM workouts WHERE workout_date >= ? GROUP BY workout_date";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getDate(1).toLocalDate(), rs.getInt(2));
                }
            }
        }
        return result;
    }

    public Map<String, Integer> popularWorkoutTypes(int limit) throws SQLException {
        String sql = "SELECT workout_type, COUNT(*) AS c FROM workouts GROUP BY workout_type "
                + "ORDER BY c DESC LIMIT ?";
        return pairs(sql, limit);
    }

    /** challenge title -> number of participants */
    public Map<String, Integer> challengeParticipation(int limit) throws SQLException {
        String sql = "SELECT c.title, COUNT(p.user_id) AS n FROM challenges c "
                + "LEFT JOIN challenge_participants p ON p.challenge_id = c.id "
                + "GROUP BY c.id, c.title ORDER BY n DESC, c.id DESC LIMIT ?";
        return pairs(sql, limit);
    }

    /** the most active members over the last 30 days (by minutes) */
    public Map<String, Integer> topMembers(int limit) throws SQLException {
        String sql = "SELECT u.name, SUM(w.duration_min) AS m FROM workouts w JOIN users u ON u.id = w.user_id "
                + "WHERE w.workout_date >= CURDATE() - INTERVAL 29 DAY GROUP BY u.id, u.name "
                + "ORDER BY m DESC LIMIT ?";
        return pairs(sql, limit);
    }

    private Map<String, Integer> pairs(String sql, int limit) throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString(1), rs.getInt(2));
                }
            }
        }
        return map;
    }
}
