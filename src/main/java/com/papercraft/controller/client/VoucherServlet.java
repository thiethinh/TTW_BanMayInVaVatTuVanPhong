package com.papercraft.controller.client;

import com.papercraft.dao.UserVoucherDAO;
import com.papercraft.dao.VoucherDAO;
import com.papercraft.model.User;
import com.papercraft.model.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "VoucherServlet", urlPatterns = {"/voucher"})
public class VoucherServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(VoucherServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserVoucherDAO dao = new UserVoucherDAO();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");
        List<Voucher> vouchers;

        if (user != null) {
            String voucherCode = request.getParameter("voucherCode");
            logger.info("Nhận yêu cầu truy cập trang voucher từ User ID: '{}'. Mã voucher gửi kèm (nếu có): '{}'", user.getId(), voucherCode);

            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                String cleanCode = voucherCode.trim();
                logger.debug("Tiến hành kiểm tra và áp dụng mã voucher: '{}' cho User ID: '{}'", cleanCode, user.getId());

                Voucher voucher = new VoucherDAO().getVoucherByCode(cleanCode);

                if (voucher == null) {
                    logger.warn("Áp dụng thất bại: Mã voucher '{}' không tồn tại trong hệ thống.", cleanCode);
                    request.setAttribute("saveVoucherError", "Mã voucher không tồn tại");
                } else if (!voucher.isAvailable()) {
                    logger.warn("Áp dụng thất bại: Mã voucher '{}' (ID: '{}') đã hết hạn, hết số lượng hoặc đang bị vô hiệu hóa.", cleanCode, voucher.getId());
                    request.setAttribute("saveVoucherError", "Voucher hiện không khả dụng");
                } else {
                    logger.debug("Voucher hợp lệ. Tiến hành liên kết Voucher ID '{}' vào kho ví của User ID '{}'...", voucher.getId(), user.getId());
                    boolean success = dao.addUserVoucher(user.getId(), voucher.getId());

                    if (!success) {
                        logger.warn("Áp dụng thất bại: User ID '{}' đã từng lưu hoặc đã sử dụng mã voucher ID '{}' này trước đó.", user.getId(), voucher.getId());
                        request.setAttribute("saveVoucherError", "Bạn đã lưu voucher này rồi hoặc đã sử dụng");
                    } else {
                        logger.info("Áp dụng thành công! Mã voucher '{}' (ID: '{}') đã được lưu vào ví của User ID '{}'", cleanCode, voucher.getId(), user.getId());
                        request.setAttribute("saveVoucherSuccess", "Áp dụng voucher thành công");
                        request.setAttribute("selectedVoucher", voucher);
                    }
                }
            }

            logger.debug("Đang tải danh sách toàn bộ ví voucher sở hữu bởi User ID: '{}'...", user.getId());
            vouchers = dao.getVouchersByUserId(user.getId());
            if (vouchers == null) {
                vouchers = new ArrayList<>();
            }
            logger.info("Tải danh sách ví voucher thành công cho User ID '{}'. Số lượng voucher hiện có: {}", user.getId(), vouchers.size());
            request.setAttribute("vouchers", vouchers);

        } else {
            logger.warn("Yêu cầu truy cập /voucher từ người dùng vô danh (Chưa đăng nhập). Hệ thống vẫn chuyển hướng hiển thị jsp nhưng không nạp dữ liệu.");
        }

        logger.debug("Chuyển tiếp luồng (Forward) dữ liệu sang giao diện hiển thị voucher.jsp");
        request.getRequestDispatcher("/WEB-INF/views/client/voucher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logger.debug("Nhận yêu cầu POST tại /voucher (Không xử lý logic nghiệp vụ).");
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }
}