package com.papercraft.controller.admin;

import com.papercraft.dao.BlogDao;
import com.papercraft.model.Blog;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminBlogServlet", value = "/admin-blog")
public class AdminBlogServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        BlogDao blogDao = new BlogDao();

        String action = request.getParameter("action");
        String viewType = request.getParameter("view");
        String keyword = request.getParameter("keyword");
        String typeFilter = request.getParameter("type");

        if (viewType == null) viewType = "pending";

        int status = "approved".equals(viewType) ? 1 : 0;

        if (action != null) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean success = false;

                if ("approve".equals(action)) {
                    success = blogDao.actionBlog(id, 1);
                    session.setAttribute("msg", success ? "Duyệt thành công!" : "Lỗi duyệt bài.");
                } else if ("hidden".equals(action)) {
                    success = blogDao.actionBlog(id, 0);
                    session.setAttribute("msg", success ? "Ẩn thành công!" : "Lỗi ẩn bài.");
                } else if ("delete".equals(action)) {
                    success = blogDao.deleteBlog(id);
                    session.setAttribute("msg", success ? "Xóa thành công!" : "Lỗi xóa bài.");
                }

                String redirectUrl = "admin-blog?view=" + viewType;
                if (keyword != null && !keyword.isEmpty()) redirectUrl += "&keyword=" + keyword;
                if (typeFilter != null && !typeFilter.isEmpty()) redirectUrl += "&type=" + typeFilter;

                response.sendRedirect(redirectUrl);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<Blog> listBlog = blogDao.searchAdminBlogs(keyword, typeFilter, status);

        request.setAttribute("listBlog", listBlog);
        request.setAttribute("currentView", viewType);
        request.setAttribute("searchKeyword", keyword);
        request.setAttribute("searchType", typeFilter);

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-blog.jsp").forward(request, response);
    }
}
