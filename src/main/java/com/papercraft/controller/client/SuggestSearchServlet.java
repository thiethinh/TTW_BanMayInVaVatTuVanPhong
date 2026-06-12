package com.papercraft.controller.client;

import com.google.gson.Gson;
import com.papercraft.dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "SuggestSearchServlet", urlPatterns = {"/suggest"})
public class SuggestSearchServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SuggestSearchServlet.class);

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        List<String> suggests = new ArrayList<>();

        logger.debug("Nhận yêu cầu gọi ý tìm kiếm (Auto-complete). Từ khóa: '{}', Phân loại nhóm: '{}'", keyword, type);

        ProductDAO productDAO = new ProductDAO();

        if (keyword != null && !keyword.isEmpty()) {
            logger.debug("Đang truy vấn top 5 sản phẩm khớp với từ khóa '{}' từ CSDL...", keyword);
            suggests = productDAO.findTop5NameProductMatchest(keyword, type);
        }

        if (suggests != null) {
            if (logger.isTraceEnabled()) {
                for (String suggest : suggests) {
                    logger.trace("Gợi ý tìm kiếm tìm thấy: '{}'", suggest);
                }
            }
        } else {
            logger.warn("Kết quả trả về từ CSDL bị null đối với từ khóa: '{}'", keyword);
            response.setContentType("application/json");
            response.getWriter().print("[]");
            return;
        }

        logger.info("Hoàn tất xử lý gợi ý cho từ khóa '{}'. Số lượng kết quả tìm thấy: {}", keyword, suggests.size());

        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        Gson gson = new Gson();
        writer.print(gson.toJson(suggests));
        writer.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Nhận yêu cầu POST tại /suggest (Không xử lý logic nghiệp vụ).");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}