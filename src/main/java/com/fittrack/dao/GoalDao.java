package com.fittrack.dao;

import com.fittrack.model.Goal;
import com.fittrack.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoalDao {

    public List<Goal> findByUser(int userId) throws SQLException {
        String sql = "SELECT id, user_id, title, metric, target_value, created_on FROM goals "
                + "WHERE user_id = ? ORDER BY id DESC";
        List<Goal> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Goal g = new Goal();
                    g.setId(rs.getInt("id"));
                    g.setUserId(rs.getInt("user_id"));
                    g.setTitle(rs.getString("title"));
                    g.setMetric(rs.getString("metric"));
                    g.setTargetValue(rs.getInt("target_value"));
                    g.setCreatedOn(rs.getDate("created_on").toLocalDate());
                    list.add(g);
                }
            }
        }
        return list;
    }

    public boolean save(Goal g) throws SQLException {
        String sql = "INSERT INTO goals (user_id, title, metric, target_value, created_on) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, g.getUserId());
            ps.setString(2, g.getTitle());
            ps.setString(3, g.getMetric());
            ps.setInt(4, g.getTargetValue());
            ps.setDate(5, Date.valueOf(g.getCreatedOn()));
            return ps.executeUpdate() == 1;
        }
    }

    public boolean deleteForUser(int goalId, int userId) throws SQLException {
        String sql = "DELETE FROM goals WHERE id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, goalId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }
}
