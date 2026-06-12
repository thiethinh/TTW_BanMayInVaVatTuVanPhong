package com.papercraft.controller.client;

import com.papercraft.dao.CategoryDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Category;
import com.papercraft.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@WebServlet(name = "StationeryServlet", urlPatterns = {"/stationery"})
public class StationeryServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(StationeryServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String search = request.getParameter("search");
        String categoryIdRaw = request.getParameter("category");
        String sort = request.getParameter("sort");
        String brand = request.getParameter("brand");

        int categoryId = 0;
        if (categoryIdRaw != null && !categoryIdRaw.isEmpty()) {
            try {
                categoryId = Integer.parseInt(categoryIdRaw);
            } catch (NumberFormatException e) {
                logger.error("Định dạng tham số mã danh mục 'category' gửi lên không đúng kiểu số: '{}'", categoryIdRaw);
                categoryId = 0;
            }
        }

        // Chuẩn hóa dữ liệu bộ lọc đầu vào
        search = (search == null || search.isEmpty() || search.isBlank()) ? null : search.trim();
        sort = (sort == null || sort.isEmpty() || sort.isBlank()) ? "rating" : sort;
        brand = (brand == null || brand.isEmpty() || brand.isBlank()) ? null : brand.trim();

        logger.info("Nhận yêu cầu tải danh sách Văn phòng phẩm. Tiêu chí bộ lọc -> Từ khóa: '{}', Category ID: {}, Thương hiệu: '{}', Tiêu chí xếp: '{}'",
                search, categoryId, brand, sort);

        ProductDAO dao = new ProductDAO();

        logger.debug("Đang truy vấn danh sách Văn phòng phẩm (Stationery) từ cơ sở dữ liệu dựa trên bộ lọc...");
        List<Product> stationery = dao.filterProduct("Stationery", search, categoryId, brand, sort);

        // Kiểm tra null an toàn
        if (stationery == null) {
            logger.debug("Danh sách sản phẩm văn phòng phẩm trả về bị null, khởi tạo danh sách trống.");
            stationery = new ArrayList<>();
        }

        // Lấy danh sách danh mục để hiển thị trong dropdown filter
        CategoryDAO categoryDAO = new CategoryDAO();
        logger.debug("Đang lấy danh sách các danh mục thuộc nhóm 'Stationery'...");
        List<Category> categories = categoryDAO.getAllCategories("Stationery");
        if (categories == null) {
            logger.debug("Danh sách danh mục văn phòng phẩm bị null, khởi tạo danh sách trống.");
            categories = new ArrayList<>();
        }

        logger.debug("Đang lấy danh sách toàn bộ các thương hiệu của nhóm sản phẩm 'Stationery'...");
        Set<String> brands = dao.getAllBrandByType("Stationery");
        if (brands == null) {
            logger.debug("Danh sách thương hiệu bị null, khởi tạo cấu trúc TreeSet trống.");
            brands = new TreeSet<>();
        }

        logger.info("Tải dữ liệu hoàn tất. Kết quả tìm kiếm: {} sản phẩm, {} phân loại danh mục, {} thương hiệu sẵn có.",
                stationery.size(), categories.size(), brands.size());

        // Gửi dữ liệu sang JSP
        request.setAttribute("stationery", stationery);
        request.setAttribute("categories", categories);
        request.setAttribute("brands", brands);

        // Gửi lại các giá trị cũ để giữ trạng thái cho các ô input trên giao diện
        request.setAttribute("searchReturn", search);
        request.setAttribute("categoryIdReturn", categoryId);
        request.setAttribute("sortReturn", sort);
        request.setAttribute("brandReturn", brand);

        logger.debug("Chuyển tiếp luồng xử lý dữ liệu (Forward) sang giao diện stationery.jsp");
        request.getRequestDispatcher("/WEB-INF/views/client/stationery.jsp").forward(request, response);
    }
}