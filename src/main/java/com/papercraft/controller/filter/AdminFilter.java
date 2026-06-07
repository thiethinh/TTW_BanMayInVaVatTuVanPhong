package com.papercraft.controller.filter;

import com.papercraft.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "AdminFilter", urlPatterns = {"/*"})
public class AdminFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        // Gom điều kiện: Nếu vào vùng /admin mà chưa log hoặc ko phải admin -> Redirect
        if ((uri.equals("/admin") || uri.startsWith("/admin/"))) {
            User user = (User) req.getSession().getAttribute("acc");

            if (user == null || user.getRole()!="admin") {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}