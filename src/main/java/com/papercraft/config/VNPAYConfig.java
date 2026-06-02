package com.papercraft.config;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class VNPAYConfig {
    public static final String vnp_TmnCode = "4AP6RXHS";
    public static final String vnp_HashSecret = "R5FL6HKTPEGFUVNK1VSO7DHT1M6CB2Q5";
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    public static String getReturnUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        if (serverPort != 80) {
            url.append(":").append(serverPort);
        }
        url.append(contextPath).append("/vnpay-return");

        return url.toString();
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new Exception();
            }
            final Mac hcmacSHA512 = Mac.getInstance("HmacSHA512");
            byte[] hcmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hcmacKeyBytes, "HmacSHA512");
            hcmacSHA512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hcmacSHA512.doFinal(dataBytes);

            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip ==null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getRandomNumber(int length) {
        Random random = new Random();
        String chars = "0123456789";
        StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
