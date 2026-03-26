package com.papercraft.controller.client;

import java.io.IOException;

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
        String action= request.getParameter("action");
        HttpSession session = request.getSession();

        //== check login mới được thêm vào gior hàng( chặn từ server) ===
        if (session.getAttribute("acc") == null && !"count".equals(action)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Cart cart = (Cart) session.getAttribute("cart");
        if(cart == null){
            cart= new Cart();
            session.setAttribute("cart",cart);
        }

        if ("count".equals(action)) {
            response.setContentType("text/plain");
            response.getWriter().print(cart.getTotalQuantity());
            return;
        }

        try{
            if("add".equals(action)){
                int id=Integer.parseInt(request.getParameter("id"));
                int qty = Integer.parseInt(request.getParameter("quantity"));
                ProductDAO dao = new ProductDAO();
                Product p = dao.getProductById(id);
                if (p != null){
                    p.setQuantity(1);
                    cart.put(p);
                }
            }
            else if ("remove".equals(action)){
                int id = Integer.parseInt(request.getParameter("id"));
                cart.remove(id);
            }
            else if("update".equals(action)){
                int id= Integer.parseInt(request.getParameter("id"));
                int quantity= Integer.parseInt(request.getParameter("quantity"));
                cart.update(id,quantity);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
        //=== Bill ====
        double subTotal= Math.round(cart.total());
        double shippingFee = (subTotal > 5000000 || subTotal ==0) ? 0 : 30000;
        double vat= Math.round(subTotal * 0.05);
        double grandTotal = Math.round(subTotal + vat + shippingFee);


        //set sang JSP
        request.setAttribute("items", cart.list());
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
}