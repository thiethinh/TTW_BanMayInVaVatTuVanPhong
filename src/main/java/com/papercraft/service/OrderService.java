package com.papercraft.service;

import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.OrderItemDAO;
import com.papercraft.dao.PaymentDAO;
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

    public boolean placeOrder(User user, Cart cart, Order order, String paymentMethod) {
        if (user == null || cart == null || cart.list() == null || cart.list().isEmpty() || order == null) {
            return false;
        }
        if(paymentMethod ==null || paymentMethod.isBlank()){
            paymentMethod = "COD";
        }

        Connection conn = null;

        try {
            conn = DBConnect.getConnection();
            conn.setAutoCommit(false);

            double subTotal = 0;
            List<OrderItem> orderItems = new ArrayList<>();

            for (Product product : cart.list()) {
                if (product == null || product.getId() <= 0 || product.getQuantity() == null || product.getQuantity() <= 0) {
                    conn.rollback();
                    return false;
                }

                int quantity = product.getQuantity();
                BigDecimal price = BigDecimal.valueOf(product.getPrice());
                BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(quantity));

                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(product.getId());
                orderItem.setQuantity(quantity);
                orderItem.setPrice(price);
                orderItem.setProduct(product);
                orderItem.setTotal(lineTotal);

                orderItems.add(orderItem);
                subTotal += lineTotal.doubleValue();
            }

            subTotal = Math.round(subTotal);
            double shippingFee = (subTotal > 5000000 || subTotal == 0) ? 0 : 30000;
            double vat = Math.round(subTotal * 0.05);
            double grandTotal = Math.round(subTotal + vat + shippingFee);

            order.setUserId(user.getId());
            order.setStatus("pending");
            order.setShippingFee(BigDecimal.valueOf(shippingFee));
            order.setTotalPrice(BigDecimal.valueOf(grandTotal));

            int orderId = orderDAO.insertOrder(conn, order);
            if (orderId <= 0) {
                conn.rollback();
                return false;
            }

//            for (OrderItem item : orderItems) {
//                item.setOrderId(orderId);
//            }
//
//            orderItemDAO.insertOrderItem(conn, orderItems);
//
//            conn.commit();
//            return true;

            for (OrderItem item: orderItems){
                item.setOrderId(orderId);
            }
            orderItemDAO.insertOrderItem(conn,orderItems);

            Payment payment= new Payment();
            payment.setOrderId(orderId);
            payment.setPaymentMethod(paymentMethod);
            payment.setPaymentAmount(BigDecimal.valueOf(grandTotal));
            payment.setStatus(false);
            payment.setTransactionCode(null);
            payment.setPaidAt(null);

            boolean paymentInserted= paymentDAO.insertPayment(conn,payment);
            if (!paymentInserted){
                conn.rollback();
                return false;
            }
            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();

            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

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