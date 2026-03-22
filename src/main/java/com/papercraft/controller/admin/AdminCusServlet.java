package com.papercraft.controller.admin;

import com.papercraft.dao.UserDAO;
import com.papercraft.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminCusServlet", value = "/admin-account")
public class AdminCusServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDAO userDAO = new UserDAO();
        request.setCharacterEncoding("UTF-8");

        //  XỬ LÝ KHÓA / MỞ KHÓA
        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if (idParam != null && action != null) {
            try {
                int uid = Integer.parseInt(idParam);
                if ("lock".equals(action)) {
                    userDAO.updateUserStatus(uid, false);
                } else if ("unlock".equals(action)) {
                    userDAO.updateUserStatus(uid, true);
                } else if ("set-role".equals(action)) {
                    String newRole = request.getParameter("role");
                    if ("admin".equals(newRole) || "user".equals(newRole)) {
                        userDAO.updateUserRole(uid, newRole);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Redirect lại trang quản lý
            response.sendRedirect("admin-account");
            return;
        }

        // Lấy từ khóa tìm kiếm
        String keyword = request.getParameter("search-customer");
        if (keyword == null) keyword = "";

        // Lấy bộ lọc trạng thái
        String statusFilter = request.getParameter("select-sort");
        if (statusFilter == null) statusFilter = "all";

        //  Phân trang
        int page = 1;
        int pageSize = 10;
        try {
            String p = request.getParameter("page");
            if (p != null) page = Integer.parseInt(p);
        } catch (Exception e) { page = 1; }

        //  Gọi DAO
        int totalUsers = userDAO.countCustomers(keyword, statusFilter);
        int totalPages = (int) Math.ceil((double) totalUsers / pageSize);
        List<User> userList = userDAO.getCustomersPagination(keyword, statusFilter, page, pageSize);

        request.setAttribute("userList", userList);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("keyword", keyword);
        request.setAttribute("statusFilter", statusFilter);

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-customer-manage.jsp").forward(request, response);
    }
}