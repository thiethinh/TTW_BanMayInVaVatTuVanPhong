package com.papercraft.controller.client;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", value = "/logout")
public class LogoutServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Nhận yêu cầu đăng xuất hệ thống từ người dùng.");

        logger.debug("Tiến hành xóa session...");
        // Xoá session
        request.getSession().invalidate();

        // Xóa Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            logger.debug("Tiến hành quét danh sách Cookies để xóa thông tin ghi nhớ...");
            for (Cookie cookie : cookies) {
                if ("cEmail".equals(cookie.getName()) || "cPassword".equals(cookie.getName())) {
                    Cookie c = new Cookie(cookie.getName(), "");
                    c.setMaxAge(0);
                    c.setPath("/");
                    response.addCookie(c);
                }
            }
        }

        String referer = request.getHeader("Referer");
        HttpSession session = request.getSession();
        session.setAttribute("success", "Đăng xuất thành công");

        if (referer != null && !referer.contains("/logout")) {
            logger.info("Đăng xuất hoàn tất. Điều hướng người dùng quay lại trang trước đó: '{}'", referer);
            response.sendRedirect(referer);
        } else {
            logger.info("Đăng xuất hoàn tất. Không tìm thấy trang trước đó hoặc URL không hợp lệ. Điều hướng mặc định về trang chủ.");
            response.sendRedirect("home");
        }
    }
}
