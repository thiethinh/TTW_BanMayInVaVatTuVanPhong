package com.papercraft.controller.client;

import com.papercraft.dao.ProductDAO;
import com.papercraft.dao.ReviewDAO;
import com.papercraft.model.Product;
import com.papercraft.model.Review;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet(name = "ProductDetailServlet", value = "/product-detail")
public class ProductDetailServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ProductDetailServlet.class);

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String productId = request.getParameter("productId");
            if (productId == null) {
                logger.warn("Yêu cầu xem chi tiết sản phẩm bị từ chối: Thiếu tham số 'productId'.");
                response.sendRedirect("home");
                return;
            }
            int id = Integer.parseInt(productId);
            logger.info("Nhận yêu cầu tải trang chi tiết sản phẩm ID: '{}'", id);

            // Lấy thông tin sản phẩm
            ProductDAO dao = new ProductDAO();
            logger.debug("Đang truy vấn thông tin cơ bản và bộ sưu tập ảnh của sản phẩm ID: '{}'...", id);
            Product product = dao.getProductById(id);
            List<String> listImages = dao.getAllImageOfProduct(id);

            // Lấy đánh giá
            ReviewDAO reviewDao = new ReviewDAO();
            logger.debug("Đang tải danh sách đánh giá (Reviews) của sản phẩm ID: '{}'...", id);
            List<Review> reviewList = reviewDao.getReviewsByProductId(id);

            double avgRating = 0.0;
            if (!reviewList.isEmpty()) {
                double total = 0;
                for (Review review : reviewList) {
                    total += review.getRating();
                }
                avgRating = total / reviewList.size();
                avgRating = Math.round(avgRating * 10.0) / 10.0;
                logger.debug("Tính toán điểm đánh giá trung bình cho sản phẩm ID '{}': {} sao (Tổng số: {} đánh giá)",
                        id, avgRating, reviewList.size());
            }
            product.setAvgRating(BigDecimal.valueOf(avgRating));

            request.setAttribute("p", product);
            request.setAttribute("listImages", listImages);
            request.setAttribute("reviewList", reviewList);
            request.setAttribute("countReview", reviewList.size());

            logger.info("Tải dữ liệu chi tiết sản phẩm '{}' thành công. Chuyển tiếp luồng sang product-details.jsp", product.getProductName());
            request.getRequestDispatcher("/WEB-INF/views/client/product-details.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Lỗi hệ thống nghiêm trọng khi tải dữ liệu chi tiết sản phẩm: ", e);
            response.sendRedirect("home");
        }
    }
}
