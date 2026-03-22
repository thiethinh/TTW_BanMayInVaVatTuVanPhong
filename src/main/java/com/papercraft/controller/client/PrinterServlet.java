package com.papercraft.controller.client;

import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;


@WebServlet(name = "PrinterServlet", urlPatterns = {"/printer"})
public class PrinterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        ProductDAO dao = new ProductDAO();
        List<Product> printers = dao.getAllProduct("Printer");
        request.setAttribute("printers", printers);

        request.getRequestDispatcher("/WEB-INF/views/client/printer.jsp").forward(request, response);
    }

}