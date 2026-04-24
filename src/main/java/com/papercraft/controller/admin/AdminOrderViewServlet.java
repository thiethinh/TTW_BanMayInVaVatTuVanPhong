package com.papercraft.controller.admin;

import com.papercraft.dao.OrderDAO;
import com.papercraft.dao.OrderItemDAO;
import com.papercraft.dao.PaymentDAO;
import com.papercraft.dao.UserDAO;
import com.papercraft.model.Order;
import com.papercraft.model.OrderItem;
import com.papercraft.model.Payment;
import com.papercraft.model.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminOrderViewServlet", value = "/admin-order-view")
public class AdminOrderViewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderID = request.getParameter("orderId");
        String verifyPayment= request.getParameter("verifyPayment");
        String transactionCode= request.getParameter("transactionCode");

        String accept = request.getParameter("accept");
        String cancel = request.getParameter("cancel");
        int id = orderID != null ? Integer.parseInt(orderID) : 0;
        OrderDAO orderDAO = new OrderDAO();

        boolean updated = false;

        boolean isAccept = false;
        boolean isCancel = false;

        if (accept != null) {
            updated = orderDAO.updateOrderStatus(id, accept);
            isAccept = true;
        } else if (cancel != null) {
            updated = orderDAO.updateOrderStatus(id, cancel);
            isCancel = true;
        }
    // Xuwr lys VerifyPayment
        boolean isVeryfyPayment= false;
        boolean verifiedPayment = false;

        if (verifyPayment != null ){
            PaymentDAO paymentDAO = new PaymentDAO();
            Payment currentPayment= paymentDAO.getPaymentByOrderId(id);
            Order currentOrder= orderDAO.getOrderByID(id);

            if (currentPayment != null && !Boolean.TRUE.equals(currentPayment.getStatus())){
                String method= currentPayment.getPaymentMethod();

                //Gia su cho COD verify khi owr trang thai shipping/complete
                if ("COD".equalsIgnoreCase(method)){
                    if (currentOrder != null && ("shipped".equalsIgnoreCase(currentOrder.getStatus())
                    || "completed".equalsIgnoreCase(currentOrder.getStatus()))){
                        verifiedPayment= paymentDAO.verifyPaymentSuccess(id,transactionCode);
                        isVeryfyPayment= true;
                    }
                }else {
                    verifiedPayment= paymentDAO.verifyPaymentSuccess(id,transactionCode);
                    isVeryfyPayment= true;
                }
            }
        }


        Order order = orderDAO.getOrderByID(id);

        if(order == null){
            return;
        }
        OrderItemDAO orderItemDAO = new OrderItemDAO();
        List<OrderItem> orderItems = orderItemDAO.getItemByOrderId(id);
        order.setOrderItems(orderItems);

        User user = new UserDAO().getBasicInfoById(order.getUserId());

        Payment payment = new PaymentDAO().getPaymentByOrderId(id);

        request.setAttribute("order", order);
        request.setAttribute("orderItems", orderItems);
        request.setAttribute("user", user);
        request.setAttribute("payment", payment);
        request.setAttribute("updated", updated);
        request.setAttribute("isAccept", isAccept);
        request.setAttribute("isCancel", isCancel);
        request.setAttribute("isVerifyPayment",isVeryfyPayment);
        request.setAttribute("verifiedPayment", verifiedPayment);

        request.getRequestDispatcher("/WEB-INF/views/admin/admin-order-view.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Code xử lý yêu cầu POST
    }
}