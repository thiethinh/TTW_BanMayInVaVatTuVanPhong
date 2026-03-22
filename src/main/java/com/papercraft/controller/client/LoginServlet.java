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

            Cookie uEmail = new Cookie("cEmail", email);
            Cookie uPassword = new Cookie("cPassword", password);

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
            if (redirectUrl != null && !redirectUrl.trim().isEmpty()) {
                response.sendRedirect(redirectUrl);
            } else {
                User acc = (User) session.getAttribute("acc");
                if("ADMIN".equalsIgnoreCase(acc.getRole())) {
                    response.sendRedirect("admin");
                } else {
                    response.sendRedirect("home");
                }
            }
        } else {
            request.setAttribute("msg", "Tài khoản hoặc mật khẩu không đúng");
            request.setAttribute("email", email);
            request.getRequestDispatcher("/WEB-INF/views/client/login.jsp").forward(request, response);
        }
    }
}
