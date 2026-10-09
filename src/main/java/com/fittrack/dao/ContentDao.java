package com.fittrack.dao;

import com.fittrack.model.FitnessContent;
import com.fittrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ContentDao implements CrudDao<FitnessContent> {

    private static final String SELECT =
            "SELECT f.id, f.title, f.category, f.body, f.submitted_by, f.status, f.admin_note, f.created_at, "
                    + "u.name AS submitter FROM fitness_content f JOIN users u ON u.id = f.submitted_by ";

    private FitnessContent map(ResultSet rs) throws SQLException {
        FitnessContent f = new FitnessContent();
        f.setId(rs.getInt("id"));
        f.setTitle(rs.getString("title"));
        f.setCategory(rs.getString("category"));
        f.setBody(rs.getString("body"));
        f.setSubmittedBy(rs.getInt("submitted_by"));
        f.setSubmitterName(rs.getString("submitter"));
        f.setStatus(rs.getString("status"));
        f.setAdminNote(rs.getString("admin_note"));
        f.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return f;
    }

    private List<FitnessContent> query(String sql, Object... params) throws SQLException {
        List<FitnessContent> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<FitnessContent> findAll() throws SQLException {
        // pending ones first so the admin sees what needs a decision
        return query(SELECT + "ORDER BY FIELD(f.status,'PENDING','APPROVED','REJECTED'), f.created_at DESC");
    }

    public List<FitnessContent> findApproved() throws SQLException {
        return query(SELECT + "WHERE f.status = 'APPROVED' ORDER BY f.created_at DESC");
    }

    public List<FitnessContent> findBySubmitter(int userId) throws SQLException {
        return query(SELECT + "WHERE f.submitted_by = ? ORDER BY f.created_at DESC", userId);
    }

    @Override
    public FitnessContent findById(int id) throws SQLException {
        List<FitnessContent> list = query(SELECT + "WHERE f.id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean save(FitnessContent f) throws SQLException {
        String sql = "INSERT INTO fitness_content (title, category, body, submitted_by, status, created_at) "
                + "VALUES (?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getTitle());
            ps.setString(2, f.getCategory());
            ps.setString(3, f.getBody());
            ps.setInt(4, f.getSubmittedBy());
            ps.setString(5, f.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            boolean ok = ps.executeUpdate() == 1;
            if (ok) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        f.setId(keys.getInt(1));
                    }
                }
            }
            return ok;
        }
    }

    /** status + note change, used when an admin reviews a submission */
    @Override
    public boolean update(FitnessContent f) throws SQLException {
        String sql = "UPDATE fitness_content SET status = ?, admin_note = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, f.getStatus());
            ps.setString(2, f.getAdminNote());
            ps.setInt(3, f.getId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM fitness_content WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public int countByStatus(String status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM fitness_content WHERE status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
