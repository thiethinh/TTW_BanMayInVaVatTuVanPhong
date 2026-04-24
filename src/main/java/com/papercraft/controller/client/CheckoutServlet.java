package com.papercraft.controller.client;

import com.papercraft.dao.AddressDAO;
import com.papercraft.model.Address;
import com.papercraft.model.Cart;
import com.papercraft.model.Order;
import com.papercraft.model.OrderItem;
import com.papercraft.model.Product;
import com.papercraft.model.User;
import com.papercraft.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("acc");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/checkout");
            return;
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null || cart.list().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        AddressDAO addressDAO = new AddressDAO();
        Address userAddr = addressDAO.findDefaultAddress(user.getId());
        request.setAttribute("addr", userAddr);

        List<OrderItem> items = new ArrayList<>();
        double subTotal = 0;

        for (Product p : cart.list()) {
            OrderItem item = new OrderItem();
            item.setProduct(p);
            item.setProductId(p.getId());
            item.setQuantity(p.getQuantity());

            BigDecimal price = BigDecimal.valueOf(p.getPrice());
            item.setPrice(price);

            BigDecimal total = price.multiply(BigDecimal.valueOf(p.getQuantity()));
            item.setTotal(total);

            items.add(item);
            subTotal += total.doubleValue();
        }

        subTotal = Math.round(subTotal);
        double shippingFee = (subTotal > 5000000 || subTotal == 0) ? 0 : 30000;
        double vat = Math.round(subTotal * 0.05);
        double grandTotal = Math.round(subTotal + vat + shippingFee);

        request.setAttribute("items", items);
        request.setAttribute("subTotal", subTotal);
        request.setAttribute("vat", vat);
        request.setAttribute("shippingFee", shippingFee);
        request.setAttribute("grandTotal", grandTotal);

        request.getRequestDispatcher("/WEB-INF/views/client/payment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("acc");
        Cart cart = (Cart) session.getAttribute("cart");

        if (user == null || cart == null || cart.list().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        String fullname = request.getParameter("fullname");
        String phone = request.getParameter("phone");
        String note = request.getParameter("note");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String nation = request.getParameter("nation");
        String paymentMethod = request.getParameter("paymentMethod");

        String fullAddress = address + ", " + city + ", " + nation;

        Order order = new Order();
        order.setShippingName(fullname);
        order.setShippingPhone(phone);
        order.setShippingAddress(fullAddress);
//        order.setNote((note == null ? "" : note.trim()) + " (PTTT: " + paymentMethod + ")");
//
//        OrderService orderService = new OrderService();
//        boolean success = orderService.placeOrder(user, cart, order);

        order.setNote(note ==null ? "": note.trim());

        if (paymentMethod ==null || paymentMethod.isBlank()){
            paymentMethod = "COD";
        }

        OrderService orderService = new OrderService();
        boolean success= orderService.placeOrder(user,cart,order,paymentMethod);

        if (success) {
            session.removeAttribute("cart");
            session.setAttribute("success", "Đơn hàng của bạn đã được đặt thành công!");
            response.sendRedirect(request.getContextPath() + "/home");
        } else {
            request.setAttribute("error", "Đặt hàng thất bại, vui lòng thử lại!");
            doGet(request, response);
        }
    }
}