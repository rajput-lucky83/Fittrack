package com.fittrack.dao;

import com.fittrack.model.Challenge;
import com.fittrack.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ChallengeDao implements CrudDao<Challenge> {

    private final ActivityDao activityDao = new ActivityDao();

    private Challenge map(ResultSet rs) throws SQLException {
        Challenge c = new Challenge();
        c.setId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setDescription(rs.getString("description"));
        c.setMetric(rs.getString("metric"));
        c.setTargetValue(rs.getInt("target_value"));
        c.setStartDate(rs.getDate("start_date").toLocalDate());
        c.setEndDate(rs.getDate("end_date").toLocalDate());
        c.setCreatedBy(rs.getInt("created_by"));
        return c;
    }

    /** all challenges with participant counts (admin view) */
    @Override
    public List<Challenge> findAll() throws SQLException {
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM challenge_participants p "
                + "WHERE p.challenge_id = c.id) AS cnt FROM challenges c ORDER BY c.end_date DESC, c.id DESC";
        List<Challenge> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Challenge c = map(rs);
                c.setParticipantCount(rs.getInt("cnt"));
                list.add(c);
            }
        }
        return list;
    }

    /** same list but each row also says whether THIS user already joined */
    public List<Challenge> findAllForUser(int userId) throws SQLException {
        String sql = "SELECT c.*, "
                + "(SELECT COUNT(*) FROM challenge_participants p WHERE p.challenge_id = c.id) AS cnt, "
                + "(SELECT COUNT(*) FROM challenge_participants p WHERE p.challenge_id = c.id AND p.user_id = ?) AS mine "
                + "FROM challenges c ORDER BY c.end_date ASC, c.id DESC";
        List<Challenge> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Challenge c = map(rs);
                    c.setParticipantCount(rs.getInt("cnt"));
                    c.setJoinedByCurrentUser(rs.getInt("mine") > 0);
                    list.add(c);
                }
            }
        }
        return list;
    }

    @Override
    public Challenge findById(int id) throws SQLException {
        String sql = "SELECT * FROM challenges WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public boolean save(Challenge c) throws SQLException {
        String sql = "INSERT INTO challenges (title, description, metric, target_value, start_date, end_date, "
                + "created_by) VALUES (?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getTitle());
            ps.setString(2, c.getDescription());
            ps.setString(3, c.getMetric());
            ps.setInt(4, c.getTargetValue());
            ps.setDate(5, Date.valueOf(c.getStartDate()));
            ps.setDate(6, Date.valueOf(c.getEndDate()));
            ps.setInt(7, c.getCreatedBy());
            boolean ok = ps.executeUpdate() == 1;
            if (ok) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        c.setId(keys.getInt(1));
                    }
                }
            }
            return ok;
        }
    }

    @Override
    public boolean update(Challenge c) throws SQLException {
        String sql = "UPDATE challenges SET title = ?, description = ?, metric = ?, target_value = ?, "
                + "start_date = ?, end_date = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, c.getTitle());
            ps.setString(2, c.getDescription());
            ps.setString(3, c.getMetric());
            ps.setInt(4, c.getTargetValue());
            ps.setDate(5, Date.valueOf(c.getStartDate()));
            ps.setDate(6, Date.valueOf(c.getEndDate()));
            ps.setInt(7, c.getId());
            return ps.executeUpdate() == 1;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM challenges WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean hasJoined(int userId, int challengeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM challenge_participants WHERE challenge_id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, challengeId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Joins a challenge. The participant row and the activity log entry are written in one
     * transaction, so we never end up with one without the other.
     */
    public boolean join(int userId, String userName, Challenge challenge) throws SQLException {
        String sql = "INSERT INTO challenge_participants (challenge_id, user_id, joined_on) VALUES (?,?,?)";
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, challenge.getId());
                ps.setInt(2, userId);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                ps.executeUpdate();
                activityDao.log(con, userId, userName, "JOIN_CHALLENGE", "Joined \"" + challenge.getTitle() + "\"");
                con.commit();
                return true;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean leave(int userId, int challengeId) throws SQLException {
        String sql = "DELETE FROM challenge_participants WHERE challenge_id = ? AND user_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, challengeId);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Every challenge this user joined, with their progress. Progress is worked out from the
     * workouts logged between the challenge's start and end dates.
     */
    public List<Challenge> findJoinedByUser(int userId) throws SQLException {
        String sql = "SELECT c.*, cp.joined_on, "
                + "(SELECT CASE c.metric WHEN 'MINUTES' THEN COALESCE(SUM(w.duration_min),0) ELSE COUNT(w.id) END "
                + "   FROM workouts w WHERE w.user_id = cp.user_id "
                + "   AND w.workout_date BETWEEN c.start_date AND c.end_date) AS progress, "
                + "(SELECT COUNT(*) FROM challenge_participants p WHERE p.challenge_id = c.id) AS cnt "
                + "FROM challenge_participants cp JOIN challenges c ON c.id = cp.challenge_id "
                + "WHERE cp.user_id = ? ORDER BY c.end_date DESC";
        List<Challenge> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Challenge c = map(rs);
                    c.setJoinedByCurrentUser(true);
                    c.setProgress(rs.getInt("progress"));
                    c.setParticipantCount(rs.getInt("cnt"));
                    c.setJoinedOn(rs.getDate("joined_on").toLocalDate());
                    list.add(c);
                }
            }
        }
        return list;
    }
}
