package com.papercraft.controller.client;

import com.papercraft.dao.BlogDao;
import com.papercraft.model.Blog;
import com.papercraft.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "BlogPostServlet", value = "/blog-post")
public class BlogPostServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(BlogPostServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            logger.warn("Yêu cầu xem chi tiết bài viết bị từ chối: Tham số 'id' trống.");
            response.sendRedirect("blog");
            return;
        }
        int id = Integer.parseInt(idParam);
        logger.info("Khách truy cập đang tải nội dung chi tiết bài viết ID: {}", id);

        BlogDao blogDao = new BlogDao();
        Blog blog = blogDao.getBlogById(id);

        if (blog == null) {
            logger.warn("Bài viết ID '{}' không tồn tại trong hệ thống cơ sở dữ liệu.", id);
            response.sendRedirect("blog");
            return;
        }

        if (!blog.getStatus()) {
            HttpSession session = request.getSession();
            User user = (User) session.getAttribute("acc");

            if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
                logger.warn("Cảnh báo bảo mật: Người dùng thông thường (hoặc khách vãng lai) cố tình truy cập bài viết chưa phê duyệt ID: {}", id);
                response.sendRedirect("home");
                return;
            }
            logger.info("Admin ID '{}' đang xem trước (Preview) bài viết chưa phê duyệt ID: {}", user.getId(), id);
        }

        List<Blog> relatedBlogs = blogDao.getRelatedBlogs(blog.getTypeBlog(), id);
        request.setAttribute("relatedBlogs", relatedBlogs);

        List<Blog> latestBlogs = blogDao.getLatestBlogs(id);
        request.setAttribute("latestBlogs", latestBlogs);

        request.setAttribute("blog", blog);
        request.getRequestDispatcher("/WEB-INF/views/client/blog-post.jsp").forward(request, response);
    }
}
