package com.papercraft.controller.admin;

import com.papercraft.dao.BannerDAO;
import com.papercraft.model.Banner;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin-banner")
public class AdminBannerServlet extends HttpServlet {

    private final BannerDAO bannerDAO = new BannerDAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action != null) {

            switch (action) {

                case "toggle":

                    toggleBanner(request, response);
                    return;

                case "delete":

                    deleteBanner(request, response);
                    return;
            }
        }

        loadBannerPage(request, response);
    }

    private void loadBannerPage(HttpServletRequest request,
                                HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");

        if (keyword == null) {
            keyword = "";
        }

        List<Banner> banners = bannerDAO.getAllBanner(keyword);
        for( Banner b : banners){
            System.out.println(b.getImagePath());
        }

        request.setAttribute("banners", banners);
        request.setAttribute("keyword", keyword);

        request.getRequestDispatcher(
                "/WEB-INF/views/admin/admin-banner.jsp"
        ).forward(request, response);
    }

    private void toggleBanner(HttpServletRequest request,
                              HttpServletResponse response)
            throws IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        bannerDAO.toggleBanner(id);

        response.sendRedirect("admin-banner");
    }

    private void deleteBanner(HttpServletRequest request,
                              HttpServletResponse response)
            throws IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        bannerDAO.deleteBanner(id);

        response.sendRedirect("admin-banner");
    }
}
