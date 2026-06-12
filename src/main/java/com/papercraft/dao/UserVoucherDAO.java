package com.papercraft.dao;

import com.papercraft.model.Voucher;
import com.papercraft.utils.DBConnect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserVoucherDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserVoucherDAO.class);


    public List<Voucher> getVouchersByUserId(int userId) {

        List<Voucher> vouchers = new ArrayList<>();

        String sql = """
                SELECT v.*
                FROM user_vouchers uv
                JOIN vouchers v
                    ON uv.voucher_id = v.id
                WHERE uv.user_id = ?
                AND uv.is_used = 0
                AND v.is_deleted = 0
                """;

        try (
                Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Voucher voucher = new Voucher();

                voucher.setId(rs.getInt("id"));
                voucher.setCode(rs.getString("code"));
                voucher.setName(rs.getString("name"));
                voucher.setDescription(rs.getString("description"));
                voucher.setDiscountType(rs.getString("discount_type"));
                voucher.setDiscountValue(rs.getBigDecimal("discount_value"));
                voucher.setMaxDiscount(rs.getBigDecimal("max_discount"));
                voucher.setMinOrderValue(rs.getBigDecimal("min_order_value"));
                voucher.setQuantity(rs.getInt("quantity"));
                voucher.setStartDate(rs.getTimestamp("start_date"));
                voucher.setEndDate(rs.getTimestamp("end_date"));
                voucher.setStatus(rs.getString("status"));
                voucher.setCreatedAt(rs.getTimestamp("created_at"));
                voucher.setIsDeleted(rs.getBoolean("is_deleted"));

                vouchers.add(voucher);
            }

        }catch (SQLException e) {
            logger.error("SQL error while getting vouchers for userId={}", userId, e);
        } catch (Exception e) {
            logger.error("Unexpected error while getting vouchers for userId={}", userId, e);
        }

        return vouchers;
    }

    public boolean addUserVoucher(int userId, int voucherId){
        String sql = """
            INSERT INTO user_vouchers (user_id,voucher_id)
            VALUES (?, ?);
        """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, voucherId);

            return ps.executeUpdate()>0;

        }catch (SQLException e) {
            logger.error("SQL error while assigning voucherId={} to userId={}", voucherId, userId, e);
        } catch (Exception e) {
            logger.error("Unexpected error while assigning voucherId={} to userId={}", voucherId, userId, e);
        }
        return false;
    }

    public boolean setUsedVoucher(int userId, int voucherId){
        String sql = """
            UPDATE user_vouchers
            SET is_used = 1,used_at = CURRENT_TIMESTAMP
            WHERE user_id = ? AND voucher_id = ? AND is_used = 0;
         """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, voucherId);

            return ps.executeUpdate()>0;

        }catch (SQLException e) {
            logger.error("SQL error while marking voucher as used. userId={}, voucherId={}", userId, voucherId, e);
        } catch (Exception e) {
            logger.error("Unexpected error while marking voucher as used. userId={}, voucherId={}", userId, voucherId, e);
        }
        return false;
    }


}
