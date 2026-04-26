package com.papercraft.dao;

import com.papercraft.utils.DBConnect;
import com.papercraft.model.Review;
import com.papercraft.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    //getReviewsByProductId
    public List<Review> getReviewsByProductId(int productId) {
        List<Review> reviews = new ArrayList<>();

        String sql = "SELECT r.*, u.fullname " +
                "FROM review r " +
                "JOIN users u ON r.user_id = u.id " +
                "WHERE r.product_id = ? " +
                "ORDER BY r.created_at DESC";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Gán tham số cho product_id
            ps.setInt(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();

                    r.setId(rs.getInt("id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setProductId(rs.getInt("product_id"));
                    r.setRating(rs.getInt("rating"));
                    r.setComment(rs.getString("comment"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));

                    reviews.add(r);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return reviews;
    }

    public boolean checkEmailExists(String email) {
        // In ra log để xem server nhận được chuỗi gì
        System.out.println("DEBUG CHECK EMAIL: [" + email + "]");

        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {


            ps.setString(1, email.trim());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void signup(User user) {
        String sql = "INSERT INTO users (fname, lname, email, phone_number, gender, password_hash, role, status) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFname());
            ps.setString(2, user.getLname());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getGender());
            ps.setString(6, user.getPasswordHash());
            ps.setString(7, "user");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User login(String account, String passwordHash) {
        String sql = "SELECT * FROM users WHERE (email = ? OR phone_number = ?) AND password_hash = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, account);
            ps.setString(2, account);
            ps.setString(3, passwordHash);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFname(rs.getString("fname"));
                user.setLname(rs.getString("lname"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setGender(rs.getString("gender"));
                user.setRole(rs.getString("role"));
                user.setPasswordHash(rs.getString("password_hash"));

                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET fname = ?, lname = ?, phone_number = ?, gender = ? WHERE id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, user.getFname());
            ps.setString(2, user.getLname());
            ps.setString(3, user.getPhoneNumber());
            ps.setString(4, user.getGender());
            ps.setInt(5, user.getId());

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changePassword(int id, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, id);

            int rowAffected = ps.executeUpdate();
            return rowAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Integer totalUser() {
        String sql = """
                SELECT COUNT(id) AS total_user FROM users;
                """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total_user");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public User getBasicInfoById(int userID) {
        String sql = """
                SELECT id, fullname, email,phone_number
                FROM users
                WHERE id =?;
                """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setFullname(rs.getString("fullname"));
                    user.setEmail(rs.getString("email"));
                    user.setPhoneNumber(String.valueOf(rs.getInt("phone_number")));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //  Đếm số lượng user
    public int countCustomers(String keyword, String statusFilter) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE role != 'admin' ");
        List<Object> params = new ArrayList<>();

        // Tìm kiếm
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (email LIKE ? OR phone_number LIKE ? OR CONCAT(fname, ' ', lname) LIKE ?) ");
            String search = "%" + keyword.trim() + "%";
            params.add(search);
            params.add(search);
            params.add(search);
        }

        // Lọc trạng thái (active/blocked)
        if ("active".equals(statusFilter)) {
            sql.append("AND status = 1 ");
        } else if ("blocked".equals(statusFilter)) {
            sql.append("AND status = 0 ");
        }

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //  Lấy danh sách user phân trang
    public List<User> getCustomersPagination(String keyword, String statusFilter, int page, int pageSize) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                    SELECT u.*, 
                           (SELECT COALESCE(SUM(o.total_price), 0) 
                            FROM `orders` o 
                            WHERE o.user_id = u.id) AS total_spending
                    FROM users u 
                    WHERE 1=1 
                """);
        List<Object> params = new ArrayList<>();

        //SEARCH
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (email LIKE ? OR phone_number LIKE ? OR CONCAT(fname, ' ', lname) LIKE ?) ");
            String search = "%" + keyword.trim() + "%";
            params.add(search);
            params.add(search);
            params.add(search);
        }
        //LỌC TRẠNG THÁI
        if ("active".equals(statusFilter)) {
            sql.append("AND status = 1 ");
        } else if ("blocked".equals(statusFilter)) {
            sql.append("AND status = 0 ");
        }

        sql.append("ORDER BY id DESC LIMIT ? OFFSET ?");

        // Tính offset
        int offset = (page - 1) * pageSize;
        params.add(pageSize);
        params.add(offset);

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setFname(rs.getString("fname"));
                    u.setLname(rs.getString("lname"));

                    u.setFullname(u.getFname() + " " + u.getLname());

                    u.setEmail(rs.getString("email"));
                    u.setPhoneNumber(rs.getString("phone_number"));
                    u.setGender(rs.getString("gender"));
                    u.setRole(rs.getString("role"));
                    u.setStatus(rs.getBoolean("status")); // 1: true, 0: false
                    u.setTotalSpending(rs.getDouble("total_spending"));


                    try {
                        u.setCreatedAt(rs.getTimestamp("created_at"));
                    } catch (Exception ignore) {
                    }

                    list.add(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //  update trạng thái (Khóa/Mở)
    public boolean updateUserStatus(int userId, boolean newStatus) {
        String sql = "UPDATE users SET status = ? WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStatus ? 1 : 0);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserRole(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newRole);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Hàm cập nhật mật khẩu theo Email
    public boolean updatePasswordByEmail(String email, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE email = ?";
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy thông tin User bằng Email
    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setFname(rs.getString("fname"));
                user.setLname(rs.getString("lname"));
                user.setEmail(rs.getString("email"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setGender(rs.getString("gender"));
                user.setRole(rs.getString("role"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setStatus(rs.getBoolean("status"));
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getCustomersByMonth(int month, int year) {
        List<User> list = new ArrayList<>();

        String sql = """
        SELECT u.id, u.fullname, u.phone_number,u.email, u.status, u.role, 
               (SELECT COALESCE(SUM(o.total_price), 0) 
                FROM orders o 
                WHERE o.user_id = u.id) AS total_spending
        FROM users u
        WHERE MONTH(u.created_at) = ?
        AND YEAR(u.created_at) = ?
        ORDER BY u.created_at DESC
    """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, month);
            ps.setInt(2, year);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setFullname(rs.getString("fullname"));
                u.setEmail(rs.getString("email"));
                u.setPhoneNumber(rs.getString("phone_number"));
                u.setRole(rs.getString("role"));
                u.setStatus(rs.getBoolean("status"));
                u.setTotalSpending(rs.getDouble("total_spending"));
                list.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}