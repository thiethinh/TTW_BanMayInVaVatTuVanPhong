package com.papercraft.dao;

import com.papercraft.utils.DBConnect;
import com.papercraft.model.Payment;

import java.math.BigDecimal;
import java.sql.*;

public class PaymentDAO {
    public double getTotalRevenue() {
        String sql = """
                SELECT COALESCE(SUM(payment_amount), 0) AS revenue
                FROM payment
                WHERE status = 1;
                """;
        try (
                Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    return rs.getDouble("revenue");
                }
            }

        } catch (SQLException e) {

            System.err.println("SQL Error : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return 0.0;
    }

    public Payment getPaymentByOrderId(int orderId) {
        String sql = """
                SELECT id, order_id, payment_method, payment_amount, status, transaction_code, paid_at
                FROM payment
                WHERE order_id =?;
                """;
        try (
                Connection conn = DBConnect.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Integer id = rs.getInt("id");
                    Integer order_id = rs.getInt("order_id");
                    String payment_method = rs.getString("payment_method");
                    BigDecimal payment_amount = rs.getBigDecimal("payment_amount");
                    Boolean status = rs.getBoolean("status");
                    String transaction_code = rs.getString("transaction_code");
                    Timestamp paid_at = rs.getTimestamp("paid_at");

                    Payment payment = new Payment();
                    payment.setId(id);
                    payment.setOrderId(order_id);
                    payment.setPaymentMethod(payment_method);
                    payment.setStatus(status);
                    payment.setPaymentAmount(payment_amount);
                    payment.setTransactionCode(transaction_code);
                    payment.setPaidAt(paid_at);

                    return payment;
                }
            }

        } catch (SQLException e) {

            System.err.println("SQL Error : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //insertPayment
    public boolean insertPayment(Connection conn, Payment payment) throws SQLException {
        String sql = """
                INSERT INTO payment (order_id, payment_method, payment_amount, status, transaction_code, paid_at)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                PreparedStatement ps = conn.prepareStatement(sql);
        ) {
            ps.setInt(1, payment.getOrderId());
            ps.setString(2, payment.getPaymentMethod());
            ps.setBigDecimal(3, payment.getPaymentAmount());
            ps.setBoolean(4, payment.getStatus() != null ? payment.getStatus() : false);

            if (payment.getTransactionCode() != null && !payment.getTransactionCode().isBlank()) {
                ps.setString(5, payment.getTransactionCode());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            if (payment.getPaidAt() != null) {
                ps.setTimestamp(6, payment.getPaidAt());
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            return ps.executeUpdate() > 0;
        }
    }

    // verifyPaymentSuccess
    public boolean verifyPaymentSuccess(int orderId, String transactionCode) {
        String sql = """
                UPDATE payment 
                Set status = 1,
                    paid_at= CURRENT_TIMESTAMP,
                    transaction_code=?
                Where order_id = ? and status=0
                """;

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (transactionCode != null && !transactionCode.isBlank()) {
                ps.setString(1, transactionCode.trim());
            } else {
                ps.setNull(1, Types.VARCHAR);
            }

            ps.setInt(2, orderId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}
