package com.papercraft.controller.admin;

import com.papercraft.dao.NotificationDAO;
import com.papercraft.dao.OrderDAO;
import com.papercraft.model.Notification;
import com.papercraft.model.Order;
import com.papercraft.model.User;
import com.papercraft.model.enums.NotificationType;
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

@WebServlet(name = "AdminOrderManage", value = "/admin/admin-order-manage")
public class AdminOrderManage extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrderManage.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");
        String action = request.getParameter("action");
        String status = request.getParameter("status");

        action = (action == null) ? "" : action;
        status = (status == null || status.isEmpty()) ? "" : status;
        logger.debug("Nhận yêu cầu vào AdminOrderManage. Action: '{}', Status Filter: '{}'", action, status);

        List<Order> orders = new ArrayList<>();
        OrderDAO orderDAO = new OrderDAO();
        if ("filter-status".equals(action)) {
            logger.info("Thực hiện lọc danh sách đơn hàng theo trạng thái: '{}'", status);
            orders = orderDAO.getOrderByState(status, Integer.MAX_VALUE, 0);
        } else if ("change-status".equals(action)) {
            NotificationDAO notificationDAO = new NotificationDAO();
            String idParam = request.getParameter("id");
            String newStatus = request.getParameter("status-order");
            logger.info("Yêu cầu đổi trạng thái đơn hàng. ID: '{}', Trạng thái mới: '{}'", idParam, newStatus);

            if (idParam != null && newStatus != null) {
                try {
                    int orderId = Integer.parseInt(idParam);
                    Order order = orderDAO.getOrderByID(orderId);

                    if (order == null) {
                        logger.warn("Không tìm thấy đơn hàng với ID: {} trong cơ sở dữ liệu.", orderId);
                    } else {
                        String currentStatus = order.getStatus();

                        if (isValidStatusChange(currentStatus, newStatus)) {
                            orderDAO.updateOrderStatus(orderId, newStatus);
                            logger.info("Admin ID '{}' cập nhật thành công Đơn hàng {} từ [{}] -> [{}]",
                                    (user != null ? user.getId() : "Unknown"), orderId, currentStatus, newStatus);

                            //tao thong bao order
                            NotificationType typeNoti = switch (newStatus) {
                                case "pending" -> NotificationType.ORDER_PENDING;
                                case "shipped" -> NotificationType.ORDER_SHIPPED;
                                case "completed" -> NotificationType.ORDER_COMPLETED;
                                case "canceled" -> NotificationType.ORDER_CANCELLED;
                                default -> null;
                            };

                            if (typeNoti != null && user != null) {
                                Notification noti = new Notification(user.getId(), typeNoti, orderId);
                                notificationDAO.insertNotification(noti);
                                logger.debug("Đã bắn thông báo loại '{}' cho Đơn hàng ID: {}", typeNoti, orderId);
                            }
                        } else {
                            logger.warn("Chuyển đổi trạng thái KHÔNG HỢP LỆ cho Đơn hàng ID {}: Không thể chuyển từ [{}] sang [{}]",
                                    orderId, currentStatus, newStatus);
                        }
                    }
                } catch (NumberFormatException e) {
                    logger.error("Lỗi định dạng ID đơn hàng không phải là số: '{}'", idParam);
                } catch (Exception e) {
                    logger.error("Lỗi hệ thống khi thay đổi trạng thái Đơn hàng ID '{}': ", idParam, e);
                }
            }
            orders = orderDAO.getAllOrders();

        } else if (action.equals("search-order-id")) {
            String orderIdRaw = request.getParameter("order-id");
            request.setAttribute("orderId", orderIdRaw);
            logger.info("Tìm kiếm đơn hàng theo ID: '{}'", orderIdRaw);

            try {
                int orderId = Integer.parseInt(orderIdRaw);
                Order order = orderDAO.getOrderByID(orderId);
                if (order != null) {
                    orders.add(order);
                    logger.debug("Tìm thấy đơn hàng khớp với ID: {}", orderId);
                } else {
                    logger.info("Không tìm thấy đơn hàng nào có ID: {}", orderId);
                }
            } catch (NumberFormatException e) {
                logger.error("Lỗi tìm kiếm: Định dạng mã đơn hàng tìm kiếm '{}' không hợp lệ.", orderIdRaw);
            } catch (Exception e) {
                logger.error("Lỗi hệ thống khi tìm kiếm Đơn hàng ID '{}': ", orderIdRaw, e);
            }
        } else if (action.equals("search-date")) {
            String yearRaw = request.getParameter("year");
            String monthRaw = request.getParameter("month");
            String dayRaw = request.getParameter("day");

            request.setAttribute("dateSearch", yearRaw + "-" + monthRaw + "-" + dayRaw);
            logger.info("Tìm kiếm đơn hàng theo ngày cụ thể: {}/{}/{}", dayRaw, monthRaw, yearRaw);

            try {
                int year = Integer.parseInt(yearRaw);
                int month = Integer.parseInt(monthRaw);
                int day = Integer.parseInt(dayRaw);
                orders = orderDAO.searchOrderByDate(year, month, day);
                logger.debug("Kết quả tìm kiếm theo ngày: tìm thấy {} đơn hàng.", (orders != null ? orders.size() : 0));
            } catch (NumberFormatException e) {
                logger.error("Định dạng tham số ngày/tháng/năm chứa ký tự không hợp lệ: [Day: '{}', Month: '{}', Year: '{}']",
                        dayRaw, monthRaw, yearRaw);
            } catch (Exception e) {
                logger.error("Lỗi hệ thống khi truy vấn đơn hàng theo ngày: ", e);
            }

        } else if (action.equals("search-month")) {
            String yearRaw = request.getParameter("year");
            String monthRaw = request.getParameter("month");
            logger.info("Tìm kiếm đơn hàng theo tháng: {}/{}", monthRaw, yearRaw);

            request.setAttribute("monthSearch", yearRaw + "-" + monthRaw);
            try {
                int year = Integer.parseInt(yearRaw);
                int month = Integer.parseInt(monthRaw);
                orders = orderDAO.searchOrderByMonth(year, month);
                logger.debug("Kết quả tìm kiếm theo tháng: tìm thấy {} đơn hàng.", (orders != null ? orders.size() : 0));
            } catch (NumberFormatException e) {
                logger.error("Định dạng tham số tháng/năm chứa ký tự không hợp lệ: [Month: '{}', Year: '{}']", monthRaw, yearRaw);
            } catch (Exception e) {
                logger.error("Lỗi hệ thống khi truy vấn đơn hàng theo tháng: ", e);
            }
        } else {
            logger.info("Tải mặc định toàn bộ danh sách đơn hàng từ CSDL.");
            orders = orderDAO.getAllOrders();
            logger.debug("Tải thành công {} bản ghi đơn hàng.", (orders != null ? orders.size() : 0));
        }

        request.setAttribute("orders", orders);
        request.setAttribute("status", status);
        // request.setAttribute("currentPage", currentPage);
        // request.setAttribute("totalPages", totalPages);

        logger.debug("Chuyển tiếp luồng hiển thị (forward) sang admin-order-manage.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-order-manage.jsp").forward(request, response);
    }

    private boolean isValidStatusChange(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }

        currentStatus = currentStatus.trim().toLowerCase();
        newStatus = newStatus.trim().toLowerCase();

        switch (currentStatus) {
            case "pending":
                return newStatus.equals("shipped") || newStatus.equals("canceled");

            case "shipped":
                return newStatus.equals("completed") || newStatus.equals("canceled");

            case "completed":
            case "canceled":
                return false;

            default:
                return false;
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws
            ServletException, IOException {
        // Code xử lý yêu cầu POST
    }
}