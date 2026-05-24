package com.papercraft.controller.client;

import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/order-success")
public class OrderSuccessServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Chặn user truy cập trực tiếp /order-success
        Boolean orderSuccess = (Boolean) session.getAttribute("orderSuccess");

        if (orderSuccess == null || !orderSuccess) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        // Xóa flag sau khi đã vào trang thành công để người dùng không vào lại /order-success trực tiếp nhiều lần
        session.removeAttribute("orderSuccess");

        ProductDAO productDAO = new ProductDAO();

        // Lấy 8 sản phẩm gợi ý
        List<Product> suggestedProducts = productDAO.getSuggestedProductsSimple(8);

        if (suggestedProducts == null) {
            suggestedProducts = new ArrayList<>();
        }

        request.setAttribute("suggestedProducts", suggestedProducts);

        request.getRequestDispatcher("/WEB-INF/views/client/order-success.jsp")
                .forward(request, response);
    }
}