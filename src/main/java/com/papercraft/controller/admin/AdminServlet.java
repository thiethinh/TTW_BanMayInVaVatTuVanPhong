package com.papercraft.controller.admin;

import com.papercraft.dao.ContactDAO;
import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.PaymentDAO;
import com.papercraft.dao.UserDAO;
import com.papercraft.model.Order;
import com.papercraft.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminServlet", value = "/admin")
public class AdminServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user == null || !user.getRole().equals("admin") && !user.getRole().equals("mod")) {
            logger.warn("Cảnh báo bảo mật: Truy cập trái phép vào vùng quản trị (/admin). Đối tượng: {}",
                    (user != null ? "User ID: " + user.getId() + " [Role: " + user.getRole() + "]" : "Khách vô danh"));
            session.setAttribute("acc", user);
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        boolean logout = request.getParameter("logout") != null;
        if (logout) {
            logger.info("Admin/Mod ID '{}' yêu cầu đăng xuất khỏi hệ thống.", user.getId());
            session.invalidate();
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        logger.debug("Tài khoản '{}' (Role: {}) truy cập trang Dashboard. Bắt đầu tổng hợp số liệu thống kê...", user.getId(), user.getRole());

        PaymentDAO paymentDAO = new PaymentDAO();
        double totalRevenue = paymentDAO.getTotalRevenueByMonthNow();

        OrderDAO orderDAO = new OrderDAO();
        Integer totalpendingOrder = orderDAO.totalPendingOrder();

        UserDAO userDAO = new UserDAO();
        Integer totalUser = userDAO.totalUser();

        ContactDAO contactDAO = new ContactDAO();
        Integer totalUnrepliedContact = contactDAO.totalUnrepliedContact();

        List<Order> orders = orderDAO.getTop10PendingOrder();
        logger.debug("Tổng hợp dữ liệu Dashboard thành công. Doanh thu: {}, Đơn chờ: {}, Khách hàng: {}, Liên hệ mới: {}",
                totalRevenue, orders, totalUser, totalUnrepliedContact);

        request.setAttribute("orders", orders);
        request.setAttribute("totalRevenue", totalRevenue);
        request.setAttribute("totalpendingOrder", totalpendingOrder);
        request.setAttribute("totalUnrepliedContact", totalUnrepliedContact);
        request.setAttribute("totalUser", totalUser);

        logger.debug("Chuyển tiếp luồng (forward) sang trang giao diện quản trị admin.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}