package com.papercraft.model.enums;

public enum NotificationType {
    // ORDER
    ORDER_CREATED("Đơn hàng đã được tạo", "order/"),
    ORDER_CONFIRMED("Đơn hàng đã được xác nhận", "order/"),
    ORDER_PACKING("Đơn hàng đang được chuẩn bị", "order/"),
    ORDER_SHIPPING("Đơn hàng đang được giao", "order/"),
    ORDER_DELIVERED("Đơn hàng đã giao thành công", "order/"),
    ORDER_CANCELLED("Đơn hàng đã bị hủy", "order/"),

    // BLOG
    BLOG_SUBMITTED("Blog đã được gửi", "blog/"),
    BLOG_APPROVED("Blog đã được duyệt", "blog/"),
    BLOG_REJECTED("Blog bị từ chối", "blog/"),

    // CONTACT
    CONTACT_SUBMITTED("Liên hệ đã được gửi", "contact/"),
    CONTACT_REPLIED("Liên hệ đã được phản hồi", "contact/"),

    // ACCOUNT
    PASSWORD_CHANGED("Mật khẩu đã được thay đổi", "profile/"),

    // PROMOTION
    PROMOTION("Khuyến mãi mới", "voucher/");

    private final String title;
    private final String routePattern;

    NotificationType(String title, String routePattern) {
        this.title = title;
        this.routePattern = routePattern;
    }

    public String getTitle() {
        return title;
    }

    public String getRoutePattern() {
        return routePattern;
    }

    public boolean requiresReferenceId() {
        return routePattern.contains("order/") ||
                routePattern.contains("blog/") ||
                routePattern.contains("contact/");
    }
}
