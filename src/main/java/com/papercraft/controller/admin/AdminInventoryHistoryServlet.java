package com.papercraft.controller.admin;

import com.google.gson.Gson;
import com.papercraft.dao.InventoryDAO;
import com.papercraft.dto.InventoryDetailDTO;
import com.papercraft.model.InventoryTransaction;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminInventoryHistoryServlet", value = "/admin/inventory-history")
public class AdminInventoryHistoryServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminInventoryHistoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String transactionIdParam = request.getParameter("transactionId");
        logger.debug("Nhận yêu cầu vào AdminInventoryHistoryServlet. Tham số transactionId raw: '{}'", transactionIdParam);

        if (transactionIdParam != null && !transactionIdParam.isEmpty()) {
            try {
                int transactionId = Integer.parseInt(transactionIdParam);
                logger.info("Yêu cầu AJAX: Lấy chi tiết lịch sử kho cho Transaction ID: {}", transactionId);

                InventoryDAO inventoryDAO = new InventoryDAO();
                List<InventoryDetailDTO> detailList = inventoryDAO.getTransactionDetails(transactionId);
                logger.debug("Truy xuất thành công từ DB. Số lượng bản ghi chi tiết tìm thấy: {}",
                        (detailList != null ? detailList.size() : 0));

                Gson gson = new Gson();
                String jsonResponse = gson.toJson(detailList);

                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse);
            } catch (NumberFormatException e) {
                logger.error("Lỗi định dạng tham số transactionId không phải là số hợp lệ: '{}'", transactionIdParam);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\": \"ID không hợp lệ\"}");
            } catch (Exception e) {
                logger.error("Lỗi hệ thống nghiêm trọng khi lấy chi tiết phiếu kho ID '{}': ", transactionIdParam, e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Lỗi hệ thống xảy ra trên server\"}");
            }
            return;
        }

        String type = request.getParameter("type");
        String search = request.getParameter("search");
        String fromDate = request.getParameter("fromDate");
        String toDate = request.getParameter("toDate");
        if (type == null) type = "all";
        logger.info("Tải danh sách lịch sử kho - Bộ lọc: [Loại phiếu: '{}', Từ khóa: '{}', Từ ngày: '{}', Đến ngày: '{}']",
                type, search, fromDate, toDate);

        InventoryDAO inventoryDAO = new InventoryDAO();
        List<InventoryTransaction> transactions = inventoryDAO.getAllTransactions(type, search, fromDate, toDate);
        logger.debug("Tìm thấy tổng số {} phiếu giao dịch kho thỏa mãn bộ lọc.",
                (transactions != null ? transactions.size() : 0));

        request.setAttribute("transactions", transactions);
        request.setAttribute("type", type);
        request.setAttribute("param", request.getParameterMap());

        logger.debug("Chuyển tiếp dữ liệu (forward) sang giao diện admin-inventory-history.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-inventory-history.jsp").forward(request, response);
    }
}
