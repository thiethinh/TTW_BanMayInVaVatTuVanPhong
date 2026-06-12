package com.papercraft.controller.admin;

import com.papercraft.dao.*;
import com.papercraft.model.*;
import com.papercraft.model.enums.NotificationType;
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
        String orderID = request.getParameter("orderId");
        String verifyPayment = request.getParameter("verifyPayment");
        String transactionCode = request.getParameter("transactionCode");
        HttpSession session = request.getSession();
        User userSession = (User) session.getAttribute("acc");

        String accept = request.getParameter("accept");
        String cancel = request.getParameter("cancel");

        logger.debug("Nhận yêu cầu GET vào AdminOrderViewServlet. orderIdRaw='{}', verifyPayment='{}', accept='{}', cancel='{}'",
                orderID, verifyPayment, accept, cancel);

        int id = orderID != null ? Integer.parseInt(orderID) : 0;
        OrderDAO orderDAO = new OrderDAO();

        boolean updated = false;
        boolean isAccept = false;
        boolean isCancel = false;

        NotificationType typeNoti = null;
        if (accept != null) {
            typeNoti = NotificationType.ORDER_SHIPPED;
        } else if (cancel != null) {
            typeNoti = NotificationType.ORDER_CANCELLED;
        }

        NotificationDAO notificationDAO = new NotificationDAO();
        if (typeNoti != null) {
            Notification noti = new Notification(userSession.getId(), typeNoti, id);
            notificationDAO.insertNotification(noti);
            logger.debug("Đã lưu thông báo loại '{}' cho đơn hàng ID: {} bởi Admin ID: {}", typeNoti, id, userSession.getId());
        } else {
            logger.warn("Không thể tạo thông báo đổi trạng thái đơn hàng vì phiên làm việc (Session) của Admin đã hết hạn.");
        }

        if (accept != null) {
            Order currentOrder = orderDAO.getOrderByID(id);

            if (currentOrder != null && isValidStatusChange(currentOrder.getStatus(), accept)) {
                updated = orderDAO.updateOrderStatus(id, accept);
                isAccept = true;
                logger.info("Cập nhật trạng thái đơn hàng ID {} thành công sang [Duyệt/Giao hàng: '{}']. Kết quả CSDL: {}", id, accept, updated);
            } else {
                logger.warn("Hành động duyệt đơn hàng ID {} bị từ chối do trạng thái hiện tại [{}] không hợp lệ để chuyển sang [{}]",
                        id, (currentOrder != null ? currentOrder.getStatus() : "NULL"), accept);
            }

        } else if (cancel != null) {
            Order currentOrder = orderDAO.getOrderByID(id);

            if (currentOrder != null && isValidStatusChange(currentOrder.getStatus(), cancel)) {
                updated = orderDAO.updateOrderStatus(id, cancel);
                isCancel = true;
                logger.info("Cập nhật trạng thái đơn hàng ID {} thành công sang [Hủy: '{}']. Kết quả CSDL: {}", id, cancel, updated);
            } else {
                logger.warn("Hành động hủy đơn hàng ID {} bị từ chối do trạng thái hiện tại [{}] không hợp lệ để chuyển sang [{}]",
                        id, (currentOrder != null ? currentOrder.getStatus() : "NULL"), cancel);
            }
        }


        // Xuwr lys VerifyPayment
        boolean isVeryfyPayment = false;
        boolean verifiedPayment = false;

        if (verifyPayment != null) {
            PaymentDAO paymentDAO = new PaymentDAO();
            Payment currentPayment = paymentDAO.getPaymentByOrderId(id);
            Order currentOrder = orderDAO.getOrderByID(id);

            if (currentPayment != null && !Boolean.TRUE.equals(currentPayment.getStatus())) {
                String method = currentPayment.getPaymentMethod();
                logger.info("Bắt đầu xác thực thanh toán cho đơn hàng ID: {}. Phương thức: '{}', Mã giao dịch: '{}'", id, method, transactionCode);

                //Gia su cho COD verify khi owr trang thai shipping/complete
                if ("COD".equalsIgnoreCase(method)) {
                    if (currentOrder != null && ("shipped".equalsIgnoreCase(currentOrder.getStatus())
                            || "completed".equalsIgnoreCase(currentOrder.getStatus()))) {
                        verifiedPayment = paymentDAO.verifyPaymentSuccess(id, transactionCode);
                        isVeryfyPayment = true;
                    } else {
                        logger.warn("Xác thực thanh toán thất bại cho đơn COD ID {}: Đơn hàng phải có trạng thái 'shipped' hoặc 'completed' thay vì [{}]",
                                id, (currentOrder != null ? currentOrder.getStatus() : "NULL"));
                    }
                } else {
                    verifiedPayment = paymentDAO.verifyPaymentSuccess(id, transactionCode);
                    isVeryfyPayment = true;
                }
                logger.info("Kết quả xác thực thanh toán đơn hàng ID {}: {}", id, verifiedPayment);
            }
        }


        Order order = orderDAO.getOrderByID(id);

        if (order == null) {
            logger.error("Không tìm thấy đơn hàng trong hệ thống ứng với ID: {}. Hủy tải trang chi tiết.", id);
            return;
        }

        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> orderItems = orderItemDAO.getItemByOrderId(id);
        order.setOrderItems(orderItems);

        User user = new UserDAO().getBasicInfoById(order.getUserId());

        Payment payment = new PaymentDAO().getPaymentByOrderId(id);
        logger.debug("Tải thành công toàn bộ dữ liệu đơn hàng ID {}. Số lượng sản phẩm: {}, Khách hàng: '{}'",
                id, (orderItems != null ? orderItems.size() : 0), (user != null ? user.getEmail() : "N/A"));

        request.setAttribute("order", order);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("user", user);
        request.setAttribute("payment", payment);
        request.setAttribute("updated", updated);
        request.setAttribute("isAccept", isAccept);
        request.setAttribute("isCancel", isCancel);
        request.setAttribute("isVerifyPayment", isVeryfyPayment);
        request.setAttribute("verifiedPayment", verifiedPayment);

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-order-view.jsp").forward(request, response);

    }

    private boolean isValidStatusChange(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        currentStatus = currentStatus.trim().toLowerCase();
        newStatus = newStatus.trim().toLowerCase();

        switch (currentStatus) {
            case "pending":
                return newStatus.equals("shipped") || newStatus.equals("canceled");

            case "shipped":
                return newStatus.equals("completed") || newStatus.equals("canceled");

            case "completed":
            case "canceled":
                return false;

            default:
                return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Code xử lý yêu cầu POST
    }
}