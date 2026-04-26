<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PaperCraft - Quản Lý Tài Khoản</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-customer-manage.css">


</head>
<body>

<div class="admin-container">

    <jsp:include page="../includes/admin-sidebar.jsp"/>

    <main class="admin-main-content">

        <header class="admin-header">
            <h1>Quản Lý Khách Hàng</h1>
        </header>

        <section class="customer-view">
            <form action="admin-account" method="get" class="block-search">

                <input type="text" name="search-customer" id="search-customer"
                       value="${keyword}"
                       placeholder="Tìm khách hàng theo tên, số điện thoại hoặc email">

                <select name="select-sort" id="select-sort">
                    <option value="all" ${statusFilter == 'all' ? 'selected' : ''}>Tất cả trạng thái</option>
                    <option value="active" ${statusFilter == 'active' ? 'selected' : ''}>Hoạt động</option>
                    <option value="blocked" ${statusFilter == 'blocked' ? 'selected' : ''}>Bị khóa</option>
                </select>

                <button type="submit" name="bt-search" id="bt-search">Tìm kiếm</button>
            </form>

            <div class="block-table">
                <table class="table-customer">
                    <thead>
                    <tr>
                        <td>Mã KH</td>
                        <td>Tên KH</td>
                        <td>SĐT</td>
                        <td>Email</td>
                        <td>Tổng chi tiêu</td>
                        <td>Quyền</td>
                        <td>Trạng thái</td>
                        <td>Thao tác</td>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="u" items="${userList}">
                        <tr class="item-page">
                            <td>${u.id}</td>

                            <td>${u.fullname}</td>

                            <td>${u.phoneNumber}</td>

                            <td>${u.email}</td>

                            <td>
                                <fmt:setLocale value="vi_VN"/>
                                <fmt:formatNumber value="${u.totalSpending}" type="currency" currencySymbol="đ"
                                                  maxFractionDigits="0"/>
                            </td>

                            <td>
                                <form action="admin-account" method="get" style="margin: 0;">
                                    <input type="hidden" name="action" value="set-role">
                                    <input type="hidden" name="id" value="${u.id}">

                                    <input type="hidden" name="page" value="${currentPage}">
                                    <input type="hidden" name="search-customer" value="${keyword}">
                                    <input type="hidden" name="select-sort" value="${statusFilter}">

                                    <select name="role" onchange="this.form.submit()"
                                            style=" border-radius: 4px; cursor: pointer; border: none;
                       ${u.role == 'admin' ? 'color: red' : ''}">

                                        <option value="user" ${u.role == 'user' ? 'selected' : ''}>Khách hàng</option>
                                        <option value="admin" ${u.role == 'admin' ? 'selected' : ''}>Admin</option>
                                    </select>
                                </form>
                            </td>

                            <td>
                                <c:choose>
                                    <c:when test="${u.status == true}">
                                        <span class="tag-status active" style="text-align: center">Hoạt động</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="tag-status blocked" style="text-align: center">Bị khóa</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td class="block-action" style="white-space: nowrap;">
                                <a href="admin-customer-details.jsp?id=${u.id}" id="link-view">Xem</a>
                                <a href="admin-customer-update.jsp?id=${u.id}">Sửa</a>

                                <c:choose>
                                    <c:when test="${u.status == true}">
                                        <a href="admin-account?action=lock&id=${u.id}"
                                           class="btn-action btn-lock"
                                           onclick="return confirm('Khóa tài khoản này?');">
                                            <i class="fa-solid fa-lock"></i> Khóa
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="admin-account?action=unlock&id=${u.id}"
                                           class="btn-action btn-unlock"
                                           onclick="return confirm('Mở khóa tài khoản này?');">
                                            <i class="fa-solid fa-unlock"></i> Mở
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty userList}">
                        <tr>
                            <td colspan="7" style="text-align: center; padding: 20px;">
                                Không tìm thấy khách hàng nào.
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
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