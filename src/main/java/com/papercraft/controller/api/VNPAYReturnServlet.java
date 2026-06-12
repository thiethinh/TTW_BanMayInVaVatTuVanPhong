package com.papercraft.controller.api;

import com.papercraft.config.VNPAYConfig;
import com.papercraft.dao.PaymentDAO;
import com.papercraft.service.OrderService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@WebServlet(value = "/vnpay-return")
public class VNPAYReturnServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(VNPAYReturnServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        String vnp_TransactionStatus = request.getParameter("vnp_TransactionStatus");
        String orderIdStr = request.getParameter("vnp_TxnRef");
        String transactionNo = request.getParameter("vnp_TransactionNo");

        logger.info("Nhận phản hồi giao dịch từ VNPAY (Return URL). TxnRef (OrderId): '{}', Status: '{}', TransNo: '{}'",
                orderIdStr, vnp_TransactionStatus, transactionNo);

        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        String signValue = hashAllFields(fields);
        if (signValue.equals(vnp_SecureHash)) {
            logger.debug("Xác thực chữ ký số VNPAY thành công. Dữ liệu toàn vẹn.");
            if ("00".equals(request.getParameter("vnp_TransactionStatus"))) {
                int orderId = 0;
                try {
                    orderId = Integer.parseInt(orderIdStr);

                    PaymentDAO paymentDAO = new PaymentDAO();
                    paymentDAO.verifyPaymentSuccess(orderId, transactionNo);
                    logger.info("Giao dịch VNPAY thành công hoàn toàn. Đơn hàng ID {} đã được cập nhật.", orderId);
                    response.sendRedirect(request.getContextPath() + "/order-success");
                } catch (Exception e) {
                    logger.error("Lỗi hệ thống: Khách đã bị trừ tiền tại VNPAY nhưng CSDL không thể cập nhật Đơn hàng ID '{}': ", orderId, e);
                    request.getSession().setAttribute("error", "Thanh toán thành công tại VNPAY nhưng hệ thống gặp sự cố cập nhật. Vui lòng liên hệ Admin kèm mã VNPAY: " + request.getParameter("vnp_TransactionNo"));
                    response.sendRedirect(request.getContextPath() + "/cart");
                }
            } else {
                logger.warn("Giao dịch VNPAY thất bại hoặc bị hủy bỏ. Trạng thái lỗi (vnp_TransactionStatus): {}", vnp_TransactionStatus);
                if (orderIdStr != null && !orderIdStr.isEmpty()) {
                    try {
                        int orderId = Integer.parseInt(orderIdStr);
                        OrderService orderService = new OrderService();
                        orderService.cancelOrderAndReleaseStock(orderId);
                        logger.info("Đã tự động hủy đơn và hoàn trả số lượng tồn kho cho Đơn hàng ID: {}", orderId);
                    } catch (NumberFormatException e) {
                        logger.error("Không thể xử lý hoàn kho do mã đơn hàng 'vnp_TxnRef' sai định dạng số: '{}'", orderIdStr);
                    } catch (Exception e) {
                        logger.error("Lỗi phát sinh khi thực hiện hủy đơn hàng tự động ID '{}': ", orderIdStr, e);
                    }
                }
                request.getSession().setAttribute("error", "Giao dịch VNPAY đã bị hủy hoặc không thành công");
                response.sendRedirect(request.getContextPath() + "/cart");
            }
        } else {
            logger.error("CẢNH BÁO AN NINH NGUY HIỂM: Sai lệch chữ ký số VNPAY (Signature Mismatch)! " +
                    "Nhận được: '{}', Tự tính toán: '{}'. Yêu cầu có thể đã bị can thiệp giả mạo dữ liệu.", vnp_SecureHash, signValue);
            request.getSession().setAttribute("error", "Lỗi bảo mật: Sai chữ ký xác thực từ VNPAY");
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private String hashAllFields(Map<String, String> fields) {
        Map<String, String> sortedFields = new TreeMap<>(fields);
        StringBuilder hashData = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedFields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                hashData.append("&");
            }
        }

        if (!hashData.isEmpty()) {
            hashData.setLength(hashData.length() - 1);
        }

        return VNPAYConfig.hmacSHA512(VNPAYConfig.vnp_HashSecret, hashData.toString());
    }
}
