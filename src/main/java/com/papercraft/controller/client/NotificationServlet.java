package com.papercraft.controller.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.papercraft.dao.NotificationDAO;
import com.papercraft.model.Notification;
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
import java.util.List;

@WebServlet(name = "NotificationServlet", urlPatterns = {"/notification"})
public class NotificationServlet extends HttpServlet {

    private static final Logger logger =  LoggerFactory.getLogger(NotificationServlet.class);
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private String contextPath;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sesion = request.getSession();
        User user = (User) sesion.getAttribute("acc");
        if (user != null) {
            String action = request.getParameter("action");
            logger.debug("Nhận yêu cầu GET /notification từ User ID: '{}', Action: '{}'", user.getId(), action);

            if (action == null || action.equals("list")) {
                getNotifications(request, response);
            } else {
                logger.warn("Yêu cầu GET chứa 'action' không hợp lệ: '{}' từ User ID: '{}'", action, user.getId());
                response.sendError(400);
            }
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");
        String action = request.getParameter("action");

        if (user != null) {
            logger.info("Nhận yêu cầu POST /notification thay đổi trạng thái từ User ID: '{}', Action: '{}'", user.getId(), action);
        }

        if ("seen".equals(action)) {
            markSeen(request, response);
        } else if ("read".equals(action)) {
            markRead(request, response);
        } else {
            logger.warn("Yêu cầu POST chứa 'action' không được hỗ trợ hoặc bị rỗng: '{}'", action);
            response.sendError(400);
        }
    }

    private void getNotifications(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User acc = (User) request.getSession().getAttribute("acc");
        contextPath = request.getContextPath();

        if (acc == null) {
            logger.warn("getNotifications bị chặn: Đối tượng tài khoản trong session bị null.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        logger.debug("Đang truy vấn danh sách thông báo và số lượng chưa xem cho User ID: '{}'", acc.getId());
        List<Notification> notifications = notificationDAO.getAllNotificationByUserId(acc.getId());
        int unseenCount = notificationDAO.countUnseenNotification(acc.getId());

        JsonObject result = new JsonObject();
        result.addProperty("unseenCount", unseenCount);

        JsonArray array = new JsonArray();
        for (Notification n : notifications) {
            JsonObject item = new JsonObject();
            item.addProperty("id", n.getId());
            item.addProperty("content", n.getContent());
            item.addProperty("type", n.getType() != null ? n.getType().name() : "");
            item.addProperty("title", n.getType() != null ? n.getType().getTitle() : "");
            item.addProperty("isRead", n.isRead());
            item.addProperty("isSeen", n.isSeen());
            item.addProperty("relativeTime", n.getRelativeTime());
            item.addProperty("url", buildNotificationUrl(n));

            array.add(item);
        }

        result.add("notifications", array);

        logger.info("Tải danh sách thông báo thành công cho User ID: '{}'. Tổng số: {}, Số lượng chưa nhìn thấy (Unseen): {}",
                acc.getId(), notifications.size(), unseenCount);

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(result.toString());
    }

    private void markSeen(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User acc = (User) request.getSession().getAttribute("acc");

        if (acc == null) {
            logger.warn("markSeen bị chặn: Yêu cầu cập nhật trạng thái 'seen' từ người dùng vô danh.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        logger.debug("Đang cập nhật trạng thái: Đánh dấu tất cả thông báo là ĐÃ XEM (Seen) cho User ID: '{}'", acc.getId());
        notificationDAO.markAllSeen(acc.getId());

        logger.info("Cập nhật trạng thái ĐÃ XEM thành công cho mọi thông báo của User ID: '{}'", acc.getId());
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void markRead(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("id");

        if (idStr == null) {
            logger.warn("markRead thất bại: Thiếu tham số 'id' của thông báo cần cập nhật.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            int notificationId = Integer.parseInt(idStr);
            logger.debug("Đang cập nhật trạng thái: Đánh dấu thông báo ID '{}' là ĐÃ ĐỌC (Read)", notificationId);
            notificationDAO.markRead(notificationId);
            logger.info("Cập nhật trạng thái ĐÃ ĐỌC thành công cho thông báo ID: '{}'", notificationId);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException e) {
            logger.error("Định dạng tham số thông báo 'id' gửi lên không phải là kiểu số nguyên hợp lệ: '{}'", idStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String buildNotificationUrl(Notification notification) {
        if (contextPath == null) return "";
        NotificationType type = notification.getType();
        if (type == null) {
            logger.warn("Phát hiện thông báo ID '{}' không có kiểu phân loại (NotificationType bị null).", notification.getId());
            return contextPath;
        }
        if (type.requiresReferenceId()) {
            Integer refId = notification.getReferenceId();
            if (refId == null) {
                logger.warn("Thông báo loại '{}' (ID: '{}') yêu cầu ReferenceId nhưng trường này đang bị rỗng.", type.name(), notification.getId());
                return contextPath;
            }
            return contextPath+ "/" +String.format(type.getRoutePattern(), notification.getReferenceId());
        }
        return contextPath+"/" + type.getRoutePattern();
    }
}