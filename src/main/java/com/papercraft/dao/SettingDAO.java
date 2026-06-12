package com.papercraft.dao;

import com.papercraft.utils.DBConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class SettingDAO {
    private static final Logger logger = LoggerFactory.getLogger(SettingDAO.class);

    public Map<String, String> getAllSettings() {
        Map<String, String> settings = new HashMap<>();
        String sql = "SELECT setting_key, setting_value FROM setting";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                settings.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        }catch (Exception e) {
            logger.error("Failed to get settings", e);
        }
        return settings;
    }

    public boolean updateSetting(String key, String value) {
        String sql = "UPDATE setting SET setting_value = ? WHERE setting_key = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value);
            ps.setString(2, key);

            return ps.executeUpdate() > 0;
        }catch (Exception e) {
            logger.error("Failed to update setting, key={}", key, e);
        }
        return false;
    }
}
