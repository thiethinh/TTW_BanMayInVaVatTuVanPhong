package com.papercraft.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.papercraft.config.GHNConfig;
import com.papercraft.model.Order;
import com.papercraft.model.OrderItem;
import com.papercraft.model.Payment;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GHNCreateOrderService {

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String createGHNOrder(Order order, List<OrderItem> orderItems, Payment payment) throws IOException, InterruptedException {

        if (order == null || orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order hoặc orderItems không hợp lệ");
        }

        if (order.getShippingDistrictId() == null || order.getShippingWardCode() == null || order.getShippingWardCode().isBlank()) {
            throw new IllegalArgumentException("Đơn hàng thiếu districtId hoặc wardCode để tạo đơn GHN");
        }

        String url = GHNConfig.BASE_URL + "/v2/shipping-order/create";
        int totalQuantity = 0;
        long insuranceValue = 0;

        JsonArray itemsJson = new JsonArray();

        for (OrderItem item : orderItems) {
            totalQuantity += item.getQuantity();

            BigDecimal itemTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            insuranceValue += itemTotal.longValue();

            JsonObject itemJson = new JsonObject();
            itemJson.addProperty("name", item.getProduct() != null ? item.getProduct().getProductName() : "Sản phẩm");
            itemJson.addProperty("code", String.valueOf(item.getProductId()));
            itemJson.addProperty("quantity", item.getQuantity());
            itemJson.addProperty("price", item.getPrice().longValue());
            itemsJson.add(itemJson);
        }

        int weight = Math.max(totalQuantity * 500, 500);
        int length = 30;
        int width = 20;
        int height = 10;

        insuranceValue = Math.min(insuranceValue, 5_000_000);

        boolean isCOD = payment != null && payment.getPaymentMethod() != null && "COD".equalsIgnoreCase(payment.getPaymentMethod());

        long codAmount = isCOD && order.getTotalPrice() != null ? order.getTotalPrice().longValue() : 0;

        JsonObject body = new JsonObject();

        body.addProperty("payment_type_id", GHNConfig.GHN_PAYMENT_TYPE_ID);
        body.addProperty("note", order.getNote() == null ? "" : order.getNote());
        body.addProperty("required_note", "KHONGCHOXEMHANG");

        // Mã đơn nội bộ để đối chiếu ngược
        body.addProperty("client_order_code", "PC-" + order.getId());

        // Thông tin shop lấy hàng
        body.addProperty("from_name", GHNConfig.SHOP_NAME);
        body.addProperty("from_phone", GHNConfig.SHOP_PHONE);
        body.addProperty("from_address", GHNConfig.SHOP_ADDRESS);
        body.addProperty("from_ward_name", GHNConfig.SHOP_WARD_NAME);
        body.addProperty("from_district_name", GHNConfig.SHOP_DISTRICT_NAME);
        body.addProperty("from_province_name", GHNConfig.SHOP_PROVINCE_NAME);

        // Thông tin người nhận
        body.addProperty("to_name", order.getShippingName());
        body.addProperty("to_phone", order.getShippingPhone());
        body.addProperty("to_address", order.getShippingAddress());
        body.addProperty("to_ward_code", order.getShippingWardCode());
        body.addProperty("to_district_id", order.getShippingDistrictId());

        body.addProperty("cod_amount", codAmount);
        body.addProperty("content", "PaperCraft - Đơn hàng #" + order.getId());

        body.addProperty("weight", weight);
        body.addProperty("length", length);
        body.addProperty("width", width);
        body.addProperty("height", height);
        body.addProperty("insurance_value", insuranceValue);
        body.addProperty("service_type_id", GHNConfig.GHN_SERVICE_TYPE_ID);

        body.add("items", itemsJson);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Token", GHNConfig.API_TOKEN)
                .header("ShopId", GHNConfig.SHOP_ID)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("===== GHN CREATE ORDER REQUEST =====");
        System.out.println(body);
        System.out.println("===== GHN CREATE ORDER RESPONSE =====");
        System.out.println(response.body());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("GHN Create Order API HTTP " + response.statusCode() + ": " + response.body());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("code") || json.get("code").getAsInt() != 200) {
            throw new IOException("GHN Create Order failed: " + response.body());
        }

        JsonObject data = json.getAsJsonObject("data");
        if (data == null || !data.has("order_code") || data.get("order_code").isJsonNull()) {
            throw new IOException("GHN không trả về order_code: " + response.body());
        }

        return data.get("order_code").getAsString();
    }
}