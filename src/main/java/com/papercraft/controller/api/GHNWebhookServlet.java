package com.papercraft.controller.api;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.papercraft.dao.NotificationDAO;
import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.PaymentDAO;
import com.papercraft.model.Notification;
import com.papercraft.model.Order;
import com.papercraft.model.enums.NotificationType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "GHNWebhookServlet", value = "/api/ghn-webhook")
public class GHNWebhookServlet extends HttpServlet {
    private final OrderDAO orderDAO = new OrderDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        try {
            String body = readRequestBody(request);
            System.out.println("======== GHN WEBHOOK RECEIVED =====");
            System.out.println(body);

            if (body == null || body.isBlank()) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\":false,\"message\":\"Empty body\"}");
                return;
            }

            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String orderCode = firstNotBlank( getStringValue(json, "orderCode"),  getStringValue(json, "order_code"),  getStringValue(json, "OrderCode"));
            String clientOrderCode = firstNotBlank(getStringValue(json, "ClientOrderCode"), getStringValue(json, "client_order_code"), getStringValue(json, "clientOrderCode"));
            String ghnStatus = firstNotBlank( getStringValue(json, "Status"),  getStringValue(json, "status"));

            if (orderCode == null || orderCode.isBlank()) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\":false,\"message\":\"Missing OrderCode\"}");
                return;
            }

            if (ghnStatus == null || ghnStatus.isBlank()) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\":false,\"message\":\"Missing Status\"}");
                return;
            }

            String internalStatus = mapGHNStatusToInternalStatus(ghnStatus);

            if (internalStatus == null) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\":true,\"message\":\"Status ignored\"}");
                return;
            }
            Order order = orderDAO.getOrderByGHNOrderCode(orderCode);
            if (order == null) {
                System.out.println("Không tìm thấy đơn với GHN OrderCode" + orderCode);
                System.out.println("ClientOrderCode: " + clientOrderCode);
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"success\":false,\"message\":\"Order not found\"}");
                return;
            }
            boolean updated = orderDAO.updateStatusByGHNOrderCode(orderCode, internalStatus, ghnStatus);
            //webhook giao hàng thành công thì => thanh toán thành công
            if (updated && "completed".equalsIgnoreCase(internalStatus)) {
                boolean paymentUpdated = paymentDAO.markPaymentAsPaidByOrderId(order.getId());

                if (!paymentUpdated) {
                    System.out.println("Đơn đã completed nhưng cập nhật thanh toán thất bại. OrderId = " + order.getId());
                }
            }
            //Tb đơn hàng thành công
            if (updated) {
                NotificationType type = getNotificationTypeByOrderStatus(internalStatus);

                if (type != null) {
                    Notification notification = new Notification(order.getUserId(),type,order.getId());

                    boolean insertedNotification = notificationDAO.insertNotification(notification);
                    if (!insertedNotification) {
                        System.out.println("Cập nhật đơn thành công nhưng tạo thông báo thất bại. OrderId = " + order.getId());
                    }
                }
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(
                    "{\"success\":true,\"message\":\"Webhook processed\",\"updated\":" + updated + "}"
            );
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"success\":false,\"message\":\"Webhook processing failed\"}");
        }


    }

    private String mapGHNStatusToInternalStatus(String ghnStatus) {
        if (ghnStatus == null) {
            return null;
        }

        String status = ghnStatus.trim().toLowerCase();

        return switch (status) {
            case "ready_to_pick", "picking", "picked", "storing", "transporting", "sorting", "delivering" -> "shipped";
            case "delivered" -> "completed";
            case "cancel", "cancelled", "canceled", "delivery_fail", "return" -> "canceled";
            default -> null;
        };
    }

    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = request.getReader();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String firstNotBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String getStringValue(JsonObject json, String key) {
        if (json == null || key == null || !json.has(key) || json.get(key).isJsonNull()) {
            return null;
        }
        return json.get(key).getAsString();

    }
    private NotificationType getNotificationTypeByOrderStatus(String status) {
        if (status == null) {
            return null;
        }

        if ("shipped".equalsIgnoreCase(status)) {
            return NotificationType.ORDER_SHIPPED;
        }

        if ("completed".equalsIgnoreCase(status)) {
            return NotificationType.ORDER_COMPLETED;
        }

        if ("canceled".equalsIgnoreCase(status)) {
            return NotificationType.ORDER_CANCELLED;
        }

        return null;
    }
}
