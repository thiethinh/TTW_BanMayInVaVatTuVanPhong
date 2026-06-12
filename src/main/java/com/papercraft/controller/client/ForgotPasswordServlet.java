package com.papercraft.controller.client;

import com.papercraft.dao.UserDAO;
import com.papercraft.utils.EmailUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    private static final Logger logger =  LoggerFactory.getLogger(ForgotPasswordServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Tải giao diện trang Quên mật khẩu (forgot-password.jsp).");
        request.setAttribute("showOTPField", false);
        request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String otp = request.getParameter("otp");
        HttpSession session = request.getSession();
        UserDAO userDAO = new UserDAO();

        if (otp == null) {
            logger.info("Nhận yêu cầu tạo/gửi mã OTP phục vụ Quên mật khẩu cho Email: '{}'", email);

            Long lastCreateTime = (Long) session.getAttribute("OTP_createTime");
            if (lastCreateTime != null && System.currentTimeMillis() - lastCreateTime < 60000) {
                logger.warn("Yêu cầu gửi OTP bị chặn do spam từ Email '{}' (Chưa đủ 60 giây giãn cách).", email);
                request.setAttribute("error", "Vui lòng đợi 60 giây trước khi yêu cầu gửi lại OTP");
                request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
                return;
            }

            if (userDAO.checkEmailExists(email)) {
                String genOTP = EmailUtils.generateOTP();
                logger.debug("Hệ thống tạo mã OTP thành công. Tiến hành gửi qua EmailUtils...");
                boolean isSent = EmailUtils.sendForgotPasswordOTP(email, genOTP);

                if (isSent) {
                    logger.info("Gửi email chứa OTP tới '{}' thành công. Đã lưu cấu hình vào Session.", email);
                    session.setAttribute("OTP_CODE", genOTP);
                    session.setAttribute("RESET_EMAIL", email);
                    session.setAttribute("OTP_createTime", System.currentTimeMillis());

                    request.setAttribute("success", "Mã OTP đã được gửi đến email của bạn!");
                    request.setAttribute("showOTPField", true);
                } else {
                    logger.error("Lỗi hệ thống: Gửi mail chứa OTP tới '{}' thất bại qua SMTP Server.", email);
                    request.setAttribute("error", "Gửi email thất bại! Vui lòng kiểm tra lại kết nối");
                }

                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
                return;
            } else {
                logger.warn("Yêu cầu gửi OTP thất bại: Email '{}' không tồn tại trên hệ thống dữ liệu.", email);
                request.setAttribute("error", "Email không tồn tại trong hệ thống!");
                request.setAttribute("showOTPField", false);
                request.setAttribute("email", email);
                request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
                return;
            }
        }

        logger.info("Bắt đầu thực hiện xác thực mã OTP cho Email: '{}'", email);
        String systemOtp = (String) session.getAttribute("OTP_CODE");
        Long createTime = (Long) session.getAttribute("OTP_createTime");

        if (systemOtp == null || createTime == null) {
            logger.warn("Xác thực thất bại: Phiên làm việc (Session chứa OTP) của '{}' không tồn tại hoặc đã bị hủy trước đó.", email);
            request.setAttribute("error", "Phiên giao dịch đã hết hạn. Vui lòng lấy lại mã.");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
            return;
        }

        if(System.currentTimeMillis() - createTime > 300000) {
            logger.warn("Xác thực thất bại: Mã OTP của Email '{}' đã quá hạn 5 phút (Vượt ngưỡng thời gian hiệu lực).", email);
            session.removeAttribute("OTP_CODE");
            request.setAttribute("error", "Mã OTP đã hết hạn! Vui lòng gửi lại mã.");
            request.setAttribute("showOTPField", true);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
            return;
        }

        if (systemOtp.equals(otp)) {
            logger.info("Xác thực thành công! Mã OTP của Email '{}' trùng khớp hoàn toàn. Chuyển hướng quyền thiết lập lại mật khẩu.", email);
            session.removeAttribute("OTP_CODE");
            session.removeAttribute("OTP_createTime");
            session.setAttribute("success", "Nhập OTP thành công, vui lòng nhập mật khẩu mới");
            session.setAttribute("IS_VERIFIED", true);
            response.sendRedirect(request.getContextPath() + "/forgot-password");
        } else {
            logger.warn("Xác thực thất bại: Mã OTP người dùng nhập vào ('{}') không khớp mã hệ thống cấp cho Email '{}'.", otp, email);
            request.setAttribute("error", "Mã OTP không chính xác!");
            request.setAttribute("showOTPField", true);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
        }
    }
}