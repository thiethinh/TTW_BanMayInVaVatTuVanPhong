package com.papercraft.controller.client;

import com.papercraft.dao.NotificationDAO;
import com.papercraft.dao.UserDAO;
import com.papercraft.model.Notification;
import com.papercraft.model.User;
import com.papercraft.model.enums.NotificationType;
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

@WebServlet(name = "ChangePasswordServlet", value = "/change-password")
public class ChangePasswordServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user == null) {
            logger.warn("Yêu cầu GET /change-password bị từ chối: Người dùng chưa đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        logger.info("Tải giao diện đổi mật khẩu cho User ID: '{}'", user.getId());
        request.getRequestDispatcher("WEB-INF/views/client/password-change.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user == null) {
            logger.warn("Yêu cầu POST /change-password bị từ chối: Phiên làm việc hết hạn hoặc chưa đăng nhập.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        String error = null;
        logger.info("Bắt đầu xử lý yêu cầu đổi mật khẩu cho User ID: '{}'", user.getId());

        String oldPassHash = MD5.getMD5(oldPassword);
        if (!user.getPasswordHash().equals(oldPassHash)) {
            error = "Mật khẩu cũ không chính xác";
            logger.warn("Đổi mật khẩu thất bại cho User ID '{}': Mật khẩu cũ không chính xác.", user.getId());
        } else if (!newPassword.equals(confirmPassword)) {
            error = "Mật khẩu xác nhận không trùng khớp";
            logger.warn("Đổi mật khẩu thất bại cho User ID '{}': Mật khẩu xác nhận không khớp.", user.getId());
        } else if (!newPassword.matches("^(?=.*[0-9])(?=.*[!@#$%^&+=])(?=\\S+$).{8,}$")) {
            error = "Mật khẩu mới yếu! Cần ít nhất 8 kí tự, có số và kí tự đặc biệt";
            logger.warn("Đổi mật khẩu thất bại cho User ID '{}': Mật khẩu mới không thỏa mãn chính sách độ mạnh.", user.getId());
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.getRequestDispatcher("WEB-INF/views/client/password-change.jsp").forward(request, response);
        } else {
            UserDAO userDAO = new UserDAO();
            String newPassHash = MD5.getMD5(newPassword);

            logger.debug("Tiến hành cập nhật mật khẩu mới vào cơ sở dữ liệu cho User ID: '{}'", user.getId());
            boolean isChanged = userDAO.changePassword(user.getId(), newPassHash);

            if (isChanged) {
                logger.info("Cập nhật DB thành công. Tiến hành tạo thông báo PASSWORD_CHANGED cho User ID: '{}'", user.getId());
                Notification noti = new Notification(user.getId(), NotificationType.PASSWORD_CHANGED, null);
                new NotificationDAO().insertNotification(noti);
            }

            if (isChanged) {
                logger.info("User ID '{}' đã đổi mật khẩu thành công. Tiến hành đồng bộ lại Session.", user.getId());
                user.setPasswordHash(newPassHash);
                session.setAttribute("acc", user);
                request.setAttribute("success", "Đổi mật khẩu thành công");
            } else {
                logger.error("Lỗi hệ thống: Câu lệnh cập nhật mật khẩu tại DB thất bại đối với User ID '{}'", user.getId());
                request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại");
            }
            request.getRequestDispatcher("WEB-INF/views/client/password-change.jsp").forward(request, response);
        }
    }
}
