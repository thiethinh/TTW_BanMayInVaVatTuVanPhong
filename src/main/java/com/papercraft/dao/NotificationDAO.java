package com.papercraft.dao;

import com.papercraft.model.Notification;
import com.papercraft.model.enums.NotificationType;
import com.papercraft.utils.DBConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDAO.class);
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
        }catch (Exception e) {
            logger.error("Failed to load notifications, userId={}", userId, e);
        }
        return list;
    }


    public int countUnseenNotification(int userId) {
        int count = 0;
        String sql = """
                SELECT COUNT(*)
                FROM notifications
                WHERE user_id = ?
                  AND is_seen = 0
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

    public void markRead(int notificationId) {
        String sql = """
                UPDATE notifications
                SET is_read = 1,
                    is_seen = 1
                WHERE id = ?
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notificationId);
            ps.executeUpdate();
        }catch (Exception e) {
            logger.error("Failed to count unseen notifications, notificationId={}", notificationId, e);
        }
    }

    public void markAllSeen(int userId) {
        String sql = """
                UPDATE notifications
                SET is_seen = 1
                WHERE user_id = ?
                AND is_seen = 0
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }catch (Exception e) {
            logger.error("Failed to mark notification as read, userId={}", userId, e);
        }
    }

    public boolean insertNotification(Notification notification) {
        if (notification == null) {
            return false;
        }
        if (notification.getType() == null) {
            return false;
        }

        String sql = """
                INSERT INTO notifications (user_id,content,type,reference_id,is_seen,is_read,created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, notification.getUserId());

            String content = notification.getContent();
            if (content == null || content.isBlank()) {
                content = buildNotificationContent(notification.getType());
            }
            ps.setString(2, content);
            ps.setString(3, notification.getType().name());

            if (notification.getReferenceId() != null) {
                ps.setInt(4, notification.getReferenceId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setBoolean(5, false);
            ps.setBoolean(6, false);
            ps.setTimestamp(7, new Timestamp(System.currentTimeMillis()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Lỗi insertNotification:");
            e.printStackTrace();
            logger.error("Failed to insert notification, userId={}, type={}, referenceId={}",
                    notification.getUserId(), notification.getType(), notification.getReferenceId(), e);
            return false;
        } catch (Exception e) {
            System.out.println("Lỗi hệ thống khi insertNotification:");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private String buildNotificationContent(NotificationType type) {
        if (type == NotificationType.ORDER_PENDING) {
            return "Đơn hàng của bạn đang chờ xử lý.";
        }
        if (type == NotificationType.ORDER_SHIPPED) {
            return "Đơn hàng của bạn đang được giao.";
        }
        if (type == NotificationType.ORDER_COMPLETED) {
            return "Đơn hàng của bạn đã giao thành công.";
        }
        if (type == NotificationType.ORDER_CANCELLED) {
            return "Đơn hàng của bạn đã bị hủy.";
        }
        return "Bạn có thông báo mới từ PaperCraft.";
    }
}
