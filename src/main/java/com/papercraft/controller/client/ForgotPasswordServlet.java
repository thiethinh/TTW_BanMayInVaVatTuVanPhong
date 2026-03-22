package com.papercraft.controller.client;

import com.papercraft.dao.UserDAO;
import com.papercraft.utils.EmailUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("text/plain; charset=UTF-8");

        if ("sendOTP".equals(action)) {
            String email = request.getParameter("email");
            UserDAO userDAO = new UserDAO();
            HttpSession session = request.getSession();

            // Chống spam
            Long lastCreateTime = (Long) session.getAttribute("OTP_createTime");
            if (lastCreateTime != null && System.currentTimeMillis() - lastCreateTime < 60000) {
                response.setStatus(429);
                response.getWriter().write("Vui lòng đợi 60 giây trước khi yêu cầu gửi lại OTP.");
                return;
            }

            // Kiểm tra email có trong db chưa
            if (userDAO.checkEmailExists(email)) {
                String otp = EmailUtils.generateOTP();
                boolean isSent = EmailUtils.sendForgotPasswordOTP(email, otp);

                if (isSent) {
                    session.setAttribute("OTP_CODE", otp);
                    session.setAttribute("RESET_EMAIL", email);
                    session.setAttribute("OTP_createTime", System.currentTimeMillis());

                    response.getWriter().write("Thành công");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Gửi email thất bại! Vui lòng kiểm tra lại kết nối hoặc email.");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("Email không tồn tại trong hệ thống!");
            }
        } else {
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userOtp = request.getParameter("otp");
        HttpSession session = request.getSession();
        String systemOtp = (String) session.getAttribute("OTP_CODE");
        Long createTime = (Long) session.getAttribute("OTP_createTime");

        if (systemOtp == null || createTime == null) {
            request.setAttribute("message", "Phiên giao dịch đã hết hạn hoặc không hợp lệ. Vui lòng lấy lại mã.");
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
            return;
        }

        if (System.currentTimeMillis() - createTime > 300000) {
            session.removeAttribute("OTP_CODE");
            request.setAttribute("message", "Mã OTP đã hết hạn!");
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
            return;
        }

        if (systemOtp.equals(userOtp)) {
            session.removeAttribute("OTP_CODE");
            session.removeAttribute("OTP_createTime");

            session.setAttribute("IS_VERIFIED", true);
            response.sendRedirect("reset-password");
        } else {
            request.setAttribute("message", "Mã OTP sai hoặc đã hết hạn!");
            request.getRequestDispatcher("/WEB-INF/views/client/forgot-password.jsp").forward(request, response);
        }
    }
}