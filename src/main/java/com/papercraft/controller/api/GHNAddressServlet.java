package com.papercraft.controller.api;

import com.papercraft.service.GHNAddressService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@WebServlet("/api/ghn/address")
public class GHNAddressServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(GHNAddressServlet.class);
    private final GHNAddressService ghnAddressService = new GHNAddressService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");

        String type = request.getParameter("type");
        logger.info("Nhận yêu cầu API địa chỉ GHN. Phân loại truy vấn (type): '{}'", type);

        try {
            String resultJson;

            if ("province".equalsIgnoreCase(type)) {
                resultJson = ghnAddressService.getProvinces();

            } else if ("district".equalsIgnoreCase(type)) {
                String provinceId = request.getParameter("provinceId");

                if (provinceId == null || provinceId.isBlank()) {
                    logger.warn("Yêu cầu lấy danh mục Quận/Huyện thất bại: Thiếu tham số 'provinceId'.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"code\":400,\"message\":\"provinceId is required\"}");
                    return;
                }

                resultJson = ghnAddressService.getDistricts(provinceId);

            } else if ("ward".equalsIgnoreCase(type)) {
                String districtId = request.getParameter("districtId");

                if (districtId == null || districtId.isBlank()) {
                    logger.warn("Yêu cầu lấy danh mục Phường/Xã thất bại: Thiếu tham số 'districtId'.");
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().write("{\"code\":400,\"message\":\"districtId is required\"}");
                    return;
                }

                resultJson = ghnAddressService.getWards(districtId);

            } else {
                logger.warn("Tham số 'type' truyền vào không nằm trong danh mục hỗ trợ: '{}'", type);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"code\":400,\"message\":\"Invalid type\"}");
                return;
            }

            response.getWriter().write(resultJson);
            logger.debug("Phản hồi dữ liệu địa chỉ thành công cho loại hình: '{}'", type);
        } catch (Exception e) {
            logger.error("Xảy ra lỗi nghiêm trọng khi thiết lập kết nối hoặc xử lý dữ liệu từ đối tác API GHN (Type: '{}'): ", type, e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String safeMessage = e.getMessage() == null ? "Unknown error" : e.getMessage().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");

            response.getWriter().write("{\"code\":500,\"message\":\"Cannot call GHN API\",\"error\":\"" + safeMessage + "\"}"
            );
        }
    }
}