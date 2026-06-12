package com.papercraft.controller.admin;

import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.UserDAO;
import com.papercraft.model.Order;
import com.papercraft.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminAccDetailsServlet", value = "/admin/admin-account-details")
public class AdminAccDetailsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminAccDetailsServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idRaw = request.getParameter("id");
        logger.debug("Nhận yêu cầu xem chi tiết tài khoản với tham số id raw: '{}'", idRaw);

        int id = Integer.parseInt(idRaw);

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUserById(id);

        OrderDAO orderDAO = new OrderDAO();
        List<Order> orderList = orderDAO.getOrdersByUserId(id);

        if (user != null) {
            logger.info("Tìm thấy người dùng '{}' (Email: {}). Đang tải thông tin đơn hàng.", user.getFullname(), user.getEmail());
            logger.debug("Số lượng đơn hàng tìm thấy cho User ID {}: {} đơn hàng.", id, (orderList != null ? orderList.size() : 0));

            request.setAttribute("acc", user);
            request.setAttribute("orderList", orderList);

            logger.debug("Chuyển tiếp (forward) dữ liệu sang giao diện admin-account-details.jsp");
            request.getRequestDispatcher("/WEB-INF/views/admin/admin-account-details.jsp").forward(request, response);
        } else {
            logger.warn("Không tìm thấy tài khoản nào có ID = {} trong hệ thống. Đang chuyển hướng về admin-account.", id);
            response.sendRedirect("admin-account");
        }
    }
}