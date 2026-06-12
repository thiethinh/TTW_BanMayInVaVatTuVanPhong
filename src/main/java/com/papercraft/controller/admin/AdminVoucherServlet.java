package com.papercraft.controller.admin;

import com.papercraft.dao.VoucherDAO;
import com.papercraft.model.Voucher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/admin/admin-voucher")
public class AdminVoucherServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminVoucherServlet.class);

    private final VoucherDAO voucherDAO = new VoucherDAO();
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        logger.debug("Nhận yêu cầu GET vào AdminVoucherServlet. Action: '{}'", action);

        if (action != null) {
            switch (action) {
                case "toggle":
                    toggleVoucher(request, response);
                    return;
                case "delete":
                    deleteVoucher(request, response);
                    return;
                case "edit":
                    editVoucher(request, response);
                    return;
                case "add":
                    logger.debug("Chuyển tiếp luồng (forward) sang biểu mẫu thêm Voucher.");
                    request.getRequestDispatcher(
                            "/WEB-INF/views/admin/admin-voucher-add.jsp"
                    ).forward(request, response);
                    return;
                default:
                    logger.warn("Cảnh báo: Nhận giá trị 'action' không hợp lệ: '{}'. Tự động rơi vào luồng tải trang mặc định.", action);
                    break;
            }
        }

        loadVoucherPage(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        logger.info("Nhận yêu cầu POST xử lý Voucher. Action: '{}'", action);

        if ("insert".equals(action)) {
            Voucher v = buildVoucherFromRequest(request);
            voucherDAO.insertVoucher(v);
            response.sendRedirect("admin-voucher");
        } else if ("update".equals(action)) {
            Voucher v = buildVoucherFromRequest(request);
            v.setId(Integer.parseInt(request.getParameter("id")));
            voucherDAO.updateVoucher(v);
            response.sendRedirect("admin-voucher");
        } else {
            logger.warn("Yêu cầu xử lý POST bị từ chối do 'action' không hợp lệ: '{}'", action);
            response.sendRedirect("admin-voucher");
        }
    }

    private void loadVoucherPage(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";

        logger.info("Tải danh sách cấu hình Voucher với từ khóa tìm kiếm: '{}'", keyword);
        List<Voucher> vouchers = voucherDAO.getAllVouchers(keyword);

        request.setAttribute("vouchers", vouchers);
        request.setAttribute("keyword", keyword);

        logger.debug("Truy vấn thành công {} bản ghi Voucher. Tiến hành Forward sang trang hiển thị.", vouchers.size());
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-voucher.jsp").forward(request, response);
    }

    private void toggleVoucher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        voucherDAO.toggleStatus(id);
        logger.info("Đã thay đổi trạng thái kích hoạt (Toggle Status) của Voucher ID: {}", id);
        response.sendRedirect("admin-voucher");
    }

    private void deleteVoucher(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        voucherDAO.deleteVoucher(id);
        logger.info("Đã thực thi xóa thành công Voucher ID: {}", id);
        response.sendRedirect("admin-voucher");
    }

    private void editVoucher(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Voucher voucher = voucherDAO.getVoucherById(id);

        if (voucher == null) {
            logger.warn("Không tìm thấy thông tin Voucher trong hệ thống ứng với ID: {}", id);
            response.sendRedirect("admin-voucher?msg=not_found");
            return;
        }

        request.setAttribute("voucher", voucher);
        logger.debug("Tải thành công thông tin Voucher ID {}. Chuyển tiếp sang trang chỉnh sửa.", id);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-voucher-edit.jsp").forward(request, response);
    }

    private Voucher buildVoucherFromRequest(HttpServletRequest request) {
        Voucher v = new Voucher();
        v.setCode(request.getParameter("code").toUpperCase().trim());
        v.setName(request.getParameter("name"));
        v.setDescription(request.getParameter("description"));
        v.setDiscountType(request.getParameter("discountType"));
        v.setDiscountValue(new BigDecimal(request.getParameter("discountValue")));

        String maxDiscount = request.getParameter("maxDiscount");
        v.setMaxDiscount(
                (maxDiscount != null && !maxDiscount.isEmpty())
                        ? new BigDecimal(maxDiscount) : null
        );

        String minOrder = request.getParameter("minOrderValue");
        v.setMinOrderValue(
                (minOrder != null && !minOrder.isEmpty())
                        ? new BigDecimal(minOrder) : BigDecimal.ZERO
        );

        v.setQuantity(Integer.parseInt(request.getParameter("quantity")));

        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        v.setStartDate(
                (startDate != null && !startDate.isEmpty())
                        ? Timestamp.valueOf(LocalDateTime.parse(startDate, FORMATTER)) : null
        );
        v.setEndDate(
                (endDate != null && !endDate.isEmpty())
                        ? Timestamp.valueOf(LocalDateTime.parse(endDate, FORMATTER)) : null
        );

        v.setStatus(request.getParameter("status"));
        return v;
    }
}