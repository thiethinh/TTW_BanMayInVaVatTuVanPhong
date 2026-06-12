package com.papercraft.controller.admin;

import com.google.gson.Gson;
import com.papercraft.dao.AnalyticsDAO;
import com.papercraft.dto.ProductPerformanceDTO;
import com.papercraft.dto.ProfitStatDTO;
import com.papercraft.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminAnalyticsServlet", value = "/admin/analytics-data")
public class AdminAnalyticsServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminAnalyticsServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");
        if (user == null || (!user.getRole().equals("admin") && !user.getRole().equals("mod"))) {
            logger.warn("Cảnh báo bảo mật: Tài khoản '{}' (Role: {}) cố gắng truy cập API thống kê mà không có quyền.",
                    user.getEmail(), user.getRole());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Không có quyền truy cập\"}");
            return;
        }

        String action = request.getParameter("action");
        logger.info("Tài khoản '{}' ({}) yêu cầu dữ liệu phân tích với action: '{}'",
                user.getEmail(), user.getRole(), action);

        AnalyticsDAO analyticsDAO = new AnalyticsDAO();
        Gson gson = new Gson();

        try {
            if ("profit".equals(action)) {
                String yearStr = request.getParameter("year");
                logger.debug("Tham số năm nhận được cho thống kê profit: '{}'", yearStr);
                int year = Integer.parseInt(yearStr);
                logger.info("Đang truy xuất dữ liệu lợi nhuận hàng tháng của năm: {}", year);

                List<ProfitStatDTO> profitStats = analyticsDAO.getMonthlyProfitStat(year);
                logger.debug("Truy xuất thành công dữ liệu lợi nhuận năm {}. Số lượng bản ghi: {}",
                        year, (profitStats != null ? profitStats.size() : 0));
                response.getWriter().write(gson.toJson(profitStats));
            } else if ("restock".equals(action)) {
                logger.info("Đang truy xuất dữ liệu hiệu suất sản phẩm và dự báo nhập hàng.");
                List<ProductPerformanceDTO> restockData = analyticsDAO.getProductPerformanceAndForecast();
                logger.debug("Truy xuất thành công dữ liệu dự báo. Số lượng sản phẩm phân tích: {}",
                        (restockData != null ? restockData.size() : 0));
                response.getWriter().write(gson.toJson(restockData));
            } else {
                logger.warn("Yêu cầu không hợp lệ. Hành động '{}' không được hỗ trợ.", action);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"Hành động không hợp lệ\"}");
            }
        } catch (Exception e) {
            logger.error("Lỗi hệ thống nghiêm trọng xảy ra khi xử lý phân tích dữ liệu (Action: {}): ", action, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Lỗi server:" + e.getMessage() + "\"}");
        }
    }
}
