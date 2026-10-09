package com.fittrack.dao;

import com.fittrack.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/** Key/value store for the system-wide settings the admin can change. */
public class SettingsDao {

    public Map<String, String> getAll() throws SQLException {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT setting_key, setting_value FROM settings ORDER BY setting_key";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString(1), rs.getString(2));
            }
        }
        return map;
    }

    public String get(String key, String fallback) throws SQLException {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : fallback;
            }
        }
    }

    public int getInt(String key, int fallback) throws SQLException {
        try {
            return Integer.parseInt(get(key, String.valueOf(fallback)).trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    public boolean getBoolean(String key, boolean fallback) throws SQLException {
        return Boolean.parseBoolean(get(key, String.valueOf(fallback)));
    }

    public void set(String key, String value) throws SQLException {
        String sql = "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, key);
            ps.setString(2, value);
            ps.executeUpdate();
        }
    }
}
