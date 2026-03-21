package com.papercraft.dao;

import com.papercraft.utils.DBConnect;
import com.papercraft.model.Payment;

import java.math.BigDecimal;
import java.sql.*;

public class PaymentDAO {
    public double getTotalRevenue() {
        String sql = """
                SELECT SUM(payment_amount) as revenue from payment;
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
                SELECT id,payment_method,payment_amount,status,paid_at
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
                    String payment_method = rs.getString("payment_method");
                    BigDecimal payment_amount = rs.getBigDecimal("payment_amount");
                    Boolean status = rs.getBoolean("status");
                    Timestamp paid_at = rs.getTimestamp("paid_at");

                    Payment payment = new Payment();
                    payment.setId(id);
                    payment.setPaymentMethod(payment_method);
                    payment.setStatus(status);
                    payment.setPaymentAmount(payment_amount);
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
}
