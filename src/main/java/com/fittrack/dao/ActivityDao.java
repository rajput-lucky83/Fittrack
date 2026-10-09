package com.fittrack.dao;

import com.fittrack.model.ActivityEntry;
import com.fittrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityDao {

    /** opens its own connection - use this for one-off log lines */
    public void log(Integer userId, String actor, String action, String details) {
        try (Connection con = DBConnection.getConnection()) {
            log(con, userId, actor, action, details);
        } catch (SQLException e) {
            // logging must never break the real request, so just print it
            System.err.println("Could not write activity log: " + e.getMessage());
        }
    }

    /** uses a connection the caller already has (handy inside a transaction) */
    public void log(Connection con, Integer userId, String actor, String action, String details)
            throws SQLException {
        String sql = "INSERT INTO activity_log (user_id, actor_name, action, details, created_at) "
                + "VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            if (userId == null) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, userId);
            }
            ps.setString(2, actor);
            ps.setString(3, action);
            ps.setString(4, details);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        }
    }

    public List<ActivityEntry> recent(int limit) throws SQLException {
        String sql = "SELECT id, user_id, actor_name, action, details, created_at FROM activity_log "
                + "ORDER BY id DESC LIMIT ?";
        List<ActivityEntry> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    private ActivityEntry map(ResultSet rs) throws SQLException {
        ActivityEntry e = new ActivityEntry();
        e.setId(rs.getInt("id"));
        e.setUserId(rs.getInt("user_id"));
        e.setActorName(rs.getString("actor_name"));
        e.setAction(rs.getString("action"));
        e.setDetails(rs.getString("details"));
        e.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return e;
    }
}
