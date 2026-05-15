package com.papercraft.controller.admin;

import com.papercraft.dao.InventoryDAO;
import com.papercraft.model.InventoryTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminInventoryHistoryServlet", value = "/admin/inventory-history")
public class AdminInventoryHistoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        if (type == null) type = "all";

        InventoryDAO inventoryDAO = new InventoryDAO();
        List<InventoryTransaction> transactions = inventoryDAO.getAllTransactions(type);

        request.setAttribute("transactions", transactions);
        request.setAttribute("type", type);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-inventory-history.jsp").forward(request, response);
    }
}
