package com.papercraft.controller.client;

import com.papercraft.model.User;
import com.papercraft.utils.EmailUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet(name = "ResendOTPServlet", value = "/resend-otp")
public class ResendOTPServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ResendOTPServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();

        User tempUser = (User) session.getAttribute("tempUser");
        String resetEmail = (String) session.getAttribute("RESET_EMAIL");

        if (tempUser == null && resetEmail == null) {
            logger.warn("Yêu cầu gửi lại OTP bị từ chối: Phiên làm việc (Session) không chứa thông tin tempUser hoặc RESET_EMAIL.");
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Phiên giao dịch hết hạn, vui lòng thao tác lại!\"}");
            return;
        }

        long currentTime = System.currentTimeMillis();

        Long lockoutTime = (Long) session.getAttribute("OTP_lockout_time");
        if (lockoutTime != null && currentTime < lockoutTime) {
            long remainingTime = (lockoutTime - currentTime) / 60000;
            logger.warn("Yêu cầu gửi lại OTP cho '{}' bị chặn do tài khoản đang trong trạng thái khóa tạm thời (Lockout). Thời gian còn lại: ~{} phút.", resetEmail, remainingTime + 1);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Vui lòng thử lại sau " + (remainingTime + 1) + " phút\"}");
            return;
        }

        logger.info("Nhận yêu cầu gửi lại mã OTP cho Email mục tiêu: '{}' (Luồng: '{}')",
                resetEmail, (tempUser != null ? "Đăng ký thành viên" : "Quên mật khẩu"));

        Long lastCreatedTime = (Long) session.getAttribute("REG_OTP_createTime");
        if (lastCreatedTime != null && (currentTime - lastCreatedTime) < 30000) {
            long remainingSeconds = 30 - ((currentTime - lastCreatedTime) / 1000);
            logger.warn("Yêu cầu gửi lại OTP cho '{}' bị chặn do thao tác quá nhanh (Chưa đủ 30 giây giãn cách). Còn thiếu: {} giây.", resetEmail, remainingSeconds);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Vui lòng đợi 30s trước khi gửi lại\"}");
            return;
        }

        Integer count = (Integer) session.getAttribute("OTP_resend_count");
        int resendCount = count == null ? 0 : count;
        if (resendCount >= 3) {
            session.setAttribute("OTP_lockout_time", currentTime + 900000);
            logger.warn("CẢNH BÁO BẢO MẬT: Email '{}' đã vượt quá giới hạn 3 lần gửi lại mã OTP liên tiếp. Tiến hành kích hoạt hình phạt khóa (Lockout) trong 15 phút.", resetEmail);
            session.removeAttribute("OTP_resend_count");
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Vượt quá 3 lần. Vui lòng thử lại sau 15 phút\"}");
            return;
        }

        logger.debug("Mọi điều kiện kiểm tra tần suất đều hợp lệ (Lần yêu cầu: {}). Bắt đầu sinh mã OTP mới...", resendCount + 1);
        String newOTP = EmailUtils.generateOTP();
        boolean isSent = false;
        if (tempUser != null) {
            logger.debug("Gọi dịch vụ gửi OTP đăng ký thành viên tới: '{}'", tempUser.getEmail());
            isSent = EmailUtils.sendRegisterOTP(tempUser.getEmail(), newOTP);
            if (isSent) session.setAttribute("authCode", newOTP);
        } else if (resetEmail != null) {
            logger.debug("Gọi dịch vụ gửi OTP khôi phục mật khẩu tới: '{}'", resetEmail);
            isSent = EmailUtils.sendForgotPasswordOTP(resetEmail, newOTP);
            if (isSent) session.setAttribute("OTP_CODE", newOTP);
        }

        if (isSent) {
            logger.info("Gửi lại mã OTP thành công đến Email: '{}'. Cập nhật lại số lần gửi và dấu mốc thời gian vào Session.", resetEmail);
            session.setAttribute("OTP_resend_count", resendCount + 1);
            session.setAttribute("REG_OTP_createTime", currentTime);
            response.getWriter().write("{\"status\": \"success\", \"message\": \"Gửi lại mã thành công\"}");
        } else {
            logger.error("Lỗi hệ thống: Không thể gửi lại mã OTP tới '{}' thông qua Mail Server.", resetEmail);
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Hệ thống gửi mail đang bận, vui lòng thử lại sau\"}");
        }
    }
}
