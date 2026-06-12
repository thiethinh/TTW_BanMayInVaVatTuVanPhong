package com.papercraft.controller.admin;

import com.papercraft.dao.PaymentDAO;
import com.papercraft.dto.RevenueDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/admin/revenue")
public class RevenueServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(RevenueServlet.class);
    private PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        logger.info("Nhận yêu cầu API thống kê doanh thu. Khoảng thời gian từ: '{}' đến: '{}'", from, to);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try (PrintWriter out = response.getWriter()) {

            // Gọi tầng DAO lấy danh sách dữ liệu doanh thu
            List<RevenueDTO> list = paymentDAO.getRevenue(from, to);

            if (list == null || list.isEmpty()) {
                logger.warn("Không tìm thấy bất kỳ dữ liệu doanh thu nào trong khoảng thời gian đã chọn. Trả về mảng rỗng.");
                out.print("[]");
                out.flush();
                return;
            }

            logger.debug("Truy vấn database thành công. Tìm thấy {} bản ghi doanh thu.", list.size());

            out.print(toJson(list));
            out.flush();
            logger.info("Đã phản hồi dữ liệu cấu trúc JSON thành công về Client ứng dụng.");
        } catch (Exception e) {
            logger.error("Xảy ra lỗi nghiêm trọng trong tiến trình xử lý API doanh thu từ '{}' đến '{}': ", from, to, e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter errorOut = response.getWriter()) {
                errorOut.print("{\"error\":\"Lỗi hệ thống nội bộ, không thể tổng hợp dữ liệu doanh thu vào lúc này.\"}");
                errorOut.flush();
            }
        }
    }

    private String toJson(List<RevenueDTO> list) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            RevenueDTO r = list.get(i);
            json.append("{")
                    .append("\"label\":\"").append(r.getLabel()).append("\",")
                    .append("\"total\":").append(r.getTotal())
                    .append("}");
            if (i < list.size() - 1) json.append(",");
        }
        json.append("]");
        return json.toString();
    }
}
