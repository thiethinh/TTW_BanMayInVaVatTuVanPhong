package com.papercraft.controller.client;

import com.papercraft.dao.UserDAO;
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

@WebServlet(name = "ResetPasswordServlet", value = "/reset-password")
public class ResetPasswordServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();

        Boolean isVerified = (Boolean) session.getAttribute("IS_VERIFIED");
        String email = (String) session.getAttribute("RESET_EMAIL");

        if (email == null || !isVerified) {
            logger.warn("CẢNH BÁO BẢO MẬT: Phát hiện yêu cầu đặt lại mật khẩu không hợp lệ từ địa chỉ IP hoặc phiên không có quyền truy cập. Email có thể bị null hoặc trạng thái IS_VERIFIED chưa được kích hoạt.");
            request.setAttribute("error", "Phiên giao dịch không hợp lệ, vui lòng thử lại!");
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
            return;
        }

        String newPass = request.getParameter("newPass");
        String confirmPass = request.getParameter("confirmPassword");
        logger.info("Bắt đầu tiến trình đặt lại mật khẩu mới cho Email: '{}'", email);

        if (!newPass.equals(confirmPass)) {
            logger.warn("Cập nhật mật khẩu thất bại cho Email '{}': Mật khẩu nhập lại không trùng khớp.", email);
            request.setAttribute("error", "Mật khẩu không khớp!");
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
            return;
        }

        UserDAO userDAO = new UserDAO();
        logger.debug("Đang mã hóa mật khẩu mới bằng giải thuật MD5 và tiến hành cập nhật vào CSDL...");
        boolean isUpdated = userDAO.updatePasswordByEmail(email, MD5.getMD5(newPass));
        if (isUpdated) {
            logger.info("Đặt lại mật khẩu thành công cho Email: '{}'. Tiến hành dọn dẹp các cờ xác thực trong Session.", email);
            session.removeAttribute("IS_VERIFIED");
            session.removeAttribute("RESET_EMAIL");
            session.setAttribute("msg", "Đổi mật khẩu thành công vui lòng đăng nhập");

            logger.debug("Điều hướng người dùng về trang Đăng nhập qua Redirect.");
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            logger.error("Lỗi hệ thống: updatePasswordByEmail trả về giá trị false khi đang cố cập nhật mật khẩu cho Email: '{}'", email);
            request.setAttribute("error", "Lỗi hệ thống, không thể cập nhật mật khẩu lúc này!");
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
        }
    }
}
