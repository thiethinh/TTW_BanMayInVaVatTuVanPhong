package com.papercraft.controller.admin;

import com.papercraft.dao.InventoryDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.InventoryTransaction;
import com.papercraft.model.InventoryTransactionDetail;
import com.papercraft.model.Product;
import com.papercraft.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AdminCreateInventoryServlet", value = "/admin/create-inventory")
public class AdminCreateInventoryServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDAO productDAO = new ProductDAO();
        List<Product> productList = productDAO.getAllProduct();

        request.setAttribute("productList", productList);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin-create-inventory.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        User user = (User) session.getAttribute("acc");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            String transactionType = request.getParameter("transactionType");
            String note = request.getParameter("note");
            double totalValue = Double.parseDouble(request.getParameter("totalValue"));

            InventoryTransaction transaction = new InventoryTransaction();
            transaction.setTransactionType(transactionType);
            transaction.setUserId(user.getId());
            transaction.setNote(note);
            transaction.setTotalValue(totalValue);

            String[] productIds = request.getParameterValues("productId[]");
            String[] quantities = request.getParameterValues("quantity[]");
            String[] prices = request.getParameterValues("price[]");

            List<InventoryTransactionDetail> details = new ArrayList<>();
            if (productIds != null && productIds.length > 0) {
                for (int i = 0; i < productIds.length; i++) {
                    InventoryTransactionDetail detail = new InventoryTransactionDetail();
                    detail.setProductId(Integer.parseInt(productIds[i]));
                    detail.setQuantity(Integer.parseInt(quantities[i]));
                    detail.setPrice(Double.parseDouble(prices[i]));
                    details.add(detail);
                }
            }
            transaction.setDetails(details);

            InventoryDAO inventoryDAO = new InventoryDAO();
            boolean isSuccess = inventoryDAO.insertTransaction(transaction);
            if (isSuccess) {
                session.setAttribute("success", "Tạo phiếu " + (transactionType.equals("IMPORT") ? "nhập" : "xuất") + " kho thành công!");
                response.sendRedirect(request.getContextPath() + "/admin/inventory-history");
            } else {
                session.setAttribute("error", "Có lỗi xảy ra khi tạo phiếu!");
                response.sendRedirect(request.getContextPath() + "/admin/create-inventory");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Dữ liệu không hợp lệ!");
            response.sendRedirect(request.getContextPath() + "/admin/create-inventory");
        }
    }
}
