package com.papercraft.controller.client;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", value = "/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("acc");

        // Lấy CurentPage trước khi nhấn Logout
        String referer = request.getHeader("Referer");

        //nếu có trang trước đó thì quay lại, không thì về home
        if (referer != null && !referer.contains("/logout")) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect("home");
        }
    }
}
