package com.papercraft.controller;

import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "StationeryServlet", urlPatterns = {"/stationery"})
public class StationeryServlet extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {
        ProductDAO dao = new ProductDAO();
        List<Product> stationery = dao.getAllProduct("Stationery");
        request.setAttribute("stationery", stationery);

        request.getRequestDispatcher("/WEB-INF/views/client/stationery.jsp").forward(request, response);

    }

}