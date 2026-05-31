package com.papercraft.dao;

import com.papercraft.model.Notification;
import com.papercraft.model.enums.NotificationType;
import com.papercraft.utils.DBConnect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    public List<Notification> getAllNotificationByUserId(int userId) {
        List<Notification> list = new ArrayList<>();
        String sql = """
                SELECT id, user_id, content, type, reference_id, is_seen, is_read, created_at
                FROM notifications
                WHERE user_id = ?
                ORDER BY created_at DESC
            """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification noti = new Notification();
                    noti.setId(rs.getInt("id"));
                    noti.setUserId(rs.getInt("user_id"));
                    noti.setContent(rs.getString("content"));

                    String typeStr = rs.getString("type");
                    if (typeStr != null) {
                        noti.setType(NotificationType.valueOf(typeStr));
                    }

                    noti.setReferenceId(rs.getInt("reference_id"));
                    noti.setSeen(rs.getBoolean("is_seen"));
                    noti.setRead(rs.getBoolean("is_read"));
                    noti.setCreatedAt(rs.getTimestamp("created_at"));

                    list.add(noti);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countUnreadNotification(int userId) {
        int count = 0;
        String sql = """
                SELECT COUNT(*) 
                FROM notifications 
                WHERE user_id = ? AND is_read = 0
            """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
