package com.papercraft.controller.client;

import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.OrderItemDAO;
import com.papercraft.dao.PaymentDAO;
import com.papercraft.dao.UserDAO;
import com.papercraft.model.Order;
import com.papercraft.model.OrderItem;
import com.papercraft.model.Payment;
import com.papercraft.model.User;
import com.papercraft.service.OrderService;
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

@WebServlet(name = "OrderViewServlet", value = "/order-view")
public class OrderViewServlet extends HttpServlet {

    private Logger logger = LoggerFactory.getLogger(OrderViewServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user == null) {
            logger.warn("Yêu cầu xem chi tiết đơn hàng bị từ chối: Người dùng chưa đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String orderIdStr = request.getParameter("orderId");
        int orderId = orderIdStr != null ? Integer.parseInt(orderIdStr) : 0;

        logger.info("User ID '{}' (Role: '{}') yêu cầu xem chi tiết đơn hàng ID: '{}'", user.getId(), user.getRole(), orderId);

        OrderDAO orderDAO = new OrderDAO();
        Order order = orderDAO.getOrderByID(orderId);

        if (order == null || order.getUserId() != user.getId() && !user.getRole().equalsIgnoreCase("admin") && !user.getRole().equalsIgnoreCase("mod")) {
            logger.warn("CẢNH BÁO BẢO MẬT: Người dùng ID '{}' cố gắng truy cập trái phép hoặc đơn hàng ID '{}' không tồn tại.", user.getId(), orderId);
            response.sendRedirect(request.getContextPath() + "/order-history");
            return;
        }

        logger.debug("Xác thực quyền xem đơn hàng thành công. Tiến hành nạp dữ liệu chi tiết đơn hàng...");

        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> orderItems = orderItemDAO.getItemByOrderId(orderId);

        UserDAO userDAO = new UserDAO();
        User orderUser = userDAO.getBasicInfoById(order.getUserId());

        PaymentDAO paymentDAO = new PaymentDAO();
        Payment payment = paymentDAO.getPaymentByOrderId(orderId);

        request.setAttribute("order", order);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("user", orderUser);
        request.setAttribute("payment", payment);

        logger.info("Tải thông tin đơn hàng ID '{}' thành công. Chuyển tiếp luồng sang giao diện order-view.jsp", orderId);
        request.getRequestDispatcher("/WEB-INF/views/client/order-view.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Hủy đơn hàng
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user == null) {
            logger.warn("Yêu cầu thao tác hủy đơn hàng bị từ chối: Người dùng chưa đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        String orderIdStr = request.getParameter("orderId");
        if ("cancel".equals(action)) {
            int orderId = Integer.parseInt(orderIdStr);
            logger.warn("Yêu cầu thao tác hủy đơn hàng bị từ chối: Người dùng chưa đăng nhập.");

            OrderDAO orderDAO = new OrderDAO();
            Order order = orderDAO.getOrderByID(orderId);

            if (order != null && order.getUserId() == user.getId() && "pending".equalsIgnoreCase(order.getStatus())) {
                logger.debug("Đơn hàng hợp lệ và đang ở trạng thái 'pending'. Tiến hành gọi OrderService để hủy và hoàn lại kho...");
                OrderService orderService = new OrderService();

                boolean isCanceled = orderService.cancelOrderAndReleaseStock(orderId);

                if (isCanceled) {
                    logger.info("Hủy đơn hàng ID '{}' thành công và đã giải phóng số lượng tồn kho.", orderId);
                    session.setAttribute("successMsg", "Đã hủy đơn hàng thành công! Số lượng sản phẩm đã được hoàn lại kho.");
                } else {
                    logger.error("Lỗi nghiệp vụ: Gọi OrderService hủy đơn hàng ID '{}' thất bại.", orderId);
                    session.setAttribute("errorMsg", "Hủy đơn hàng thất bại, vui lòng thử lại!");
                }
            } else {
                logger.warn("Yêu cầu hủy đơn hàng ID '{}' không hợp lệ. Nguyên nhân có thể do: Đơn hàng không tồn tại, không thuộc quyền sở hữu của User ID '{}' hoặc trạng thái hiện tại không phải 'pending' (Trạng thái thực tế: '{}').",
                        orderId, user.getId(), (order != null ? order.getStatus() : "NULL"));
                session.setAttribute("errorMsg", "Không thể hủy đơn hàng");
            }
            response.sendRedirect(request.getContextPath() + "/order-view?orderId=" + orderId);
        } else {
            logger.warn("Nhận yêu cầu POST tại /order-view với hành động 'action' không được hỗ trợ: '{}'", action);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
