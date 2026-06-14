package com.papercraft.config;

public class GHNConfig {
    public static final String BASE_URL = "https://online-gateway.ghn.vn/shiip/public-api";
    public static final String API_TOKEN = getEnvOrDefault("GHN_API_TOKEN", "ce95f091-598f-11f1-b69e-d24b77050b6a");
    public static final String SHOP_ID = getEnvOrDefault("GHN_SHOP_ID", "6458847");
    public static final String SHOP_NAME = getEnvOrDefault("GHN_SHOP_NAME", "PaperCraft");
    public static final String SHOP_PHONE = getEnvOrDefault("GHN_SHOP_PHONE", "0365065375");
    public static final String SHOP_ADDRESS = getEnvOrDefault("GHN_SHOP_ADDRESS", "Trường Đại Học Nông Lâm");

    public static final String SHOP_WARD_NAME = getEnvOrDefault("GHN_SHOP_WARD_NAME", "Phường Linh Trung");
    public static final String SHOP_DISTRICT_NAME = getEnvOrDefault("GHN_SHOP_DISTRICT_NAME", "Thủ Đức");
    public static final String SHOP_PROVINCE_NAME = getEnvOrDefault("GHN_SHOP_PROVINCE_NAME", "Hồ Chí Minh");

    public static final int GHN_SERVICE_TYPE_ID = Integer.parseInt(getEnvOrDefault("GHN_SERVICE_TYPE_ID", "2"));
    public static final int GHN_PAYMENT_TYPE_ID = Integer.parseInt(getEnvOrDefault("GHN_PAYMENT_TYPE_ID", "1"));

    private static String getEnvOrDefault(String key, String defaultValue) {
        String value = System.getenv(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }
}