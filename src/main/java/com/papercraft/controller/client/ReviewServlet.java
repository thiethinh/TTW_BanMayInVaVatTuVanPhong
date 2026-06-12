package com.papercraft.controller.client;

import com.papercraft.dao.ReviewDAO;
import com.papercraft.model.Review;
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
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/add-review")
public class ReviewServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ReviewServlet.class);

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user == null) {
            logger.warn("Yêu cầu gửi đánh giá bị từ chối: Người dùng chưa đăng nhập.");
            out.print("{\"status\":\"error\", \"message\": \"login_required\"}");
            out.flush();
            return;
        }

        try {
            int productId = Integer.parseInt(request.getParameter("productId"));
            int rating = Integer.parseInt(request.getParameter("rating"));
            String comment = request.getParameter("comment");

            logger.info("Nhận yêu cầu thêm đánh giá từ User ID '{}' cho Sản phẩm ID '{}' (Số sao: {})",
                    user.getId(), productId, rating);

            if (rating < 1 || rating > 5) {
                logger.warn("Dữ liệu đánh giá không hợp lệ từ User ID '{}': Số sao {} nằm ngoài phạm vi 1-5.", user.getId(), rating);
                out.print("{\"status\": \"error\", \"message\": \"Vui lòng chọn số sao!\"}");
                return;
            }

            Review review = new Review();
            review.setUserId(user.getId());
            review.setProductId(productId);
            review.setRating(rating);
            review.setComment(comment);

            ReviewDAO dao = new ReviewDAO();
            logger.debug("Đang tiến hành ghi nhận đánh giá mới vào cơ sở dữ liệu...");
            dao.addReview(review);

            String dateStr = new SimpleDateFormat("dd 'tháng' MM 'năm' yyyy, HH:mm").format(new Date());

            String authorName = user.getFullname();
            if (authorName == null || authorName.trim().isEmpty()) {
                String lastName = (user.getLname() != null) ? user.getLname() : "";
                String firstName = (user.getFname() != null) ? user.getFname() : "";
                authorName = (lastName + " " + firstName).trim();
            }

            // Xử lý chuỗi an toàn khi ghép JSON thủ công
            String safeComment = comment.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "");

            String jsonResponse = String.format(
                    "{\"status\": \"success\", \"authorName\": \"%s\", \"rating\": %d, \"comment\": \"%s\", \"date\": \"%s\"}",
                    authorName,
                    rating,
                    safeComment,
                    dateStr
            );

            logger.info("User ID '{}' đã đánh giá thành công sản phẩm ID '{}'. Phản hồi kết quả dữ liệu JSON về phía Client.", user.getId(), productId);
            out.print(jsonResponse);
        } catch (NumberFormatException e) {
            logger.error("Lỗi dữ liệu đầu vào: Tham số productId hoặc rating gửi lên bị sai định dạng số.", e);
            out.print("{\"status\": \"error\", \"message\": \"Dữ liệu không hợp lệ\"}");
        } catch (Exception e) {
            logger.error("Lỗi hệ thống nghiêm trọng khi xử lý thêm đánh giá mới: ", e);
            out.print("{\"status\": \"error\", \"message\": \"Lỗi hệ thống\"}");
        }
        out.flush();
    }
}