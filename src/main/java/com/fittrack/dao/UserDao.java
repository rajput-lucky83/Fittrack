package com.fittrack.dao;

import com.fittrack.model.Role;
import com.fittrack.model.User;
import com.fittrack.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class UserDao implements CrudDao<User> {

    private static final String COLUMNS =
            "id, name, email, password_hash, role, active, weight_kg, height_cm, fitness_goal, joined_on";

    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(Role.fromString(rs.getString("role")));
        u.setActive(rs.getBoolean("active"));

        double w = rs.getDouble("weight_kg");
        u.setWeightKg(rs.wasNull() ? null : w);
        double h = rs.getDouble("height_cm");
        u.setHeightCm(rs.wasNull() ? null : h);

        u.setFitnessGoal(rs.getString("fitness_goal"));
        Date joined = rs.getDate("joined_on");
        if (joined != null) {
            u.setJoinedOn(joined.toLocalDate());
        }
        return u;
    }

    private void setNullableDouble(PreparedStatement ps, int index, Double value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DECIMAL);
        } else {
            ps.setDouble(index, value);
        }
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users ORDER BY id";
        List<User> list = new ArrayList<>();
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
    public User findById(int id) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT " + COLUMNS + " FROM users WHERE email = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    /** true if some OTHER user (id != excludeId) already uses this email */
    public boolean emailTaken(String email, int excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND id <> ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    @Override
    public boolean save(User u) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, role, active, weight_kg, height_cm, "
                + "fitness_goal, joined_on) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPasswordHash());
            ps.setString(4, u.getRole().name());
            ps.setBoolean(5, u.isActive());
            setNullableDouble(ps, 6, u.getWeightKg());
            setNullableDouble(ps, 7, u.getHeightCm());
            ps.setString(8, u.getFitnessGoal());
            ps.setDate(9, Date.valueOf(u.getJoinedOn()));

            boolean ok = ps.executeUpdate() == 1;
            if (ok) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        u.setId(keys.getInt(1));
                    }
                }
            }
            return ok;
        }
    }

    /** admin edit: name, email, role and active flag only */
    @Override
    public boolean update(User u) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, role = ?, active = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getRole().name());
            ps.setBoolean(4, u.isActive());
            ps.setInt(5, u.getId());
            return ps.executeUpdate() == 1;
        }
    }

    /** what a user can change about themselves */
    public boolean updateProfile(User u) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, weight_kg = ?, height_cm = ?, fitness_goal = ? "
                + "WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.getName());
            ps.setString(2, u.getEmail());
            setNullableDouble(ps, 3, u.getWeightKg());
            setNullableDouble(ps, 4, u.getHeightCm());
            ps.setString(5, u.getFitnessGoal());
            ps.setInt(6, u.getId());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean updatePassword(int userId, String newHash) throws SQLException {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public int countByRole(Role role) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, role.name());
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
