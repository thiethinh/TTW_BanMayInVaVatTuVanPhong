package com.papercraft.controller.admin;

import com.google.gson.Gson;
import com.papercraft.dao.ContactDAO;
import com.papercraft.dao.NotificationDAO;
import com.papercraft.dto.ContactDTO;
import com.papercraft.model.Contact;
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
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

@WebServlet(name = "AdminContact", value = "/admin/admin-contacts")
public class AdminContact extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(AdminContact.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        ContactDAO contactDAO = new ContactDAO();
        String action = request.getParameter("action");
        logger.debug("Nhận yêu cầu GET vào AdminContact với action: '{}'", action);

        if ("get-by-month".equals(action)) {

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            try {
                int month = Integer.parseInt(request.getParameter("month"));
                int year = Integer.parseInt(request.getParameter("year"));

                logger.info("Yêu cầu AJAX: Tải danh sách liên hệ theo tháng {}/{}", month, year);
                List<ContactDTO> list = new ContactDAO().getContactsByMonth(month, year);

                PrintWriter out = response.getWriter();
                out.print(new Gson().toJson(list));
                out.flush();
                logger.debug("Trả về dữ liệu JSON thành công cho hành động 'get-by-month'");
            } catch (Exception e) {
                logger.error("Lỗi hệ thống nghiêm trọng khi thực hiện lấy dữ liệu liên hệ theo tháng: ", e);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().print("{\"error\":\"server error\"}");
            }
            return;
        }


        String keyword = request.getParameter("keyword");
        if (keyword == null) keyword = "";

        String reply = request.getParameter("reply");
        int replied = -1;

        if (reply != null && !reply.isEmpty()) {
            try {
                replied = Integer.parseInt(reply);
            } catch (NumberFormatException e) {
                logger.warn("Lỗi định dạng bộ lọc 'reply' nhận được từ URL (reply='{}'). Tự động thiết lập hiển thị tất cả (-1).", reply);
            }
        }

        if ("toggle".equals(action)) {
            String idRaw = request.getParameter("id");
            try {
                int id = Integer.parseInt(idRaw);
                boolean currentStatus = Boolean.parseBoolean(request.getParameter("status"));
                boolean newStatus = !currentStatus;

                logger.info("Thực hiện đổi trạng thái liên hệ ID '{}' từ {} sang {}", id, currentStatus, newStatus);
                contactDAO.updateStatus(id, !currentStatus);

                String redirectUrl = "admin-contacts?keyword=" + URLEncoder.encode(keyword, "UTF-8");
                if (replied != -1) {
                    redirectUrl += "&replied=" + replied;
                }

                if (!currentStatus) {
                    HttpSession session = request.getSession();
                    User user = (User) session.getAttribute("acc");

                    if (user != null) {
                        NotificationDAO notificationDAO = new NotificationDAO();
                        Notification noti = new Notification(user.getId(), NotificationType.CONTACT_REPLIED, id);
                        notificationDAO.insertNotification(noti);
                        logger.info("Admin ID '{}' đã xử lý liên hệ ID '{}'. Đã bắn thông báo CONTACT_REPLIED.", user.getId(), id);
                    } else {
                        logger.warn("Không tìm thấy thông tin Admin trong session. Bỏ qua bước tạo thông báo hệ thống.");
                    }
                }

                logger.debug("Redirect về danh sách liên hệ: {}", redirectUrl);
                response.sendRedirect(redirectUrl);
                return;
            } catch (Exception e) {
                logger.error("Lỗi nghiêm trọng xảy ra khi thực hiện đổi trạng thái liên hệ ID '{}': ", idRaw, e);
            }
        }

        if ("delete".equals(action)) {
            String idRaw = request.getParameter("id");
            try {
                int id = Integer.parseInt(idRaw);
                logger.info("Yêu cầu xóa liên hệ ID: {}", id);
                boolean deleted = contactDAO.deleteContactById(id);
                logger.info("Kết quả xóa liên hệ ID '{}' từ DB: {}", id, deleted);

                response.sendRedirect("admin-contacts?deleted=" + deleted);
                return;

            } catch (Exception e) {
                logger.error("Lỗi hệ thống khi thực hiện xóa liên hệ ID '{}': ", idRaw, e);
                response.sendRedirect("admin-contacts?deleted=false");
                return;
            }
        }

        logger.info("Đang tải danh sách liên hệ - Bộ lọc [Từ khóa: '{}', Trạng thái phản hồi (replied): {}]", keyword, replied);
        List<Contact> contacts = contactDAO.getContact(keyword, replied);
        logger.debug("Tìm thấy tổng cộng {} bản ghi liên hệ.", (contacts != null ? contacts.size() : 0));

        request.setAttribute("contacts", contacts);
        request.setAttribute("keyword", keyword);
        request.setAttribute("currentReplied", replied);

        logger.debug("Forward dữ liệu sang giao diện admin-contacts.jsp");
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-contacts.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Code xử lý yêu cầu POST
    }
}