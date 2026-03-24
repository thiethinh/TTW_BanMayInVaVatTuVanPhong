package com.papercraft.controller.admin;

import com.papercraft.dao.ReviewDAO;
import com.papercraft.model.Review;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "AdminReview", value = "/admin-review")
public class AdminReview extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ReviewDAO reviewDAO = new ReviewDAO();

        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if ("delete".equals(action) && idParam != null) {
            try {
                int idReview = Integer.parseInt(idParam);
                boolean isDeleted = reviewDAO.deleteReviewByID(idReview);

                if (isDeleted) {
                    request.setAttribute("success", "Xóa thành công");
                } else {
                    request.setAttribute("error", "Lỗi: Xóa thất bại");
                }

                String redirectUrl = "admin-review";
                if (!keyword.isEmpty()) {
                    redirectUrl += "?keyword=" + URLEncoder.encode(keyword, "UTF-8");
                }
                response.sendRedirect(redirectUrl);
                return;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        List<Review> reviews = reviewDAO.getReviews(keyword);
        request.setAttribute("reviews", reviews);
        request.setAttribute("currentKeyword", keyword);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-review.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Code xử lý yêu cầu POST
    }
}