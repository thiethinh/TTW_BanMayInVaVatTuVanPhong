package com.papercraft.controller.admin;

import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Order;
import com.papercraft.model.Product;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminOrderManage", value = "/admin-order-manage")
public class AdminOrderManage extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       String status =request.getParameter("status");
        status=(status==null||status.isEmpty())? "":status;
        OrderDAO orderDAO = new OrderDAO();

        int pageSize = 15;
        int currentPage = 1;
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            }else{
                currentPage = 1;
            }
        } catch (NumberFormatException e) {
            currentPage = 1;
        }

        int offset = (currentPage-1) *pageSize;

        int totalOrders = orderDAO.getTotalCount(status);

        int totalPages = (int) Math.ceil((double) totalOrders / pageSize);

        List<Order> orders = orderDAO.getOrderByState(status, pageSize, offset);


        request.setAttribute("orders", orders);
        request.setAttribute("status", status);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-order-manage.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Code xử lý yêu cầu POST
    }
}