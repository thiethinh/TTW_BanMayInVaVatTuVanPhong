package com.papercraft.controller.client;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@WebServlet(name = "PrinterServlet", urlPatterns = {"/printer"})
public class PrinterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(PrinterServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String search = request.getParameter("search");
        String categoryRaw = request.getParameter("category");
        String sort = request.getParameter("sort");
        String brand = request.getParameter("brand");

        search = (search == null || search.isBlank()) ? null : search.trim();
        brand = (brand == null || brand.isBlank()) ? null : brand.trim();

        int categoryId = 0;
        if (categoryRaw != null && !categoryRaw.isBlank()) {
            try {
                categoryId = Integer.parseInt(categoryRaw);
            } catch (NumberFormatException ignored) {
                logger.error("Định dạng tham số danh mục 'category' gửi lên không phải kiểu số hợp lệ: '{}'", categoryRaw);
            }
        }

        if (sort == null || sort.isBlank()) {
            sort = "rating";
        }

        logger.info("Nhận yêu cầu lọc danh sách Máy in. Tiêu chí tìm kiếm -> Từ khóa: '{}', Category ID: {}, Thương hiệu: '{}', Sắp xếp: '{}'",
                search, categoryId, brand, sort);

        ProductDAO productDAO = new ProductDAO();
        logger.debug("Đang truy vấn danh sách sản phẩm Máy in theo bộ lọc...");
        List<Product> printers = productDAO.filterProduct("Printer", search, categoryId, brand, sort);
        if (printers == null) {
            logger.debug("Danh sách máy in trả về bị null, khởi tạo danh sách trống.");
            printers = new ArrayList<>();
        }

        CategoryDAO categoryDAO = new CategoryDAO();
        logger.debug("Đang lấy danh sách tất cả danh mục thuộc nhóm 'Printer'...");
        List<Category> categories = categoryDAO.getAllCategories("Printer");
        if (categories == null) {
            logger.debug("Danh sách danh mục trả về bị null, khởi tạo danh sách trống.");
            categories = new ArrayList<>();
        }

        logger.debug("Đang lấy danh sách toàn bộ thương hiệu thuộc nhóm 'Printer'...");
        Set<String> brands = productDAO.getAllBrandByType("Printer");
        logger.info("Tải dữ liệu thành công. Kết quả lọc: {} Máy in, {} Danh mục, {} Thương hiệu.",
                printers.size(), categories.size(), (brands != null ? brands.size() : 0));

        request.setAttribute("searchReturn", search);
        request.setAttribute("categoryIdReturn", categoryId);
        request.setAttribute("sortReturn", sort);
        request.setAttribute("brandReturn", brand);
        request.setAttribute("printers", printers);
        request.setAttribute("categories", categories);
        request.setAttribute("brands", brands);

        logger.debug("Chuyển tiếp luồng (Forward) sang giao diện hiển thị printer.jsp");
        request.getRequestDispatcher("/WEB-INF/views/client/printer.jsp").forward(request, response);
    }

}