package com.papercraft.controller.client;

import com.papercraft.dao.AddressDAO;
import com.papercraft.dao.UserDAO;
import com.papercraft.model.Address;
import com.papercraft.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "UpdateProfileServlet", value = "/account")
public class AccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }


        AddressDAO addressDAO = new AddressDAO();
        Address address = addressDAO.findDefaultAddress(user.getId());

        String lnameAdrr = request.getParameter("lname");
        String fnameAdrr = request.getParameter("fname");
        String phoneAdrr = request.getParameter("address-phone");
        String nation = request.getParameter("nation");
        String city = request.getParameter("city");
        String poseCode = request.getParameter("post-code");
        String detailAddress = request.getParameter("address");

        if (lnameAdrr != null && fnameAdrr != null && phoneAdrr != null && nation != null && city != null && poseCode != null && detailAddress != null) {
            if(address != null) {
                address.setFname(fnameAdrr);
                address.setLname(lnameAdrr);
                address.setPhone(phoneAdrr);
                address.setDetailAddress(detailAddress);
                address.setNation(nation);
                address.setCity(city);
                address.setPostcode(poseCode);
                boolean isUpdateAddr = addressDAO.updateAddress(address, user.getId());
                if (isUpdateAddr) {
                    request.setAttribute("msgAddr", "Cập nhật thông tin thành công!");
                    address = addressDAO.getAddresById(user.getId());
                    request.setAttribute("address", address);
                } else {
                    request.setAttribute("errorAddr", "Có lỗi xảy ra, vui lòng thử lại!");
                }
            }else{
                address = new Address();
                address.setUserId(user.getId());
                address.setFname(fnameAdrr);
                address.setLname(lnameAdrr);
                address.setPhone(phoneAdrr);
                address.setDetailAddress(detailAddress);
                address.setNation(nation);
                address.setCity(city);
                address.setPostcode(poseCode);
                address.setEmail(user.getEmail());
                address.setDefault(true);
                boolean inserted =addressDAO.insertAddress(address);

                if (inserted) {
                    request.setAttribute("msgAddr", "Cập nhật thông tin thành công!");
                    address = addressDAO.getAddresById(address.getId());
                    request.setAttribute("address", address);
                } else {
                    request.setAttribute("errorAddr", "Có lỗi xảy ra, vui lòng thử lại!");
                }
            }
        }
        request.setAttribute("address", address);
        request.getRequestDispatcher("/WEB-INF/views/client/account.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("acc");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String fname = request.getParameter("firstname");
        String lname = request.getParameter("lastname");
        String phone = request.getParameter("phone");
        String gender = request.getParameter("gender");
        if (fname != null && lname != null && phone != null && gender != null) {
            User userToUpdate = new User();
            userToUpdate.setId(user.getId());
            userToUpdate.setFname(fname);
            userToUpdate.setLname(lname);
            userToUpdate.setPhoneNumber(phone);
            userToUpdate.setGender(gender);

            UserDAO userDAO = new UserDAO();
            boolean isUpdated = userDAO.updateProfile(userToUpdate);

            if (isUpdated) {
                user.setFname(fname);
                user.setLname(lname);
                user.setPhoneNumber(phone);
                user.setGender(gender);
                session.setAttribute("acc", user);

                request.setAttribute("msg", "Cập nhật thông tin thành công!");
            } else {
                request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại!");
            }
        }

        request.getRequestDispatcher("/WEB-INF/views/client/account.jsp").forward(request, response);
    }
}
