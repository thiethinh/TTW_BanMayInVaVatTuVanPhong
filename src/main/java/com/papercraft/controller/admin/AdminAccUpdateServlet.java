package com.papercraft.controller.admin;

import com.papercraft.dao.UserDAO;
import com.papercraft.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminAccUpdateServlet", value = "/admin-account-update")
public class AdminAccUpdateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idRaw = request.getParameter("id");
        int id = Integer.parseInt(idRaw);

        UserDAO  userDAO = new UserDAO();
        User user = userDAO.getUserById(id);

        if (user != null) {
            request.setAttribute("acc", user);
            request.getRequestDispatcher("/WEB-INF/views/admin/admin-account-update.jsp").forward(request, response);
        } else {
            response.sendRedirect("/admin-account");
        }
    }
}
