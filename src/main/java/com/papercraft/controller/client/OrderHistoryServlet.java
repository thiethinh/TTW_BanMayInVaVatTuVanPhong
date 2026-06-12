package com.papercraft.controller.client;

import com.papercraft.dao.OrderDAO;
import com.papercraft.model.Order;
import com.papercraft.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderHistoryServlet", value = "/order-history")
public class OrderHistoryServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(OrderHistoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user == null) {
            logger.warn("Yêu cầu truy cập lịch sử đơn hàng bị từ chối: Người dùng chưa đăng nhập hệ thống.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        logger.info("Nhận yêu cầu tải lịch sử đơn hàng từ User ID: '{}', Email: '{}'", user.getId(), user.getEmail());

        OrderDAO orderDAO = new OrderDAO();
        logger.debug("Đang truy vấn danh sách đơn hàng từ CSDL cho User ID: '{}'...", user.getId());
        List<Order> orderList = orderDAO.getOrdersByUserId(user.getId());

        logger.info("Tải danh sách lịch sử thành công. Tìm thấy {} đơn hàng cho User ID: '{}'", orderList.size(), user.getId());
        request.setAttribute("orderList", orderList);

        logger.debug("Chuyển tiếp luồng (Forward) sang giao diện hiển thị order-history.jsp");
        request.getRequestDispatcher("/WEB-INF/views/client/order-history.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}