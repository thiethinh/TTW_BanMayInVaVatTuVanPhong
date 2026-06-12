package com.papercraft.controller.client;

import com.papercraft.dao.UserDAO;
import com.papercraft.model.User;
import com.papercraft.utils.EmailUtils;
import com.papercraft.utils.MD5;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "RegisterServlet", value = "/register")
public class RegisterServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fname = request.getParameter("firstname") != null ? request.getParameter("firstname").trim() : "";
        String lname = request.getParameter("lastname") != null ? request.getParameter("lastname").trim() : "";
        String email = request.getParameter("email") != null ? request.getParameter("email").trim() : "";
        String phone = request.getParameter("phone") != null ? request.getParameter("phone").trim() : "";
        String gender = request.getParameter("gender");
        String password = request.getParameter("password") != null ? request.getParameter("password") : "";
        String confirmPassword = request.getParameter("confirmPassword") != null ? request.getParameter("confirmPassword") : "";

        logger.info("Nhận yêu cầu đăng ký tài khoản mới. Email: '{}', Họ tên: '{} {}', Số điện thoại: '{}'", email, fname, lname, phone);

        UserDAO dao = new UserDAO();
        List<String> errors = new ArrayList<>();

        // Validation
        if (fname.isEmpty()) errors.add("Họ không được để trống");
        if (lname.isEmpty()) errors.add("Tên không được để trống");
        if (gender == null || gender.isEmpty()) errors.add("Vui lòng chọn giới tính");

        if (phone.isEmpty() || !phone.matches("^0\\d{9}$")) {
            errors.add("Số điện thoại không hợp lệ");
        }

        if (email.isEmpty() || !email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            errors.add("Email không hợp lệ");
        } else if (dao.checkEmailExists(email)) {
            errors.add("Email đã được sử dụng");
        }

        if (!password.equals(confirmPassword)) {
            errors.add("Mật khẩu nhập lại không khớp");
        }
        if (!password.matches("^(?=.*[0-9])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$")) {
            errors.add("Mật khẩu yếu! Cần tối thiểu 8 kí tự, có số và kí tự đặc biệt");
        }

        if (!errors.isEmpty()) {
            logger.warn("Dữ liệu đăng ký của Email '{}' không hợp lệ. Số lượng lỗi phát hiện: {}. Chi tiết lỗi: {}", email, errors.size(), errors);
            request.setAttribute("errorRegister", errors);
            setFormDataToRequest(request, fname, lname, email, phone, gender);
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
            return;
        }

        // Chống spam otp
        HttpSession session = request.getSession();
        Long lastCreateTime = (Long) session.getAttribute("REG_OTP_createTime");
        if (lastCreateTime != null && (System.currentTimeMillis() - lastCreateTime) < 60000) {
            logger.warn("Yêu cầu gửi mã OTP đăng ký từ Email '{}' bị chặn do spam (Chưa đủ 60 giây giãn cách).", email);
            errors.add("Vui lòng đợi 60 giây trước khi yêu cầu gửi lại OTP.");
            request.setAttribute("errorRegister", errors);
            setFormDataToRequest(request, fname, lname, email, phone, gender);
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
            return;
        }

        // Chuẩn bị dữ liệu để gửi
        logger.debug("Thông tin biểu mẫu hợp lệ. Tiến hành tạo mã OTP và mã hóa mật khẩu tài khoản...");
        String otp = EmailUtils.generateOTP();
        User newUser = new User();
        newUser.setFname(fname);
        newUser.setLname(lname);
        newUser.setEmail(email);
        newUser.setPhoneNumber(phone);
        newUser.setGender(gender);
        newUser.setPasswordHash(MD5.getMD5(password));

        logger.debug("Đang gửi email chứa mã xác thực OTP đăng ký tới '{}'...", newUser.getEmail());
        boolean isSent = EmailUtils.sendRegisterOTP(newUser.getEmail(), otp);

        // Kết quả gửi email
        if (isSent) {
            logger.info("Gửi email chứa OTP thành công tới '{}'. Cấu hình thông tin tạm thời vào Session thời hạn 5 phút (300s).", newUser.getEmail());
            session.setAttribute("authCode", otp);
            session.setAttribute("tempUser", newUser);
            session.setAttribute("REG_OTP_createTime", System.currentTimeMillis());
            session.setMaxInactiveInterval(300);

            String redirectUrl = request.getParameter("redirect");
            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                logger.debug("Lưu giữ URL điều hướng sau đăng ký: '{}'", redirectUrl);
                session.setAttribute("redirectAfterRegister", redirectUrl);
            }

            request.setAttribute("showVerifyModal", true);
            request.setAttribute("activeTab", "register");
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
        } else {
            logger.error("Lỗi hệ thống: Gửi mail chứa OTP tới '{}' thất bại từ hệ thống Mail Server.", newUser.getEmail());
            errors.add("Gửi email thất bại! Vui lòng kiểm tra lại kết nối hoặc email.");
            request.setAttribute("errorRegister", errors);
            setFormDataToRequest(request, fname, lname, email, phone, gender);
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
        }
    }

    private void setFormDataToRequest(HttpServletRequest request, String fname, String lname, String email, String phone, String gender) {
        request.setAttribute("valueFName", fname);
        request.setAttribute("valueLName", lname);
        request.setAttribute("valueEmail", email);
        request.setAttribute("valuePhone", phone);
        request.setAttribute("valueGender", gender);
        request.setAttribute("activeTab", "register");
    }
}
