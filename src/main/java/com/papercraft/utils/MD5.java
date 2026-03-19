package com.papercraft.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] msgDigest = md.digest(input.getBytes());

            // Chuyển byte sang hệ 16 để lưu vào DB
            StringBuilder sb = new StringBuilder();
            for (byte b : msgDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
