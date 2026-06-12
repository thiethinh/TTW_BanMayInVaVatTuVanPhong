package com.papercraft.controller.client;

import com.papercraft.dao.CartDAO;
import com.papercraft.dao.ProductDAO;
import com.papercraft.model.Cart;
import com.papercraft.model.Product;
import com.papercraft.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(CartServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        HttpSession session = request.getSession();

        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            logger.info("Giỏ hàng chưa tồn tại trong session. Khởi tạo giỏ hàng mới.");
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        logger.debug("Nhận yêu cầu xử lý giỏ hàng. Action: '{}', Cart Session ID: '{}'", action, session.getId());

        ProductDAO dao = new ProductDAO();
        CartDAO cartDAO = new CartDAO();
        try {
            if ("add".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                int qty = Integer.parseInt(request.getParameter("quantity"));
                logger.info("Thực hiện hành động ADD: Thêm sản phẩm ID {} với số lượng {} vào giỏ.", id, qty);

                Product p = dao.getProductById(id);

                if (p == null) {
                    logger.warn("ADD thất bại: Sản phẩm ID {} không tồn tại trong hệ thống.", id);
                    sendJson(response, false, "Sản phẩm không tồn tại", cart.getTotalQuantity());
                    return;
                }

                if (qty <= 0) {
                    logger.warn("ADD thất bại: Số lượng yêu cầu từ client không hợp lệ ({} <= 0).", qty);
                    sendJson(response, false, "Số lượng thêm vào giỏ phải lớn hơn 0", cart.getTotalQuantity());
                    return;
                }

                if (p.getStockQuantity() <= 0) {
                    logger.warn("ADD thất bại: Sản phẩm ID {} ('{}') đã hết hàng trong kho.", id, p.getStockQuantity());
                    sendJson(response, false, "Sản phẩm đã hết hàng", cart.getTotalQuantity());
                    return;
                }

                p.setQuantity(qty);

                String error = cart.putWithCheckStock(p, p.getStockQuantity());

                if (error != null) {
                    logger.warn("ADD thất bại: Lỗi kiểm tra tồn kho từ Cart Model cho sản phẩm ID {}. Lý do: '{}'", id, error);
                    sendJson(response, false, error, cart.getTotalQuantity());
                    return;
                }

                session.setAttribute("cart", cart);

                if (session.getAttribute("acc") != null) {
                    User user = (User) session.getAttribute("acc");
                    Product inCart = cart.get(id);

                    if (inCart != null) {
                        logger.info("Đồng bộ CSDL: Lưu sản phẩm ID {} (Số lượng: {}) cho User ID {}.", id, inCart.getQuantity(), user.getId());
                        cartDAO.saveItem(user.getId(), id, inCart.getQuantity());
                    }
                }

                logger.info("ADD thành công: Sản phẩm ID {} đã được thêm vào giỏ. Tổng lượng giỏ hàng hiện tại: {}.", id, cart.getTotalQuantity());
                sendJson(response, true, "Thêm vào giỏ hàng thành công", cart.getTotalQuantity());
                return;

            } else if ("count".equals(action)) {
                logger.debug("Thực hiện hành động COUNT: Trả về số lượng tổng hiện tại là {}.", cart.getTotalQuantity());
                response.getWriter().print(cart.getTotalQuantity());
                return;
            } else if ("calculateSelected".equals(action)) {
                String selectedIdsRaw = request.getParameter("selectedIds");
                logger.info("Thực hiện hành động CALCULATE_SELECTED. Danh sách ID nhận được: '{}'", selectedIdsRaw);
                List<Integer> selectedIds = parseSelectedIds(selectedIdsRaw);

                double[] bill = calculateBillBySelectedItems(cart, selectedIds);

                response.setContentType("application/json; charset=UTF-8");
                logger.debug("Kết quả tính bill selected -> SubTotal: {}, GrandTotal: {}", bill[0], bill[3]);
                response.getWriter().print(String.format(
                        "{\"success\":true,\"cartCount\":%d,\"subTotal\":%.0f,\"shippingFee\":%.0f,\"vat\":%.0f,\"grandTotal\":%.0f}",
                        cart.getTotalQuantity(),
                        bill[0],
                        bill[1],
                        bill[2],
                        bill[3]
                ));

                return;


            } else if ("remove".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                logger.info("Thực hiện hành động REMOVE: Xóa sản phẩm ID {} khỏi giỏ hàng.", id);
                cart.remove(id);
                session.setAttribute("cart", cart);

                //Tính lại bill
                double subTotal = Math.round(cart.total());
                double shippingFee = 0;
                double vat = Math.round(subTotal * 0.05);
                double grandTotal = Math.round(subTotal + vat);

                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().print(String.format(
                        "{\"success\":true,\"cartCount\":%d,\"subTotal\":%.0f,\"shippingFee\":%.0f,\"vat\":%.0f,\"grandTotal\":%.0f,\"empty\":%b}",
                        cart.getTotalQuantity(), subTotal, shippingFee, vat, grandTotal, cart.list().isEmpty()
                ));
                if (session.getAttribute("acc") != null) {
                    User user = (User) session.getAttribute("acc");
                    logger.info("Đồng bộ CSDL: Xóa sản phẩm ID {} của User ID {}.", id, user.getId());
                    cartDAO.deleteItem(user.getId(), id);
                }
                return;
            } else if ("update".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                logger.info("Thực hiện hành động UPDATE: Thay đổi số lượng sản phẩm ID {} thành {}.", id, quantity);

                if (quantity <= 0) {
                    logger.warn("UPDATE thất bại: Số lượng thay đổi không hợp lệ ({} <= 0).", quantity);
                    sendJson(response, false, "Số lượng sản phẩm phải ít nhất là 1", cart.getTotalQuantity());
                    return;
                }

                Product p = dao.getProductById(id);

                if (p == null) {
                    logger.warn("UPDATE thất bại: Sản phẩm ID {} không tồn tại.", id);
                    sendJson(response, false, "Sản phẩm không tồn tại!", cart.getTotalQuantity());
                    return;
                }

                if (p.getStockQuantity() <= 0) {
                    logger.warn("UPDATE thất bại: Sản phẩm ID {} đã hết hàng trong kho.", id);
                    sendJson(response, false, "Sản phẩm đã hết hàng", cart.getTotalQuantity());
                    return;
                }

                String error = cart.updateWithStock(id, quantity, p.getStockQuantity());
                session.setAttribute("cart", cart);

                if (error != null) {
                    logger.warn("UPDATE thất bại: Không vượt qua kiểm tra stock tồn kho cho sản phẩm ID {}. Chi tiết: '{}'", id, error);
                    sendJson(response, false, error, cart.getTotalQuantity());
                } else {
                    double subTotal = Math.round(cart.total());
                    double shippingFee = 0;
                    double vat = Math.round(subTotal * 0.05);
                    double grandTotal = Math.round(subTotal + vat);

                    //Capap nhật giá của item vừa update
                    double itemTotal = p.getPrice() * quantity;

                    logger.info("UPDATE thành công sản phẩm ID {}. ItemTotal mới: {}", id, itemTotal);
                    response.setContentType("application/json; charset=UTF-8");
                    response.getWriter().print(String.format(
                            "{\"success\":true,\"cartCount\":%d,\"subTotal\":%.0f,\"shippingFee\":%.0f,\"vat\":%.0f,\"grandTotal\":%.0f,\"itemTotal\":%.0f}",
                            cart.getTotalQuantity(), subTotal, shippingFee, vat, grandTotal, itemTotal
                    ));
                }
                if (session.getAttribute("acc") != null && error == null) {
                    User user = (User) session.getAttribute("acc");
                    logger.info("Đồng bộ CSDL: Cập nhật số lượng mới ({}) cho sản phẩm ID {} của User ID {}.", quantity, id, user.getId());
                    cartDAO.saveItem(user.getId(), id, quantity);
                }
                return;
            }

        } catch (NumberFormatException e) {
            logger.error("Lỗi xử lý tham số đầu vào (Sai định dạng số) trong luồng xử lý Giỏ hàng: ", e);
            sendJson(response, false, "Dữ liệu không hợp lệ!", 0);
            return;
        }

        //=== Bill ====
//        double subTotal = Math.round(cart.total());
//        double shippingFee = 0;
//        double vat = Math.round(subTotal * 0.05);
//        double grandTotal = Math.round(subTotal + vat);
//
//
//        //set sang JSP
//        List<Product> items = new ArrayList<>(cart.list());
//        for (Product item : items) {
//            Product fresh = dao.getProductById(item.getId());
//            if (fresh != null) {
//                item.setStockQuantity(fresh.getStockQuantity());
//                item.setThumbnail(fresh.getThumbnail());
//            }
//        }
//        request.setAttribute("items", items);
//
//        request.setAttribute("subTotal", subTotal);
//        request.setAttribute("shippingFee", shippingFee);
//        request.setAttribute("vat", vat);
//        request.setAttribute("grandTotal", grandTotal);
//
//        request.getRequestDispatcher("/WEB-INF/views/client/cart.jsp").forward(request, response);

        logger.info("Thực hiện tải trang giao diện Giỏ hàng (Mặc định). Tổng số item hiển thị: {}", cart.list().size());
        List<Product> items = new ArrayList<>(cart.list());

        List<Integer> availableIds = new ArrayList<>();

        for (Product item : items) {
            Product fresh = dao.getProductById(item.getId());

            if (fresh != null) {
                item.setStockQuantity(fresh.getStockQuantity());
                item.setThumbnail(fresh.getThumbnail());
                item.setPrice(fresh.getPrice());
                item.setOriginPrice(fresh.getOriginPrice());
                item.setDiscount(fresh.getDiscount());

                if (fresh.getStockQuantity() > 0 && item.getQuantity() <= fresh.getStockQuantity()) {
                    availableIds.add(item.getId());
                }
            } else {
                logger.warn("Sản phẩm ID {} nằm trong giỏ hàng nhưng không tìm thấy thông tin mới nhất trong CSDL.", item.getId());
                item.setStockQuantity(0);
            }
        }

        // Bill mặc định chỉ tính sp còn hàng
        logger.debug("Tính toán hóa đơn hiển thị mặc định cho các sản phẩm hợp lệ còn hàng. Số lượng ID: {}", availableIds.size());
        double[] bill = calculateBillBySelectedItems(cart, availableIds);

        request.setAttribute("items", items);
        request.setAttribute("subTotal", bill[0]);
        request.setAttribute("shippingFee", bill[1]);
        request.setAttribute("vat", bill[2]);
        request.setAttribute("grandTotal", bill[3]);

        logger.debug("Chuyển hướng dữ liệu giỏ hàng sang view JSP Render.");
        request.getRequestDispatcher("/WEB-INF/views/client/cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("Nhận yêu cầu qua phương thức POST tại /cart, chuyển tiếp xử lý sang doGet.");
        doGet(request, response);
    }

    //calculateBillBySelectedItems
    private double[] calculateBillBySelectedItems(Cart cart, List<Integer> selectedIds) {
        ProductDAO dao = new ProductDAO();
        double subTotal = 0;

        for (Integer id : selectedIds) {
            Product cartProduct = cart.get(id);

            if (cartProduct == null) {
                continue;
            }

            Product freshProduct = dao.getProductById(id);

            if (freshProduct == null || freshProduct.getStockQuantity() <= 0) {
                logger.debug("Tính tiền bỏ qua: Sản phẩm ID {} đã hết hàng hoặc không tồn tại ngoài CSDL.", id);
                continue;
            }

            if (cartProduct.getQuantity() > freshProduct.getStockQuantity()) {
                logger.debug("Tính tiền bỏ qua: Số lượng trong giỏ của sản phẩm ID {} lớn hơn số lượng kho hiện tại.", id);
                continue;
            }

            subTotal += freshProduct.getPrice() * cartProduct.getQuantity();
        }

        subTotal = Math.round(subTotal);
        double shippingFee = 0;
        double vat = Math.round(subTotal * 0.05);
        double grandTotal = Math.round(subTotal + vat);

        return new double[]{subTotal, shippingFee, vat, grandTotal};
    }

    //parseSelectedIds
    private List<Integer> parseSelectedIds(String selectedIdsRaw) {
        List<Integer> ids = new ArrayList<>();

        if (selectedIdsRaw == null || selectedIdsRaw.trim().isEmpty()) {
            return ids;
        }
        String[] parts = selectedIdsRaw.split(",");

        for (String part : parts) {
            try {
                int id = Integer.parseInt(part.trim());
                ids.add(id);
            } catch (NumberFormatException e) {
                logger.warn("Không thể chuyển đổi chuỗi ID '{}' thành số nguyên trong cụm dữ liệu raw '{}'", part, selectedIdsRaw);
            }
        }
        return ids;
    }

    private void sendJson(HttpServletResponse response, boolean success,
                          String message, int cartCount) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        //=== Escape-bỏ all ký tự nguy hiểm trong message
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