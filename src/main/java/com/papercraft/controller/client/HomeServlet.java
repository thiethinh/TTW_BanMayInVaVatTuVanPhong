package com.papercraft.controller.client;

import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ProductServlet", value = "/home")
public class HomeServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO dao = new ProductDAO();

        List<Product> featuredPrinter = dao.getFeaturedProductsByType("Printer");
        List<Product> featuredStationery = dao.getFeaturedProductsByType("Stationery");

        request.setAttribute("printers", featuredPrinter);
        request.setAttribute("stationery", featuredStationery);

        request.getRequestDispatcher("/WEB-INF/views/client/home.jsp").forward(request, response);
    }
}
