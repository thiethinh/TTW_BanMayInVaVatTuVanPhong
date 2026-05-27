package com.papercraft.controller.client;

import com.papercraft.dao.AddressDAO;
import com.papercraft.dao.CartDAO;
import com.papercraft.dao.UserVoucherDAO;
import com.papercraft.dao.VoucherDAO;
import com.papercraft.model.*;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        //Kiểm tra Login
        if (session.getAttribute("acc") == null) {
            response.sendRedirect(request.getContextPath() + "/login?redirect=/checkout");
            return;
        }

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


        String selectedIdsRaw = request.getParameter("selectedIds");
        Set<Integer> selectedIds= parseSelectedIdSet(selectedIdsRaw);

        if (selectedIds.isEmpty()){
            response.sendRedirect(request.getContextPath()+ "/cart");
            return;
        }

        AddressDAO addressDAO = new AddressDAO();
        Address userAddr = addressDAO.findDefaultAddress(user.getId());
        request.setAttribute("addr", userAddr);

//        UserVoucherDAO  userVoucherDAO = new UserVoucherDAO();
//        List<Voucher> vouchers = userVoucherDAO.getVouchersByUserId(user.getId());
//        request.setAttribute("vouchers", vouchers);
//
//        String voucherCode=request.getParameter("voucherCode");
//        if(voucherCode!=null&&!voucherCode.trim().isEmpty()){
//            Voucher voucher=new VoucherDAO().getVoucherByCode(voucherCode.trim());
//            if(voucher==null){
//                request.setAttribute("saveVoucherError", "Mã voucher không tồn tại");
//            }else if(!voucher.isAvailable()){
//                request.setAttribute("saveVoucherError", "Voucher hiện không khả dụng");
//            }else{
//                boolean success= userVoucherDAO.addUserVoucher(user.getId(), voucher.getId());
//                if(!success){
//                    request.setAttribute("saveVoucherError", "Bạn đã lưu voucher này rồi");
//                }else{
//                    request.setAttribute("saveVoucherSuccess", "Áp dụng voucher thành công");
//                    request.setAttribute("selectedVoucher",voucher);
//                }
//            }
//        }


        List<OrderItem> items = new ArrayList<>();
        double subTotal = 0;

        for (Product p : cart.list()) {

            //nếu sp khng được tick
            if (!selectedIds.contains(p.getId())){
                continue;
            }

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

        //nếu selectedId gửi lên không khớp sp trong cart => không cho checkout(về cart )
        if (items.isEmpty()){
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        subTotal = Math.round(subTotal);
        double shippingFee = (subTotal > 5000000 || subTotal == 0) ? 0 : 30000;
        double vat = Math.round(subTotal * 0.05);
        double grandTotal = Math.round(subTotal + vat + shippingFee);


        //tính voucher
        BigDecimal discountAmount = BigDecimal.ZERO;
        String voucherIdRaw = request.getParameter("voucherId");
        Voucher selectedVoucher = null;
        if (voucherIdRaw != null && !voucherIdRaw.isBlank()) {
            try {
                int voucherId = Integer.parseInt(voucherIdRaw);
                selectedVoucher = new VoucherDAO().getVoucherById(voucherId);
                if (selectedVoucher != null) {
                    String voucherError = selectedVoucher.validateString(BigDecimal.valueOf(grandTotal));

                    if (voucherError != null) {
                        request.setAttribute("errorVoucher", voucherError);

                    } else {
                        discountAmount = selectedVoucher.calculateDiscount(BigDecimal.valueOf(grandTotal));
                        grandTotal = selectedVoucher.applyDiscount(BigDecimal.valueOf(grandTotal)).toBigInteger().doubleValue();
                        request.setAttribute("successVoucher", "Áp dụng voucher thành công");
                        request.setAttribute("selectedVoucher", selectedVoucher);
                    }
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorVoucher", "Voucher không hợp lệ"
                );
            }
        }



        request.setAttribute("items", items);
        request.setAttribute("subTotal", subTotal);
        request.setAttribute("vat", vat);
        request.setAttribute("shippingFee", shippingFee);
        request.setAttribute("discountAmount", discountAmount);
        request.setAttribute("grandTotal", grandTotal);

        request.setAttribute("selectedIds",selectedIdsRaw);

        request.getRequestDispatcher("/WEB-INF/views/client/payment.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("acc");
        Cart cart = (Cart) session.getAttribute("cart");

        String selectedIdsRaw= request.getParameter("selectedIds");
        Set<Integer> selectedIds= parseSelectedIdSet(selectedIdsRaw);

        if (user == null || cart == null || cart.list().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        if (selectedIds.isEmpty()){
            response.sendRedirect(request.getContextPath()+ "/cart");
            return;
        }
        Cart selectedCart= new Cart();
        for (Product p: cart.list()){
            if (selectedIds.contains(p.getId())){
                selectedCart.put(p);
            }
        }
        if (selectedCart.list().isEmpty()){
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
        String shippingProvider = request.getParameter("shippingProvider");
        String shippingFeeRaw = request.getParameter("shippingFee");

        //parse phí ship
        BigDecimal shippingFee = BigDecimal.ZERO;

        try {
            if (shippingFeeRaw != null && !shippingFeeRaw.isBlank()) {
                shippingFee = new BigDecimal(shippingFeeRaw.trim());
            }
        } catch (NumberFormatException e) {
            shippingFee = BigDecimal.ZERO;
        }

        if (shippingProvider == null || shippingProvider.isBlank()) {
            shippingProvider = "GHN";
        }


        String fullAddress = address + ", " + city + ", " + nation;

        Order order = new Order();
        order.setShippingName(fullname);
        order.setShippingPhone(phone);
        order.setShippingAddress(fullAddress);
        order.setShippingProvider(shippingProvider);
        order.setShippingFee(shippingFee);

//        //test
//        order.setShippingProvider("GHN");
//        order.setShippingFee(BigDecimal.valueOf(30000));

        order.setNote(note ==null ? "": note.trim());

        if (paymentMethod ==null || paymentMethod.isBlank()){
            paymentMethod = "COD";
        }

        OrderService orderService = new OrderService();
        int orderId = orderService.placeOrderAndReturnId(user, selectedCart, order, paymentMethod);

        if (orderId > 0) {
            CartDAO cartDAO = new CartDAO();

            for (Integer id : selectedIds) {
                cart.remove(id);
                cartDAO.deleteItem(user.getId(), id);
            }

            session.setAttribute("cart", cart);

            // Cho phép vào trang /orderSuccess
            session.setAttribute("orderSuccess", true);

            // Lưu orderId vừa đặt
            session.setAttribute("lastOrderId", orderId);

            response.sendRedirect(request.getContextPath() + "/order-success");
        } else {
            request.setAttribute("error", "Đặt hàng thất bại. Có thể một số sản phẩm không còn đủ tồn kho, vui lòng kiểm tra lại giỏ hàng.");
            doGet(request, response);
        }
    }

    //parseSelectedIdSet
    private Set<Integer> parseSelectedIdSet(String selectedIdsRaw){
        Set<Integer> result = new HashSet<>();

        //check đàu vào rỗng
        boolean isEmpty= selectedIdsRaw == null || selectedIdsRaw.trim().isEmpty();
        if (isEmpty){
            return result;
        }
        //tách chuỗi thành mảng theo ","
        String[] parts= selectedIdsRaw.split(",");
        for (String part : parts){
            String trimmed = part.trim();
            if (trimmed.matches("\\d+")){
                result.add(Integer.parseInt(trimmed));
            }
        }
        return result;
    }
}