package com.papercraft.service;

import com.papercraft.dao.NotificationDAO;
import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.OrderItemDAO;
import com.papercraft.dao.PaymentDAO;
import com.papercraft.model.Notification;
import com.papercraft.model.Order;
import com.papercraft.model.OrderItem;
import com.papercraft.model.Payment;
import com.papercraft.model.enums.NotificationType;

import java.util.List;

public class OrderShippingService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final GHNCreateOrderService ghnCreateOrderService = new GHNCreateOrderService();

    public boolean shipOrderWithGHN(int orderId) {
        try {
            Order order = orderDAO.getOrderByID(orderId);

            if (order == null) {
                return false;
            }

            if (!"pending".equalsIgnoreCase(order.getStatus())) {
                return false;
            }

            List<OrderItem> orderItems = orderItemDAO.getItemByOrderId(orderId);
            Payment payment = paymentDAO.getPaymentByOrderId(orderId);
            if (orderItems == null || orderItems.isEmpty()) {
                return false;
            }

            //nếu chưa có mã GHN => tạo đơn GHN
            if (order.getGhnOrderCode() == null || order.getGhnOrderCode().isBlank()) {
                String ghnOrderCode = ghnCreateOrderService.createGHNOrder(order, orderItems, payment);
                boolean savedGHN = orderDAO.updateGHNInfo(orderId, ghnOrderCode, "created");
                if (!savedGHN) {
                    return false;
                }
            }

            boolean updatedStatus = orderDAO.updateOrderStatus(orderId, "shipped");
            if (updatedStatus) {
                Notification notification = new Notification(order.getUserId(), NotificationType.ORDER_SHIPPED, orderId);
                notificationDAO.insertNotification(notification);
            }
            return updatedStatus;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}