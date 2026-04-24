<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<fmt:setTimeZone value="Asia/Ho_Chi_Minh" />
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PaperCraft - Admin Quản Lý Đơn Hàng</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-product-edit.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-order-manage.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
</head>


<body>

<div class="admin-container">
    <jsp:include page="../includes/admin-sidebar.jsp"/>

    <!-- ===========main========= -->
    <main class="admin-main-content">
        <div class="admin-order-manage-view">
            <h1 id="title-table">Quản Lý Đơn Hàng</h1>
        </div>
        <section class="content-table-view">
            <div class="filter">
                <label for="statusFilter"><i class="fa fa-filter"></i> Lọc theo trạng thái:</label>
                <select id="statusFilter" onchange="location.href='?status=' + this.value;">
                    <option value="" ${param.status == '' ? 'selected' : ''}>Tất cả</option>
                    <option value="pending" ${param.status == 'pending' ? 'selected' : ''}>Chờ Xử Lý</option>
                    <option value="shipped" ${param.status == 'shipped' ? 'selected' : ''}>Đã Gửi</option>
                    <option value="completed" ${param.status == 'completed' ? 'selected' : ''}>Hoàn Thành</option>
                    <option value="canceled" ${param.status == 'canceled' ? 'selected' : ''}>Đã Hủy</option>
                </select>
            </div>


            <c:choose>
                <c:when test="${empty orders}">
                    <p style="text-align: center;">Không có đơn hàng nào</p>
                </c:when>
                <c:otherwise>
                    <table class="table-view">
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
                            <tr class="item-page">
                                <td>${o.id}</td>
                                <td>${o.shippingName}</td>
                                <td><fmt:formatDate value="${o.createdAt}" pattern="dd/MM/yyyy HH:mm" timeZone="Asia/Ho_Chi_Minh"/></td>
                                <td><fmt:formatNumber value="${o.totalPrice}" pattern="#,###"/> đ</td>

                                <td><span class="status-badge ${o.status}">${o.status}</span></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin-order-view?orderId=${o.id}" class="btn-action view">Xem</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>


<%--            <div class="footer">--%>
<%--                <nav>--%>
<%--                    <ul class="nav-change-page">--%>
<%--                        &lt;%&ndash; Nút Trang trước &ndash;%&gt;--%>
<%--                        <c:if test="${currentPage > 1}">--%>
<%--                            <li>--%>
<%--                                <a href="?status=${param.status}&page=${currentPage - 1}"><span>«</span> Trang trước</a>--%>
<%--                            </li>--%>
<%--                        </c:if>--%>

<%--                        &lt;%&ndash; Hiển thị danh sách số trang &ndash;%&gt;--%>
<%--                        <c:forEach begin="1" end="${totalPages}" var="i">--%>
<%--                            <li class="${currentPage == i ? 'active' : ''}">--%>
<%--                                <a href="?status=${param.status}&page=${i}">${i}</a>--%>
<%--                            </li>--%>
<%--                        </c:forEach>--%>

<%--                        &lt;%&ndash; Nút Trang tiếp theo &ndash;%&gt;--%>
<%--                        <c:if test="${currentPage < totalPages}">--%>
<%--                            <li>--%>
<%--                                <a href="?status=${param.status}&page=${currentPage + 1}">Trang tiếp theo <span>»</span></a>--%>
<%--                            </li>--%>
<%--                        </c:if>--%>
<%--                    </ul>--%>
<%--                </nav>--%>
<%--            </div>--%>

        </section>
        <div class ="pagination"></div>

    </main>
</div>
<script type="module">
    import { initPagination } from '${pageContext.request.contextPath}/js/pagination-admin.js';
    document.addEventListener("DOMContentLoaded", () => {
        initPagination();
    });
</script>

</body>

</html>