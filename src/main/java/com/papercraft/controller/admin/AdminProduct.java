package com.papercraft.controller.admin;

import com.papercraft.dao.CategoryDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Category;
import com.papercraft.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminProduct", value = "/admin/admin-product")
public class AdminProduct extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminProduct.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();
        List<Product> products;

        String idDeleted = request.getParameter("delete");
        if (idDeleted != null) {
            logger.info("Nhận yêu cầu xóa sản phẩm. ID raw từ client: '{}'", idDeleted);
            boolean isDeleted = productDAO.deleteProductById(Integer.parseInt(idDeleted));

            if (isDeleted) {
                logger.info("Xóa thành công sản phẩm ID: {}", idDeleted);
            } else {
                logger.error("Thất bại khi thực thi xóa sản phẩm ID: {} tại cơ sở dữ liệu", idDeleted);
            }

            response.sendRedirect(request.getContextPath() + "/admin/admin-product?msg=" + (isDeleted ? "delete_success" : "delete_fail"));
            return;
        }

        // Tìm kiếm & Phân trang
        String keyword = request.getParameter("keyword");
        if (keyword != null) {
            keyword = keyword.trim();
            products = productDAO.searchByName(keyword);
            request.setAttribute("keyword", keyword);
            request.setAttribute("products", products);
            request.getRequestDispatcher("/WEB-INF/views/admin/admin-products.jsp").forward(request, response);
            return;
        }

        String type = request.getParameter("type");
        String categoryId = request.getParameter("category");

        if (type != null) {
            type = type.trim();
            if (type.isEmpty()) {
                type = null;
            }
        }

        if (categoryId != null) {
            categoryId = categoryId.trim();
            if (categoryId.isEmpty()) {
                categoryId = null;
            }
        }

        logger.debug("Thông số bộ lọc sản phẩm nhận được - Keyword: '{}', Type: '{}', CategoryId: '{}'", keyword, type, categoryId);

        if (type != null && categoryId == null) {
            logger.info("Thực hiện tìm kiếm sản phẩm theo từ khóa: '{}'", keyword);
            type = type.trim();
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.getAllCategories(type);
            products = productDAO.getAllProduct(type);

            logger.debug("Hoàn tất truy vấn dữ liệu. Gửi sang JSP {} sản phẩm và {} danh mục", products, categories);
            request.setAttribute("categories", categories);
            request.setAttribute("products", products);
            request.setAttribute("type", type);
            request.setAttribute("category", categoryId);

            request.getRequestDispatcher("/WEB-INF/views/admin/admin-products.jsp").forward(request, response);
            return;
        } else if (type != null && categoryId != null) {
            type = type.trim();
            categoryId = categoryId.trim();
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.getAllCategories(type);
            products = productDAO.getProductByTypeAndCategory(type, categoryId);
            request.setAttribute("categories", categories);
            request.setAttribute("products", products);
            request.setAttribute("type", type);
            request.setAttribute("category", categoryId);

            request.getRequestDispatcher("/WEB-INF/views/admin/admin-products.jsp").forward(request, response);
            return;
        }

        products = productDAO.getAllProduct();
        //  Gửi dữ liệu sang JSP
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-products.jsp").forward(request, response);
    }
}