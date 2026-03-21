package com.papercraft.dao;

import com.papercraft.utils.DBConnect;
import com.papercraft.model.OrderItem;
import com.papercraft.model.Product;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {
    private static final String ROOT_PATH = "images/upload/";

    public List<OrderItem> getItemByOrderId(int orderId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = """
                SELECT oi.id ,i.img_name,oi.product_id, p.product_name,oi.quantity,oi.price
                FROM order_item oi
                JOIN product p ON p.id =oi.product_id
                JOIN image i ON i.entity_id = p.id
                WHERE oi.order_id =? and i.is_thumbnail =1;
                """;
        try (Connection conn = DBConnect.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setThumbnail(ROOT_PATH + rs.getString("img_name"));
                    Integer id = rs.getInt("id");
                    Integer quantity = rs.getInt("quantity");
                    BigDecimal price = rs.getBigDecimal("price");

                    OrderItem oi = new OrderItem();
                    oi.setOrderId(id);
                    oi.setPrice(price);
                    oi.setQuantity(quantity);
                    oi.setProduct(product);

                    orderItems.add(oi);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderItems;
    }

    public void insertOrderItem(Connection conn, List<OrderItem> oi) {
        String sql = "INSERT INTO order_item (order_id, product_id, quantity, price) VALUES (?,?,?,?)";

        try (PreparedStatement ps = conn.prepareStatement(sql);) {
            for (OrderItem item : oi) {
                ps.setInt(1, item.getOrderId());
                ps.setInt(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                ps.setBigDecimal(4, item.getPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
