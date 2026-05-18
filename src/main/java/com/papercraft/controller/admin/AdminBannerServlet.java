package com.papercraft.controller.admin;

import com.papercraft.dao.BannerDAO;
import com.papercraft.model.Banner;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@WebServlet("/admin-banner")
@MultipartConfig
public class AdminBannerServlet extends HttpServlet {

    private final BannerDAO bannerDAO = new BannerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if (action != null) {
            switch (action) {
                case "toggle":
                    toggleBanner(request, response);
                    return;

                case "delete":

                    deleteBanner(request, response);
                    return;
                case "get-active":
                    getActiveBanner(request, response);
                    return;
                case "edit":
                    editBanner(request, response);
                    return;
                case "add-banner":
                    addBanner(request, response);
                    return;
            }
        }

        loadBannerPage(request, response);
    }



    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        System.out.println(action);


        if("update".equals(action)){
            int id = Integer.parseInt(request.getParameter("id"));
            String title = request.getParameter("title");
            int sortOrder = Integer.parseInt(request.getParameter("sortOrder"));
            boolean active = request.getParameter("active") != null;
            String oldImage = request.getParameter("oldImage");

            Part imagePart = request.getPart("image");
            String fileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();

            String uploadPath = getServletContext().getRealPath("/images/upload");

            File uploadDir = new File(uploadPath);

            if(!uploadDir.exists()){
                uploadDir.mkdirs();
            }

            String imageName = oldImage;

            // có upload ảnh mới
            if(fileName != null && !fileName.isEmpty()){
                imageName = System.currentTimeMillis() + fileName;
                imagePart.write(uploadPath + File.separator + imageName);
            }

            Banner b = new Banner();
            b.setId(id);
            b.setTitle(title);
            b.setImgName(imageName);
            b.setImagePath(imageName);
            b.setActive(active);
            b.setSortOrder(sortOrder);

            BannerDAO dao = new BannerDAO();
            dao.updateBanner(b);
            response.sendRedirect("admin-banner");
        }else if("insert".equals(action)){
            BannerDAO dao = new BannerDAO();

            String title = request.getParameter("title");
            int sortOrder = Integer.parseInt(request.getParameter("sortOrder"));
            boolean active = request.getParameter("active") != null;
            Part imagePart = request.getPart("image");
            String fileName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
            String projectPath = System.getProperty("user.dir");

            String uploadPath = projectPath + "/src/main/webapp/images/upload";
            File uploadDir = new File(uploadPath);

            if(!uploadDir.exists()){
                uploadDir.mkdirs();
            }

            String imageName = System.currentTimeMillis() + fileName;
            imagePart.write(uploadPath + File.separator + imageName);
            System.out.println(uploadPath + File.separator + imageName);


            Banner b = new Banner();

            b.setTitle(title);
            b.setImgName(imageName);
            b.setImagePath("images/upload/" + imageName);
            b.setActive(active);
            b.setSortOrder(sortOrder);

            dao.insertBanner(b);
            response.sendRedirect("admin-banner");
        }

    }



    private void loadBannerPage(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-banner.jsp").forward(request, response);
    }

    private void toggleBanner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        bannerDAO.toggleBanner(id);
        response.sendRedirect("admin-banner");
    }

    private void deleteBanner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        bannerDAO.deleteBanner(id);
        response.sendRedirect("admin-banner");
    }

    private void getActiveBanner(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Banner> banners =bannerDAO.getActiveBanner();
        request.setAttribute("banners", banners);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-banner.jsp").forward(request,response);
    }

    private void editBanner(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int id = Integer.parseInt(request.getParameter("id"));
        BannerDAO dao = new BannerDAO();
        Banner banner = dao.getBannerById(id);
        request.setAttribute("banner", banner);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-banner-edit.jsp").forward(request,response);
    }

    private void addBanner(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-banner-add.jsp").forward(request,response);
    }
}
