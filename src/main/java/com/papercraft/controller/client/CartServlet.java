package com.papercraft.controller.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Cart;
import com.papercraft.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        String action= request.getParameter("action");
        HttpSession session = request.getSession();

        //== check login mới được thêm vào gior hàng( chặn từ server) ===
        if (session.getAttribute("acc") == null && !"count".equals(action)) {
            if(action ==null){
                response.sendRedirect((request.getContextPath() + "/login?redirect=/cart"));
            }else{
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            return;
        }


        Cart cart = (Cart) session.getAttribute("cart");
        if(cart == null){
            cart= new Cart();
            session.setAttribute("cart",cart);
        }
        ProductDAO dao = new ProductDAO();
        try{
            if("add".equals(action)){
                int id=Integer.parseInt(request.getParameter("id"));
                System.out.println("Debug: id = " + id);
                int qty = Integer.parseInt(request.getParameter("quantity"));

                Product p = dao.getProductById(id);
                System.out.println("Debug: product = " + p);
                if (p == null){
                    sendJson(response, false, "Sản phẩm không tồn tại", cart.getTotalQuantity());
                    return;

                }
                p.setQuantity(qty);
                String error = cart.putWithCheckStock(p, p.getStockQuantity());
                session.setAttribute("cart", cart);

                if(error != null){
                    sendJson(response, false, error, cart.getTotalQuantity());
                }else{
                    sendJson(response, true, error, cart.getTotalQuantity());

                }
                return;
            }
            else if ("count".equals(action)) {
                response.getWriter().print(cart.getTotalQuantity());
                return;
            }
            else if ("remove".equals(action)){
                int id = Integer.parseInt(request.getParameter("id"));
                cart.remove(id);
                session.setAttribute("cart", cart);
                response.getWriter().print(cart.getTotalQuantity());
                return;
            }
            else if("update".equals(action)){
                int id= Integer.parseInt(request.getParameter("id"));
                int quantity= Integer.parseInt(request.getParameter("quantity"));

                Product p = dao.getProductById(id);
                if (p == null){
                    sendJson(response, false, "Sản phẩm không tồn tại!", cart.getTotalQuantity());
                    return;
                }
                String error = cart.updateWithStock(id, quantity, p.getStockQuantity());
                session.setAttribute("cart", cart);

                if (error != null){
                    sendJson(response, false, error,cart.getTotalQuantity());
                }else{
                    sendJson(response,true, null, cart.getTotalQuantity());
                }
                return;
            }

        } catch (NumberFormatException e) {
            sendJson(response, false, "Dữ liệu không hợp lệ!", 0);
            return;
        }

        //=== Bill ====
        double subTotal= Math.round(cart.total());
        double shippingFee = (subTotal > 5000000 || subTotal ==0) ? 0 : 30000;
        double vat= Math.round(subTotal * 0.05);
        double grandTotal = Math.round(subTotal + vat + shippingFee);


        //set sang JSP
        List<Product> items= new ArrayList<>(cart.list());
        for (Product item : items){
            Product fresh = dao.getProductById(item.getId());
            if (fresh != null ){
                item.setStockQuantity(fresh.getStockQuantity());
            }
        }
        request.setAttribute("items", items);

        request.setAttribute("subTotal", subTotal);
        request.setAttribute("shippingFee", shippingFee);
        request.setAttribute("vat",vat);
        request.setAttribute("grandTotal", grandTotal);

        request.getRequestDispatcher("/WEB-INF/views/client/cart.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        doGet(request, response);
    }
    private void sendJson(HttpServletResponse response, boolean success,
                          String message, int cartCount) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        //=== Escape all ký tự nguy hiểm trong message
        String safeMsg = (message == null) ? "" : message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        String json = String.format(
                "{\"success\":%b,\"message\":\"%s\",\"cartCount\":%d}",
                success, safeMsg, cartCount
        );

        response.getWriter().print(json);
    }
}