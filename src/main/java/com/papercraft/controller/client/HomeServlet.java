package com.papercraft.controller.client;

import com.papercraft.dao.BannerDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Banner;
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
        BannerDAO bannerDAO= new BannerDAO();

        List<String> banners = bannerDAO.getActiveUrlBannerImage();
        for( String url : banners){
            System.out.println("Danh sach banner url"+url);

        }

        List<Product> featuredPrinter = dao.getFeaturedProductsByType("Printer");
        for( Product  p : featuredPrinter){
            System.out.println("Danh sach printer url: "+p.getThumbnail());

        }
        List<Product> featuredStationery = dao.getFeaturedProductsByType("Stationery");



        request.setAttribute("printers", featuredPrinter);
        request.setAttribute("stationery", featuredStationery);
        request.setAttribute("banners", banners);
        request.getRequestDispatcher("/WEB-INF/views/client/home.jsp").forward(request, response);
    }
}
