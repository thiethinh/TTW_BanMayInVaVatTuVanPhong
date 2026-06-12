package com.papercraft.controller.admin;

import com.papercraft.dao.ImageDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Product;
import com.papercraft.service.CloudinaryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.cloudinary.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AdminProductAddServlet", value = "/admin/admin-product-add")
@MultipartConfig
public class AdminProductAddServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminProductAddServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Nhận yêu cầu GET: Hiển thị giao diện thêm mới sản phẩm.");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-product-add.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        List<String> uploadedFiles = new ArrayList<>();
        String name = req.getParameter("name");
        logger.info("Bắt đầu xử lý luồng POST thêm sản phẩm mới. Tên sản phẩm gửi lên: '{}'", name);

        try {
            int categoryId = Integer.parseInt(req.getParameter("categoryId"));
            double originPrice = Double.parseDouble(req.getParameter("price"));
            double discount = Double.parseDouble(req.getParameter("discount"));
            int stock = Integer.parseInt(req.getParameter("stock"));
            String description = req.getParameter("description");
            String details = req.getParameter("details");
            String specs = req.getParameter("specs");

            if (name == null || name.isBlank()) {
                throw new RuntimeException("Tên sản phẩm không được để trống");
            }

            String brand = "";
            if (specs != null && !specs.isBlank()) {
                try {
                    JSONObject obj = new JSONObject(specs);
                    brand = obj.optString("brand", "");
                } catch (Exception e) {
                    logger.warn("Không thể phân tích cú pháp chuỗi JSON 'specs' để tìm thương hiệu (brand). Chuỗi raw: '{}'. Lỗi: {}", specs, e.getMessage());
                }
            }

            Part thumbPart = req.getPart("image");
            if (thumbPart == null || thumbPart.getSize() == 0) {
                throw new RuntimeException("Vui lòng chọn ảnh đại diện");
            }

            String thumbName = Paths.get(thumbPart.getSubmittedFileName()).getFileName().toString();
            File tempThumb = File.createTempFile("thumb_", ".tmp");
            logger.debug("Tạo file tạm cho ảnh đại diện tại đường dẫn: {}", tempThumb.getAbsolutePath());
            try {
                thumbPart.write(tempThumb.getAbsolutePath());
                CloudinaryService.upload(tempThumb, thumbName);
                uploadedFiles.add(thumbName);
                logger.info("Upload thành công ảnh đại diện '{}' lên Cloudinary.", thumbName);
            } finally {
                boolean isDeleted = tempThumb.delete();
                if (isDeleted) {
                    logger.debug("Đã dọn dẹp sạch file tạm ảnh đại diện.");
                } else {
                    logger.warn("Không thể xóa file tạm ảnh đại diện tại: {}", tempThumb.getAbsolutePath());
                }
            }

            List<String> galleryNames = new ArrayList<>();
            List<Part> galleryParts = new ArrayList<>();

            for (Part p : req.getParts()) {
                if ("gallery".equals(p.getName())
                        && p.getSize() > 0
                        && p.getSubmittedFileName() != null
                        && !p.getSubmittedFileName().isBlank()) {
                    galleryParts.add(p);
                }
            }
            logger.debug("Tìm thấy {} tệp tin hợp lệ nằm trong mục Gallery ảnh.", galleryParts.size());

            if (galleryParts.size() > 5) {
                throw new RuntimeException("Tối đa 5 ảnh gallery");
            }

            for (Part p : galleryParts) {
                String fileName = Paths.get(p.getSubmittedFileName()).getFileName().toString();
                File temp = File.createTempFile("gallery_", ".tmp");
                try {
                    p.write(temp.getAbsolutePath());
                    CloudinaryService.upload(temp, fileName);
                    galleryNames.add(fileName);
                    uploadedFiles.add(fileName);
                    logger.debug("Upload thành công ảnh thuộc bộ sưu tập: '{}'", fileName);
                } finally {
                    boolean isDeleted = temp.delete();
                    if (!isDeleted) {
                        logger.warn("Không thể xóa file tạm bộ sưu tập tại: {}", temp.getAbsolutePath());
                    }
                }
            }

            Product product = new Product();
            product.setCategoryId(categoryId);
            product.setProductName(name);
            product.setDescriptionThumbnail(details);
            product.setProductDescription(description);
            product.setProductDetail(specs);
            product.setBrand(brand);
            product.setOriginPrice(originPrice);
            product.setDiscount(discount);
            product.setStockQuantity(stock);

            logger.info("Tiến hành ghi nhận thông tin sản phẩm vào Cơ sở dữ liệu...");
            ProductDAO productDAO = new ProductDAO();
            boolean inserted = productDAO.insertProduct(product);
            if (!inserted) {
                throw new RuntimeException("Không thể thêm sản phẩm");
            }
            int productId = product.getId();
            logger.info("Thêm sản phẩm thành công! ID vừa sinh ra: {}", productId);

            ImageDAO imageDAO = new ImageDAO();
            imageDAO.insertImage(productId, "Product", thumbName, true);
            logger.debug("Đã map ảnh đại diện '{}' với sản phẩm ID {}", thumbName, productId);

            for (String img : galleryNames) {
                imageDAO.insertImage(productId, "Product", img, false);
                logger.debug("Đã map ảnh gallery '{}' với sản phẩm ID {}", img, productId);
            }

            logger.info("Hoàn tất nghiệp vụ thêm sản phẩm. Thực hiện chuyển hướng (redirect)...");
            resp.sendRedirect(req.getContextPath() + "/admin/admin-product?msg=add_success");

        } catch (Exception e) {
            logger.error("Xảy ra lỗi hệ thống nghiêm trọng trong quá trình thêm sản phẩm mới '{}': ", name, e);

            for (String file : uploadedFiles) {
                try {
                    CloudinaryService.delete(file);
                } catch (Exception ignore) {
                }
            }

            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/views/admin/admin-product-add.jsp").forward(req, resp);
        }
    }
}
