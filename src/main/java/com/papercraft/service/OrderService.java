package com.papercraft.service;

import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.OrderItemDAO;
import com.papercraft.dao.PaymentDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.*;
import com.papercraft.utils.DBConnect;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ProductDAO productDAO = new ProductDAO();


    // để lấy được orderId
    public int placeOrderAndReturnId(User user, Cart cart, Order order, String paymentMethod) {
        if (user == null || cart == null || cart.list().isEmpty() || order == null) {
            return 0;
        }

        if (paymentMethod == null || paymentMethod.isBlank()) {
            paymentMethod = "COD";
        }

        Connection conn = null;

        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            double subTotal = 0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (Product product : cart.list()) {
                if (product == null || product.getId() <= 0 || product.getQuantity() <= 0) {
                    conn.rollback();
                    return 0;
                }

                BigDecimal price = BigDecimal.valueOf(product.getPrice());
                BigDecimal total = price.multiply(BigDecimal.valueOf(product.getQuantity()));

                OrderItem item = new OrderItem();
                item.setProductId(product.getId());
                item.setQuantity(product.getQuantity());
                item.setPrice(price);
                item.setTotal(total);
                item.setProduct(product);

                orderItems.add(item);
                subTotal += total.doubleValue();
            }

            subTotal = Math.round(subTotal);

            BigDecimal shippingFeeBD = order.getShippingFee();

            if (shippingFeeBD == null || shippingFeeBD.compareTo(BigDecimal.ZERO) < 0) {
                shippingFeeBD = BigDecimal.ZERO;
            }

            double shippingFee = shippingFeeBD.doubleValue();
            double vat = Math.round(subTotal * 0.05);
            double grandTotal = Math.round(subTotal + shippingFee + vat);

            order.setUserId(user.getId());
            order.setStatus("pending");
            order.setShippingFee(BigDecimal.valueOf(shippingFee));
            order.setTotalPrice(BigDecimal.valueOf(grandTotal));

            if (order.getShippingProvider() == null || order.getShippingProvider().isBlank()) {
                order.setShippingProvider("GHN");
            }

            int orderId = orderDAO.insertOrder(conn, order);

            if (orderId <= 0) {
                conn.rollback();
                return 0;
            }

            for (OrderItem item : orderItems) {
                item.setOrderId(orderId);
            }

            orderItemDAO.insertOrderItem(conn, orderItems);

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentAmount(BigDecimal.valueOf(grandTotal));
            payment.setStatus(false);
            payment.setTransactionCode(null);
            payment.setPaidAt(null);

            boolean paymentInserted = paymentDAO.insertPayment(conn, payment);

            if (!paymentInserted) {
                conn.rollback();
                return 0;
            }

            // Trừ tồn kho sau khi tạo order và payment thành công
            for (OrderItem item : orderItems) {
                boolean stockUpdated = productDAO.decreaseStockIfEnough(
                        conn,
                        item.getProductId(),
                        item.getQuantity()
                );

                if (!stockUpdated) {
                    conn.rollback();
                    return 0;
                }
            }

            conn.commit();
            return orderId;

        } catch (Exception e) {
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            return 0;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}