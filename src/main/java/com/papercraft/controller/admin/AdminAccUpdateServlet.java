package com.papercraft.controller.admin;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "AdminAccUpdateServlet", value = "/admin-account-update")
public class AdminAccUpdateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-account-update.jsp");
    }
}
