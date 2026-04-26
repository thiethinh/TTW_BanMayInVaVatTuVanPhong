package com.papercraft.controller.admin;

import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Product;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminProduct", value = "/admin-product")
public class AdminProduct extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();


        String idDeleted = request.getParameter("delete");
        if (idDeleted != null) {
            boolean isDeleted = productDAO.deleteProductById(Integer.parseInt(idDeleted));
            request.setAttribute("isDeleted", isDeleted);
        }

        // Tìm kiếm & Phân trang
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";

        int page = 1;
        int pageSize = 10;

        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        //   lấy dữ liệu
        int totalProducts = productDAO.countProducts(keyword); //   tổng số
        int totalPages = (int) Math.ceil((double) totalProducts / pageSize); //   tổng số trang

        // Lấy danh sách sản phẩm cho trang hiện tại
        List<Product> products = productDAO.getAllProduct();

        //  Gửi dữ liệu sang JSP
        request.setAttribute("products", products);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Code xử lý yêu cầu POST
    }
}