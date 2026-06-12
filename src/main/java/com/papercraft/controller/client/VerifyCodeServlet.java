package com.papercraft.controller.client;

import com.papercraft.dao.UserDAO;
import com.papercraft.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "VerifyCodeServlet", value = "/verify-code")
public class VerifyCodeServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(VerifyCodeServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String inputOTP = request.getParameter("otp");

        HttpSession session = request.getSession();
        String serverOTP = (String) session.getAttribute("authCode");
        User tempUser = (User) session.getAttribute("tempUser");

        if (serverOTP == null || tempUser == null) {
            logger.warn("Yêu cầu xác thực OTP bị từ chối: Phiên giao dịch (Session) của người dùng đã bị hủy hoặc hết hạn trước đó.");
            request.setAttribute("error", "Phiên giao dịch hết hạn vui lòng đăng ký lại");
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
            return;
        }

        logger.info("Nhận yêu cầu xác thực OTP từ Email: '{}'", tempUser.getEmail());

        Long createTime = (Long) session.getAttribute("REG_OTP_createTime");
        if (createTime != null && (System.currentTimeMillis() - createTime) > 300000) {
            logger.warn("Xác thực thất bại: Mã OTP gửi đến Email '{}' đã quá hạn 5 phút quy định.", tempUser.getEmail());
            request.setAttribute("error", "OTP đã hết hạn! Vui lòng bấm gửi lại mã");
            request.setAttribute("showVerifyModal", true);
            request.setAttribute("activeTab", "register");
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
            return;
        }

        // So khớp mã OTP người dùng nhập vào với mã lưu trên Server Session
        if (inputOTP != null && inputOTP.equals(serverOTP)) {
            logger.info("Mã OTP hoàn toàn chính xác. Tiến hành ghi nhận thông tin và tạo tài khoản chính thức cho Email: '{}' vào CSDL...", tempUser.getEmail());

            UserDAO dao = new UserDAO();
            dao.signup(tempUser);

            logger.debug("Tạo tài khoản thành công. Bắt đầu giải phóng và dọn dẹp toàn bộ dữ liệu tạm thời (OTP, TempUser, RateLimiter) ra khỏi Session...");
            session.removeAttribute("authCode");
            session.removeAttribute("tempUser");
            session.removeAttribute("REG_OTP_createTime");

            session.removeAttribute("OTP_resend_count");
            session.removeAttribute("OTP_lockout_time");

            session.setAttribute("msg", "Đăng ký thành công! Bạn có thể đăng nhập");

            logger.info("Quy trình đăng ký của Email '{}' đã hoàn tất tốt đẹp. Điều hướng sang trang đăng nhập.", tempUser.getEmail());
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            logger.warn("Xác thực thất bại: Người dùng nhập sai mã OTP đối với Email '{}'. (Mã nhập vào: '{}')", tempUser.getEmail(), inputOTP);
            request.setAttribute("error", "Mã OTP không đúng");
            request.setAttribute("showVerifyModal", true);
            request.setAttribute("activeTab", "register");
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
        }
    }
}