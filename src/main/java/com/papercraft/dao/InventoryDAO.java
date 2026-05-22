package com.papercraft.dao;

import com.papercraft.model.InventoryTransaction;
import com.papercraft.model.InventoryTransactionDetail;
import com.papercraft.utils.DBConnect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
    public boolean insertTransaction(InventoryTransaction transaction) {
        Connection conn = null;
        PreparedStatement psTrans = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;

        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            String sqlTrans = "INSERT INTO inventory_transactions (transaction_type, user_id, note, total_value) VALUES (?, ?, ?, ?)";
            psTrans = conn.prepareStatement(sqlTrans, Statement.RETURN_GENERATED_KEYS);
            psTrans.setString(1, transaction.getTransactionType());
            psTrans.setInt(2, transaction.getUserId());
            psTrans.setString(3, transaction.getNote());
            psTrans.setDouble(4, transaction.getTotalValue());
            psTrans.executeUpdate();

            rs = psTrans.getGeneratedKeys();
            int transId = 0;
            if (rs.next()) {
                transId = rs.getInt(1);
            }

            String sqlDetail = "INSERT INTO inventory_transaction_details (transaction_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            String sqlUpdateStock = transaction.getTransactionType().equals("IMPORT")
                    ? "UPDATE product SET stock_quantity = stock_quantity + ? WHERE id = ? AND is_deleted = 0"
                    : "UPDATE product SET stock_quantity = stock_quantity - ? WHERE id = ? AND is_deleted = 0";

            psDetail = conn.prepareStatement(sqlDetail);
            psUpdateStock = conn.prepareStatement(sqlUpdateStock);

            for (InventoryTransactionDetail detail : transaction.getDetails()) {
                psDetail.setInt(1, transId);
                psDetail.setInt(2, detail.getProductId());
                psDetail.setInt(3, detail.getQuantity());
                psDetail.setDouble(4, detail.getPrice());
                psDetail.addBatch();

                psUpdateStock.setInt(1, detail.getQuantity());
                psUpdateStock.setInt(2, detail.getProductId());
                psUpdateStock.addBatch();
            }
            psDetail.executeBatch();
            psUpdateStock.executeBatch();

            conn.commit();
            return true;
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {

        }
        return false;
    }

    public List<InventoryTransaction> getAllTransactions(String type) {
        List<InventoryTransaction> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT i.*, u.fullname AS admin_name
                FROM inventory_transactions i
                JOIN users u ON i.user_id = u.id
                """);

        if (type != null && !type.trim().isEmpty() && !type.equals("all")) {
            sql.append(" WHERE i.transaction_type = ? ");
        }
        sql.append(" ORDER BY i.created_at DESC");

        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString());) {
            if (type != null && !type.trim().isEmpty() && !type.equals("all")) {
                ps.setString(1, type);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                InventoryTransaction transaction = new InventoryTransaction();
                transaction.setId(rs.getInt("id"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setUserId(rs.getInt("user_id"));
                transaction.setCreatedAt(rs.getTimestamp("created_at"));
                transaction.setNote(rs.getString("note"));
                transaction.setTotalValue(rs.getDouble("total_value"));
                transaction.setAdminName(rs.getString("admin_name"));
                result.add(transaction);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
