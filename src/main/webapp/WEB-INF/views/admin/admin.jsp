<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<fmt:setLocale value="vi_VN"/>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PaperCraft - Admin Bảng Điều Khiển</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp" />

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>

<body>

    <div class="admin-container">

        <jsp:include page="../includes/admin-sidebar.jsp"/>

        <main class="admin-main-content">

            <header class="admin-header">
                <h1>Bảng Điều Khiển</h1>
                <div class="admin-user-info">
                    <span>Xin chào, ADMIN</span>
                    <a href="${pageContext.request.contextPath}/admin?logout=1" class="btn-logout">Đăng Xuất</a>
                </div>
            </header>

            <section class="stats-grid">
                <div class="stat-card">
                    <div class="stat-icon" style="background-color: #eaf1ff;">
                        <i class="fa-solid fa-dollar-sign" style="color: #165FF2;"></i>
                    </div>
                    <div class="stat-info">
                        <span class="stat-number"><fmt:formatNumber value="${totalRevenue}" type="currency"/></span>
                        <span class="stat-label">Tổng Doanh Thu</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background-color: #fef4e6;">
                        <i class="fa-solid fa-receipt" style="color: #f5a623;"></i>
                    </div>
                    <div class="stat-info">
                        <span class="stat-number">${totalpendingOrder}</span>
                        <span class="stat-label">Đơn Hàng Chưa Phản Hồi</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background-color: #e6f7f0;">
                        <i class="fa-solid fa-message" style="color: #2e7d32;"></i>
                    </div>
                    <div class="stat-info">
                        <span class="stat-number">${totalUnrepliedContact}</span>
                        <span class="stat-label">Tin Nhắn Chưa Phản Hồi</span>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-icon" style="background-color: #fdeeee;">
                        <i class="fa-solid fa-users" style="color: #d9534f;"></i>
                    </div>
                    <div class="stat-info">
                        <span class="stat-number">${totalUser}</span>
                        <span class="stat-label">Tổng Khách Hàng</span>
                    </div>
                </div>
            </section>

            <section class="content-table-card">
                <h2>Đơn Hàng Chưa Phản Hồi</h2>

                <table class="content-table">
                    <thead>
                        <tr>
                            <th>Mã ĐH</th>
                            <th>Khách Hàng</th>
                            <th>Ngày Đặt</th>
                            <th>Tổng Tiền</th>
                            <th>Trạng Thái</th>
                            <th>Hành Động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${orders}" var="o">
                            <tr>
                                <td>${o.id}</td>
                                <td>${o.shippingName}</td>
                                <td>${o.createdAt}</td>
                                <td><fmt:formatNumber value="${o.totalPrice}" pattern="#,###"/> ₫</td>
                                <td><span class="status-badge pending">${o.status}</span></td>
                                <td><a href="${pageContext.request.contextPath}/admin-order-view?orderId=${o.id}" class="btn-action view">Xem</a></td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty orders}">
                            <tr>
                                <td colspan="6" style="text-align: center">Không tìm thấy đơn hàng</td>
                            </tr>

                        </c:if>
                    </tbody>
                </table>

                <div class="table-footer">
                    <a href="${pageContext.request.contextPath}/admin-order-manage">Xem tất cả đơn hàng &rarr;</a>
                </div>
            </section>

        </main>
    </div>

</body>

</html>