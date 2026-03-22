package com.papercraft.controller.client;

import com.papercraft.dao.ContactDAO;
import com.papercraft.model.Contact;
import com.papercraft.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/contact")
public class ContactServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/client/contact.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         //cấu hình TV
        request.setCharacterEncoding("UTF-8");

        // Lấy dữ liệu từ Form
        String fullname = request.getParameter("fullname");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        // Tạo đối tượng Contact
        Contact contact = new Contact();
        contact.setUserFullname(fullname);
        contact.setEmail(email);
        contact.setContactTitle(subject);
        contact.setContent(message);

        // Kiểm tra đăng nhập (Lấy user từ session "acc")
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user != null) {
            contact.setUserId(user.getId()); // Nếu đã login thì lưu ID
        } else {
            contact.setUserId(null); // User chưa login
        }

        //  Gọi DAO lưu vào DB
        ContactDAO dao = new ContactDAO();
        boolean isSuccess = dao.insertContact(contact);

        // Thông báo kết quả
        if (isSuccess) {
            request.setAttribute("ms", "Gửi liên hệ thành công! Chúng tôi sẽ phản hồi sớm.");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại!");

            // Giữ lại nội dung cũ để user không cần nhập lại
            request.setAttribute("old_fullname", fullname);
            request.setAttribute("old_email", email);
            request.setAttribute("old_subject", subject);
            request.setAttribute("old_message", message);
        }

        // chuyển hướng về contact.jsp
        request.getRequestDispatcher("/WEB-INF/views/client/contact.jsp").forward(request, response);
    }
}