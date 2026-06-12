package com.papercraft.controller.admin;

import com.papercraft.dao.BannerDAO;
import com.papercraft.model.Banner;
import com.papercraft.service.CloudinaryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@WebServlet("/admin/admin-banner")
@MultipartConfig
public class AdminBannerServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminBannerServlet.class);
    private final BannerDAO bannerDAO = new BannerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        logger.debug("Nhận yêu cầu GET với action: '{}'", action);

        if (action != null) {
            switch (action) {
                case "toggle":
                    toggleBanner(request, response);
                    return;

                case "delete":
                    deleteBanner(request, response);
                    return;
                case "get-active":
                    getActiveBanner(request, response);
                    return;
                case "edit":
                    editBanner(request, response);
                    return;
                case "add-banner":
                    addBanner(request, response);
                    return;
                default:
                    logger.warn("Hành động GET '{}' không hợp lệ, chuyển về tải trang mặc định.", action);
            }
        }

        loadBannerPage(request, response);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        logger.info("Nhận yêu cầu POST với hành động: '{}'", action);

        if ("update".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            int sortOrder = Integer.parseInt(request.getParameter("sortOrder"));
            boolean active = request.getParameter("active") != null;
            String oldImage = request.getParameter("oldImage");
            logger.info("Bắt đầu cập nhật Banner ID: {} [Tiêu đề: '{}', Thứ tự: {}, Active: {}]", id, title, sortOrder, active);

            Part imagePart = request.getPart("image");
            String fileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
            String imageName = oldImage;

            // Có upload ảnh mới
            if (fileName != null && !fileName.isBlank()) {
                logger.info("Phát hiện ảnh mới được upload: '{}'. Tiến hành đẩy lên Cloudinary...", fileName);
                File tempFile = File.createTempFile("banner_", ".tmp");
                try {
                    imagePart.write(tempFile.getAbsolutePath());
                    CloudinaryService.upload(tempFile, fileName);
                    imageName = fileName;
                    logger.debug("Tải ảnh mới lên Cloudinary thành công.");
                } catch (Exception e) {
                    logger.error("Lỗi xảy ra khi tải ảnh lên Cloudinary cho Banner ID {}: ", id, e);
                    throw e;
                } finally {
                    if (tempFile.exists()) {
                        boolean deleted = tempFile.delete();
                        logger.debug("Xóa file tạm thời '{}': {}", tempFile.getName(), deleted);
                    }
                }
            } else {
                logger.debug("Không có ảnh mới được chọn. Sử dụng lại ảnh cũ: '{}'", oldImage);
            }

            Banner b = new Banner();
            b.setId(id);
            b.setTitle(title);
            b.setImgName(imageName);
            b.setActive(active);
            b.setSortOrder(sortOrder);

            BannerDAO dao = new BannerDAO();
            dao.updateBanner(b);
            logger.info("Cập nhật thành công Banner ID: {} vào cơ sở dữ liệu.", id);

            response.sendRedirect("admin-banner");
        } else if ("insert".equals(action)) {
            BannerDAO dao = new BannerDAO();

            String title = request.getParameter("title");
            int sortOrder = Integer.parseInt(request.getParameter("sortOrder"));
            boolean active = request.getParameter("active") != null;
            logger.info("Bắt đầu thêm mới Banner [Tiêu đề: '{}', Thứ tự: {}, Active: {}]", title, sortOrder, active);

            Part imagePart = request.getPart("image");
            String fileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();

            if (fileName == null || fileName.isBlank()) {
                logger.warn("Thêm banner thất bại: Admin không lựa chọn file ảnh.");
                throw new RuntimeException("Vui lòng chọn ảnh");
            }

            logger.info("Tiến hành tải ảnh '{}' lên Cloudinary...", fileName);
            File tempFile = File.createTempFile("banner_", ".tmp");
            try {
                imagePart.write(tempFile.getAbsolutePath());
                CloudinaryService.upload(tempFile, fileName);
                logger.debug("Tải ảnh lên Cloudinary thành công.");
            } catch (Exception e) {
                logger.error("Lỗi xảy ra khi tải ảnh lên Cloudinary trong quá trình thêm mới: ", e);
                throw e;
            } finally {
                if (tempFile.exists()) {
                    boolean deleted = tempFile.delete();
                    logger.debug("Xóa file tạm thời '{}': {}", tempFile.getName(), deleted);
                }
            }
            Banner banner = new Banner();
            banner.setTitle(title);
            banner.setImgName(fileName);
            banner.setActive(active);
            banner.setSortOrder(sortOrder);

            dao.insertBanner(banner);
            logger.info("Thêm mới Banner thành công vào cơ sở dữ liệu.");
            response.sendRedirect("admin-banner");
        } else {
            logger.warn("Hành động POST '{}' không hợp lệ.", action);
            response.sendRedirect("admin-banner");
        }
    }


    private void loadBannerPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");

        if (keyword == null) {
            keyword = "";
        }

        logger.info("Tải danh sách Banner với từ khóa tìm kiếm: '{}'", keyword);
        List<Banner> banners = bannerDAO.getAllBanner(keyword);

        request.setAttribute("banners", banners);
        request.setAttribute("keyword", keyword);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-banner.jsp").forward(request, response);
    }

    private void toggleBanner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        logger.info("Thực hiện bật/tắt (toggle) trạng thái hoạt động của Banner ID: {}", id);
        bannerDAO.toggleBanner(id);
        response.sendRedirect("admin-banner");
    }

    private void deleteBanner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        logger.info("Yêu cầu xóa Banner ID: {}", id);
        bannerDAO.deleteBanner(id);
        logger.info("Đã xóa thành công Banner ID: {} khỏi cơ sở dữ liệu.", id);
        response.sendRedirect("admin-banner");
    }

    private void getActiveBanner(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        logger.info("Lọc danh sách các Banner đang hoạt động (Active).");
        List<Banner> banners = bannerDAO.getActiveBanner();
        request.setAttribute("banners", banners);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-banner.jsp").forward(request, response);
    }

    private void editBanner(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        logger.info("Yêu cầu xem form chỉnh sửa cho Banner ID: {}", id);
        BannerDAO dao = new BannerDAO();
        Banner banner = dao.getBannerById(id);

        if (banner != null) {
            request.setAttribute("banner", banner);
            request.getRequestDispatcher("/WEB-INF/views/admin/admin-banner-edit.jsp").forward(request, response);
        } else {
            logger.warn("Không tìm thấy Banner có ID: {} để chỉnh sửa. Điều hướng lại.", id);
            response.sendRedirect("admin-banner");
        }
    }

    private void addBanner(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Chuyển hướng sang giao diện thêm mới banner (admin-banner-add.jsp).");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-banner-add.jsp").forward(request, response);
    }
}
