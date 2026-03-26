<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<c:set var="uri" value="${pageContext.request.requestURI}"/>

<aside class="admin-sidebar">
    <div class="sidebar-header">
        <img src="${pageContext.request.contextPath}/images/logo.webp" height="60" width="60" alt="Logo">
        <h2>PaperCraft Admin</h2>
    </div>
    <nav class="admin-nav">
        <ul>
            <li>
                <a href="${pageContext.request.contextPath}/admin"
                   class="${fn:endsWith(uri, '/admin.jsp') || fn:endsWith(uri, '/admin.jsp/') ? 'active' : ''}">
                    <i class="fa-solid fa-gauge"></i> Bảng Điều Khiển
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin-product"
                   class="${fn:contains(uri, 'admin-product') ? 'active' : ''}">
                    <i class="fa-solid fa-box-archive"></i> Quản Lý Sản Phẩm
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin-review"
                   class="${fn:contains(uri, 'admin-review') ? 'active' : ''}">
                    <i class="fa-solid fa-file-lines"></i> Quản Lý Đánh Giá
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin-order-manage"
                   class="${fn:contains(uri, 'admin-order-manage') ? 'active' : ''}">
                    <i class="fa-solid fa-receipt"></i> Quản Lý Đơn Hàng
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin-account"
                   class="${fn:contains(uri, 'admin-customer-manage') ? 'active' : ''}">
                    <i class="fa-solid fa-users"></i> Quản Lý Tài Khoản
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin-blog?view=pending"
                   class="${fn:contains(uri, 'admin-blog') ? 'active' : ''}">
                    <i class="fa-solid fa-blog"></i> Quản Lí Blog
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin-contacts"
                   class="${fn:contains(uri, 'admin-contacts') ? 'active' : ''}">
                    <i class="fa-solid fa-message"></i> Tin Nhắn Liên Hệ
                </a>
            </li>
            <li>
                <a href="${pageContext.request.contextPath}/admin-setting"
                   class="${fn:contains(uri, 'admin-setting') ? 'active' : ''}">
                    <i class="fa-solid fa-gear"></i> Cài Đặt Website
                </a>
            </li>
        </ul>
    </nav>

    <div class="sidebar-footer">
        <a href="${pageContext.request.contextPath}/home" class="btn-logout">
            <i class="fa-solid fa-right-from-bracket"></i> Xem Website
        </a>
    </div>
</aside>