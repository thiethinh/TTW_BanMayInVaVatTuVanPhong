package com.papercraft.controller.client;

import com.papercraft.dao.BannerDAO;
import com.papercraft.dao.ProductDAO;
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

@WebServlet(name = "ProductServlet", value = "/home")
public class HomeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.info("Bắt đầu tải dữ liệu trang chủ hệ thống (Home Page).");

        ProductDAO dao = new ProductDAO();
        BannerDAO bannerDAO = new BannerDAO();

        logger.debug("Đang truy vấn danh sách URL Banner đang hoạt động...");
        List<String> banners = bannerDAO.getActiveUrlBannerImage();

        logger.debug("Đang truy vấn danh sách sản phẩm nổi bật thuộc nhóm 'Printer'...");
        List<Product> featuredPrinter = dao.getFeaturedProductsByType("Printer");

        logger.debug("Đang truy vấn danh sách sản phẩm nổi bật thuộc nhóm 'Stationery'...");
        List<Product> featuredStationery = dao.getFeaturedProductsByType("Stationery");

        logger.info("Tải dữ liệu trang chủ thành công. Số lượng Banner: {}, Số lượng Máy in: {}, Số lượng Văn phòng phẩm: {}",
                banners.size(), featuredPrinter.size(), featuredStationery.size());

        request.setAttribute("printers", featuredPrinter);
        request.setAttribute("stationery", featuredStationery);
        request.setAttribute("banners", banners);

        logger.debug("Chuyển tiếp luồng (Forward) dữ liệu sang giao diện home.jsp");
        request.getRequestDispatcher("/WEB-INF/views/client/home.jsp").forward(request, response);
    }
}
