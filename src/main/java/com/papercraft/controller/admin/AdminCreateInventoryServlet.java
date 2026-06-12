package com.papercraft.controller.admin;

import com.papercraft.dao.InventoryDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.InventoryTransaction;
import com.papercraft.model.InventoryTransactionDetail;
import com.papercraft.model.Product;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "AdminCreateInventoryServlet", value = "/admin/create-inventory")
public class AdminCreateInventoryServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminCreateInventoryServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Nhận yêu cầu GET: Hiển thị giao diện tạo phiếu kho.");

        ProductDAO productDAO = new ProductDAO();
        List<Product> productList = productDAO.getAllProduct();
        logger.debug("Tải thành công danh sách sản phẩm nền. Số lượng: {}", (productList != null ? productList.size() : 0));

        request.setAttribute("productList", productList);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-create-inventory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("acc");
        if (user == null) {
            logger.warn("Yêu cầu POST bị từ chối: Người dùng chưa đăng nhập hệ thống.");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String transactionType = request.getParameter("transactionType");
        String note = request.getParameter("note");
        String totalValueStr = request.getParameter("totalValue");
        logger.info("Admin ID '{}' gửi yêu cầu tạo phiếu kho [Loại: '{}', Tổng giá trị raw: '{}']",
                user.getId(), transactionType, totalValueStr);

        double totalValue = (totalValueStr != null && !totalValueStr.isEmpty()) ? Double.parseDouble(totalValueStr) : 0;

        String[] productIds = request.getParameterValues("productId[]");
        String[] quantities = request.getParameterValues("quantity[]");
        String[] prices = request.getParameterValues("price[]");
        logger.debug("Mảng tham số nhận được: productIds={}, quantities={}, prices={}",
                Arrays.toString(productIds), Arrays.toString(quantities), Arrays.toString(prices));

        try {
            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setTransactionType(transactionType);
            transaction.setUserId(user.getId());
            transaction.setNote(note);
            transaction.setTotalValue(totalValue);

            List<InventoryTransactionDetail> details = new ArrayList<>();
            if (productIds != null && productIds.length > 0) {
                logger.debug("Bắt đầu phân tích cú pháp chuỗi danh sách sản phẩm gồm {} dòng phần tử.", productIds.length);
                for (int i = 0; i < productIds.length; i++) {
                    if (productIds[i] != null && !productIds[i].trim().isEmpty()) {
                        InventoryTransactionDetail detail = new InventoryTransactionDetail();
                        detail.setProductId(Integer.parseInt(productIds[i]));
                        detail.setQuantity(Integer.parseInt(quantities[i]));
                        detail.setPrice(Double.parseDouble(prices[i]));
                        details.add(detail);
                    }
                }
            }

            if (details.isEmpty()) {
                logger.warn("Lập phiếu thất bại: Danh sách chi tiết phiếu kho trống rỗng.");
                throw new Exception("Bạn chưa chọn sản phẩm nào hợp lệ!");
            }

            transaction.setDetails(details);
            logger.debug("Phân tích danh sách sản phẩm hoàn tất. Đang tiến hành lưu phiếu kho vào CSDL qua DAO...");

            InventoryDAO inventoryDAO = new InventoryDAO();
            boolean isSuccess = inventoryDAO.insertTransaction(transaction);

            if (isSuccess) {
                logger.info("Tạo phiếu kho thành công! Loại: {}, ID Người tạo: {}, Tổng số mặt hàng: {}",
                        transactionType, user.getId(), details.size());

                clearDraftSession(session);
                session.setAttribute("success", "Tạo phiếu " + (transactionType.equals("IMPORT") ? "nhập" : "xuất") + " kho thành công!");
                response.sendRedirect(request.getContextPath() + "/admin/inventory-history");
            } else {
                throw new Exception("Có lỗi xảy ra khi lưu vào cơ sở dữ liệu!");
            }
        } catch (Exception e) {
            logger.error("Thất bại khi xử lý lập phiếu kho. Tiến hành sao lưu dữ liệu tạm (Draft Session) cho Admin ID '{}'. Lý do: ", user.getId(), e);
            session.setAttribute("error", "Lỗi: " + e.getMessage());

            session.setAttribute("draftType", transactionType);
            session.setAttribute("draftNote", note);
            session.setAttribute("draftTotalValue", totalValue);
            session.setAttribute("draftProductIds", productIds);
            session.setAttribute("draftQuantities", quantities);
            session.setAttribute("draftPrices", prices);

            response.sendRedirect(request.getContextPath() + "/admin/create-inventory");
        }
    }

    private void clearDraftSession(HttpSession session) {
        logger.debug("Dọn dẹp sạch sẽ các bản ghi nháp (Draft Session) phiếu kho.");
        session.removeAttribute("draftType");
        session.removeAttribute("draftNote");
        session.removeAttribute("draftTotalValue");
        session.removeAttribute("draftProductIds");
        session.removeAttribute("draftQuantities");
        session.removeAttribute("draftPrices");
    }
}