package com.papercraft.controller.client;

import com.papercraft.dao.UserDAO;
import com.papercraft.model.User;
import com.papercraft.utils.MD5;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("cEmail".equals(cookie.getName())) {
                    request.setAttribute("cEmail", cookie.getValue());
                }
                if ("cPassword".equals(cookie.getName())) {
                    request.setAttribute("cPassword", cookie.getValue());
                    request.setAttribute("cRemember", "checked");
                }
            }
        }
        request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        UserDAO userDAO = new UserDAO();
        String passwordHash = MD5.getMD5(password);
        User user = userDAO.login(email, passwordHash);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("acc", user);
            session.setAttribute("success", "Bạn đã đăng nhập thành công!");

            Cookie uEmail = new Cookie("cEmail", email);
            Cookie uPassword = new Cookie("cPassword", password);
            uEmail.setHttpOnly(true);
            uPassword.setHttpOnly(true);
            uEmail.setPath("/");
            uPassword.setPath("/");

            if ("on".equals(remember)) {
                uEmail.setMaxAge(60 * 60 * 24 * 7);
                uPassword.setMaxAge(60 * 60 * 24 * 7);
            } else {
                uEmail.setMaxAge(0);
                uPassword.setMaxAge(0);
            }

            response.addCookie(uEmail);
            response.addCookie(uPassword);

            String redirectUrl = request.getParameter("redirect");
            String contextPath = request.getContextPath();

            if (redirectUrl != null && !redirectUrl.trim().isEmpty() && !redirectUrl.equalsIgnoreCase("null")) {
                String finalRedirect;
                if (redirectUrl.startsWith("http") || redirectUrl.startsWith(contextPath)) {
                    finalRedirect = redirectUrl;
                } else {
                    finalRedirect = contextPath + (redirectUrl.startsWith("/") ? "" : "/") + redirectUrl;
                }
                response.sendRedirect(finalRedirect);
            } else {
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect(contextPath + "/admin");
                } else {
                    response.sendRedirect(contextPath + "/home");
                }
            }
        } else {
            request.setAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
            request.setAttribute("email", email);
            request.setAttribute("redirect", request.getParameter("redirect"));
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
        }
    }
}