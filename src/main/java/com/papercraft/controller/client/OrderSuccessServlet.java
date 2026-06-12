package com.papercraft.controller.client;

import com.papercraft.dao.ProductDAO;
import com.papercraft.dao.UserVoucherDAO;
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
import java.util.List;

@WebServlet("/order-success")
public class OrderSuccessServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(OrderSuccessServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Chặn user truy cập trực tiếp /order-success
        Boolean orderSuccess = (Boolean) session.getAttribute("orderSuccess");
        Integer lastOrderId = (Integer) session.getAttribute("lastOrderId");
        Integer voucherId = (Integer) session.getAttribute("voucherId");
        User user = (User) session.getAttribute("acc");

        // Nếu user vào trực tiếp /order-success mà không qua checkout thì chuyển về /home
        if (orderSuccess == null || !orderSuccess || lastOrderId == null || lastOrderId <= 0) {
            logger.warn("Cảnh báo: Phát hiện lượt truy cập trực tiếp/trái phép vào URL /order-success mà không qua luồng thanh toán.");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        logger.info("Xử lý hoàn tất đơn hàng thành công. Đơn hàng ID: '{}', Khách hàng: '{}'",
                lastOrderId, (user != null ? user.getEmail() : "Khách vãng lai"));

        ProductDAO productDAO = new ProductDAO();
        logger.debug("Đang truy vấn danh sách sản phẩm gợi ý cho trang hoàn tất đơn hàng...");
        List<Product> suggestedProducts = productDAO.getSuggestedProductsSimple(8);

        if (suggestedProducts == null) {
            logger.debug("Danh sách sản phẩm gợi ý trả về bị null, khởi tạo danh sách trống.");
            suggestedProducts = new ArrayList<>();
        }

        if (voucherId != null && voucherId != 0) {
            logger.info("Phát hiện mã giảm giá sử dụng trong đơn hàng. Tiến hành cập nhật trạng thái ĐÃ DÙNG cho Voucher ID '{}' của User ID '{}'", voucherId, user.getId());
            UserVoucherDAO userVoucherDAO = new UserVoucherDAO();
            userVoucherDAO.setUsedVoucher(user.getId(), voucherId);
        } else {
            logger.warn("Lỗi logic: Tìm thấy Voucher ID '{}' nhưng đối tượng người dùng (User) trong session bị null.", voucherId);
        }

        // Gửi data sang JSP
        request.setAttribute("orderId", lastOrderId);
        request.setAttribute("suggestedProducts", suggestedProducts);

        // Xóa session để k vào lại trang success trực tiếp nhiều lần
        logger.debug("Đang dọn dẹp các thuộc tính kiểm tra đặt hàng ('orderSuccess', 'lastOrderId') trong session để tránh F5/truy cập lại.");
        session.removeAttribute("orderSuccess");
        session.removeAttribute("lastOrderId");

        logger.info("Chuyển tiếp luồng (Forward) dữ liệu thành công sang giao diện hiển thị order-success.jsp");
        request.getRequestDispatcher("/WEB-INF/views/client/order-success.jsp")
                .forward(request, response);
    }
}