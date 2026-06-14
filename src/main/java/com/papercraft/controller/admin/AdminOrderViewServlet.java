package com.papercraft.controller.admin;

import com.papercraft.dao.*;
import com.papercraft.model.*;
import com.papercraft.model.enums.NotificationType;
import com.papercraft.service.OrderShippingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminOrderViewServlet", value = "/admin/admin-order-view")
public class AdminOrderViewServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderViewServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        String orderIdRaw = request.getParameter("orderId");
        String accept = request.getParameter("accept");
        String cancel = request.getParameter("cancel");
        String verifyPayment = request.getParameter("verifyPayment");
        String transactionCode = request.getParameter("transactionCode");

        logger.debug("AdminOrderViewServlet GET. orderId='{}', accept='{}', cancel='{}', verifyPayment='{}', transactionCode='{}'", orderIdRaw, accept, cancel, verifyPayment, transactionCode);
        Integer orderId = parseIntegerOrNull(orderIdRaw);

        if (orderId == null || orderId <= 0) {
            logger.warn("Invalid orderId received: {}", orderIdRaw);
            session.setAttribute("errorMsg", "Mã đơn hàng không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/admin-order-manage");
            return;
        }
        OrderDAO orderDAO = new OrderDAO();

        boolean updated = false;
        boolean isAccept = false;
        boolean isCancel = false;
        boolean isVerifyPayment = false;
        boolean verifiedPayment = false;

        if (accept != null || cancel != null) {
            Order currentOrder = orderDAO.getOrderByID(orderId);
            if (currentOrder == null) {
                logger.warn("Cannot update status because order ID {} was not found.", orderId);
                session.setAttribute("errorMsg", "Không tìm thấy đơn hàng #" + orderId);
                response.sendRedirect(request.getContextPath() + "/admin/admin-order-manage");
                return;
            }

            // Tránh THop request gửi cả accept và cancel cùng lúc
            if (accept != null && cancel != null) {
                logger.warn("Invalid request: both accept and cancel are present for order ID {}", orderId);
                request.setAttribute("errorMsg", "Yêu cầu cập nhật trạng thái không hợp lệ.");
            }

            //admin duyệt đơn: pending => shipped
            else if (accept != null) {
                if (!isValidStatusChange(currentOrder.getStatus(), accept)) {
                    logger.warn("Rejected approve action. Order ID {} cannot change from [{}] to [{}].", orderId, currentOrder.getStatus(), accept);
                    request.setAttribute("errorMsg", "Không thể duyệt đơn ở trạng thái hiện tại.");
                } else if ("shipped".equalsIgnoreCase(accept)) {

                    //duyệt đơn GHN: Tạo vận đơn GHN=> lưu ghn_order_code => chuyển orders.status = shipped.
                    OrderShippingService shippingService = new OrderShippingService();
                    updated = shippingService.shipOrderWithGHN(orderId);
                    isAccept = updated;

                    if (updated) {
                        logger.info("Approved order ID {} and created GHN shipping order successfully.", orderId);
                        request.setAttribute("successMsg", "Đã duyệt đơn và tạo vận đơn GHN thành công.");
                    } else {
                        logger.warn("Failed to approve order ID {} or create GHN shipping order.", orderId);
                        request.setAttribute("errorMsg", "Duyệt đơn thất bại hoặc không tạo được vận đơn GHN.");
                    }
                }
            }

            //chỉ cho admin hủy khi pending
            else if (cancel != null) {
                if (!isValidStatusChange(currentOrder.getStatus(), cancel)) {
                    logger.warn("Rejected cancel action. Order ID {} cannot change from [{}] to [{}].", orderId, currentOrder.getStatus(), cancel);
                    request.setAttribute("errorMsg", "Chỉ có thể hủy đơn khi đơn còn đang chờ xử lý.");
                } else if ("canceled".equalsIgnoreCase(cancel)) {
                    updated = orderDAO.updateOrderStatus(orderId, "canceled");
                    isCancel = updated;

                    if (updated) {
                        NotificationDAO notificationDAO = new NotificationDAO();
                        Notification notification = new Notification(currentOrder.getUserId(), NotificationType.ORDER_CANCELLED, orderId);
                        boolean insertedNotification = notificationDAO.insertNotification(notification);

                        if (!insertedNotification) {
                            logger.warn("Order ID {} was canceled but notification insert failed.", orderId);
                        }

                        logger.info("Canceled order ID {} successfully.", orderId);
                        request.setAttribute("successMsg", "Đã hủy đơn hàng thành công.");
                    } else {
                        logger.warn("Failed to cancel order ID {}.", orderId);
                        request.setAttribute("errorMsg", "Hủy đơn thất bại.");
                    }
                }
            }
        }

// xử lý xác nhận thanh toán
        if (verifyPayment != null) {
            PaymentDAO paymentDAO = new PaymentDAO();
            Payment currentPayment = paymentDAO.getPaymentByOrderId(orderId);
            Order currentOrder = orderDAO.getOrderByID(orderId);

            if (currentPayment == null) {
                logger.warn("Cannot verify payment because payment not found for order ID {}.", orderId);
                request.setAttribute("errorMsg", "Không tìm thấy thông tin thanh toán của đơn hàng.");
            } else if (Boolean.TRUE.equals(currentPayment.getStatus())) {
                logger.info("Payment for order ID {} is already verified.", orderId);
                request.setAttribute("successMsg", "Đơn hàng này đã được xác nhận thanh toán trước đó.");
            } else {
                String method = currentPayment.getPaymentMethod();
                logger.info("Starting payment verification for order ID {}. Method='{}', transactionCode='{}'", orderId, method, transactionCode);
                if ("COD".equalsIgnoreCase(method)) {
                    logger.warn("Manual COD payment verification rejected for order ID {}. COD payment will be updated by GHN webhook when delivered. Current order status: {}", orderId, currentOrder != null ? currentOrder.getStatus() : "NULL");

                    isVerifyPayment = false;
                    verifiedPayment = false;
                    request.setAttribute("errorMsg", "Không thể xác nhận thanh toán COD thủ công. Thanh toán COD sẽ tự cập nhật khi GHN giao hàng thành công.");
                } else {
                    verifiedPayment = paymentDAO.verifyPaymentSuccess(orderId, transactionCode);
                    isVerifyPayment = true;

                    if (verifiedPayment) {
                        logger.info("Payment verified successfully for order ID {}.", orderId);
                        request.setAttribute("successMsg", "Xác nhận thanh toán thành công.");
                    } else {
                        logger.warn("Payment verification failed for order ID {}.", orderId);
                        request.setAttribute("errorMsg", "Xác nhận thanh toán thất bại.");
                    }
                }
            }
        }

        Order order = orderDAO.getOrderByID(orderId);

        if (order == null) {
            logger.error("Order not found when loading detail page. Order ID = {}", orderId);
            session.setAttribute("errorMsg", "Không tìm thấy hoặc không tải được đơn hàng #" + orderId);
            response.sendRedirect(request.getContextPath() + "/admin/admin-order-manage");
            return;
        }

        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> orderItems = orderItemDAO.getItemByOrderId(orderId);
        order.setOrderItems(orderItems);

        User user = new UserDAO().getBasicInfoById(order.getUserId());
        Payment payment = new PaymentDAO().getPaymentByOrderId(orderId);

        logger.debug("Loaded order detail successfully. Order ID={}, itemCount={}, customer='{}'", orderId, orderItems != null ? orderItems.size() : 0, user != null ? user.getEmail() : "N/A");

        request.setAttribute("order", order);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("user", user);
        request.setAttribute("payment", payment);

        request.setAttribute("updated", updated);
        request.setAttribute("isAccept", isAccept);
        request.setAttribute("isCancel", isCancel);
        request.setAttribute("isVerifyPayment", isVerifyPayment);
        request.setAttribute("verifiedPayment", verifiedPayment);

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-order-view.jsp").forward(request, response);
    }

    //    Kiểm tra trạng thái hợp lệ cho thao tác của Admin.
    private boolean isValidStatusChange(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        currentStatus = currentStatus.trim().toLowerCase();
        newStatus = newStatus.trim().toLowerCase();

        if ("pending".equals(currentStatus)) {
            return "shipped".equals(newStatus) || "canceled".equals(newStatus);
        }
        if ("shipped".equals(currentStatus)) {
            return false;
        }
        if ("completed".equals(currentStatus) || "canceled".equals(currentStatus)) {
            return false;
        }
        return false;
    }

    private Integer parseIntegerOrNull(String value) {
        try {
            if (value == null || value.isBlank()) {
                return null;
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            logger.warn("Cannot parse integer value: {}", value);
            return null;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}