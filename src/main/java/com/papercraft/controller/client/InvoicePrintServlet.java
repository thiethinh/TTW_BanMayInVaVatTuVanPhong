package com.papercraft.controller.client;

import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.OrderItemDAO;
import com.papercraft.dao.PaymentDAO;
import com.papercraft.dao.UserDAO;
import com.papercraft.model.Order;
import com.papercraft.model.OrderItem;
import com.papercraft.model.Payment;
import com.papercraft.model.User;
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

@WebServlet(name = "InvoicePrintServlet", value = "/invoice-print")
public class InvoicePrintServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(InvoicePrintServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("acc");

        if (currentUser == null) {
            logger.warn("Yêu cầu xem hóa đơn bị từ chối: Người dùng chưa đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String orderIdRaw = request.getParameter("orderId");
        int orderId;
        try {
            orderId = Integer.parseInt(orderIdRaw);
        } catch (Exception e) {
            logger.error("Định dạng tham số orderId gửi lên không hợp lệ: '{}'", orderIdRaw);
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        logger.info("User ID '{}' (Role: '{}') yêu cầu truy cập thông tin in hóa đơn ID: '{}'",
                currentUser.getId(), currentUser.getRole(), orderId);

        OrderDAO orderDAO = new OrderDAO();
        Order order = orderDAO.getOrderByID(orderId);

        if (order == null) {
            logger.warn("Không tìm thấy đơn hàng ID '{}' trong hệ thống dữ liệu.", orderId);
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        boolean isAdmin = "admin".equalsIgnoreCase(currentUser.getRole());
        boolean isOwner = order.getUserId() != null && order.getUserId() == currentUser.getId();
        if (!isAdmin && !isOwner) {
            logger.warn("CẢNH BÁO BẢO MẬT: User ID '{}' cố gắng truy cập trái phép hóa đơn ID '{}' của User ID '{}'!",
                    currentUser.getId(), orderId, order.getUserId());
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        logger.debug("Xác thực phân quyền thành công (IsAdmin: {}, IsOwner: {}). Tiến hành nạp dữ liệu chi tiết hóa đơn...", isAdmin, isOwner);

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

        logger.info("Tải dữ liệu hóa đơn ID '{}' thành công. Chuyển tiếp luồng hiển thị sang invoice-print.jsp", orderId);
        request.getRequestDispatcher("/WEB-INF/views/client/invoice-print.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Nhận yêu cầu POST tại /invoice-print. Chuyển tiếp luồng xử lý sang doGet().");
        doGet(request, response);
    }
}