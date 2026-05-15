<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Papercraft - Admin Lịch sử Nhập/Xuất Kho</title>

    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-inventory-history.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
</head>

<body>
<div class="admin-container">
    <jsp:include page="../includes/admin-sidebar.jsp"/>

    <main class="admin-main-content">
        <header class="admin-header">
            <h1>Quản Lý Kho</h1>
            <a href="${pageContext.request.contextPath}/admin/create-inventory" class="btn btn-primary">
                <i class="fa-solid fa-plus"></i>Tạo Phiếu Mới
            </a>
        </header>

        <form action="${pageContext.request.contextPath}/admin/inventory-history" method="GET" class="search-type" id="filterForm">
            <div style="width: 250px;"> <label style="font-weight: 600; margin-bottom: 5px; display: block;">Lọc theo loại phiếu:</label>

                <div class="custom-dropdown">
                    <input type="hidden" name="type" id="transactionType" value="${selectedType}">

                    <div class="select-trigger" onclick="toggleDropdown()">
                <span class="selected-value" id="selectedValue">
                    <c:choose>
                        <c:when test="${selectedType == 'IMPORT'}">Nhập Kho</c:when>
                        <c:when test="${selectedType == 'EXPORT'}">Xuất Kho</c:when>
                        <c:otherwise>Tất cả giao dịch</c:otherwise>
                    </c:choose>
                </span>
                        <i class="arrow fas fa-chevron-down"></i>
                    </div>

                    <div class="option-value" id="dropdownOptions">
                        <div class="option-item ${selectedType == 'ALL' ? 'selected' : ''}" onclick="selectOption('all', 'Tất cả giao dịch')">Tất cả giao dịch</div>
                        <div class="option-item ${selectedType == 'IMPORT' ? 'selected' : ''}" onclick="selectOption('IMPORT', 'Nhập Kho')">Nhập Kho</div>
                        <div class="option-item ${selectedType == 'EXPORT' ? 'selected' : ''}" onclick="selectOption('EXPORT', 'Xuất Kho')">Xuất Kho</div>
                    </div>
                </div>
            </div>
        </form>

        <c:if test="${not empty sessionScope.success}">
            <p style="color: green; font-weight: bold; text-align: center">${sessionScope.success}</p>
            <c:remove var="success" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.error}">
            <p style="color: red; font-weight: bold; text-align: center">${sessionScope.error}</p>
            <c:remove var="error" scope="session"/>
        </c:if>

        <table class="content-table inventory-table">
            <thead>
            <tr>
                <th>Mã Phiếu</th>
                <th>Loại</th>
                <th>Người Tạo</th>
                <th>Ngày Tạo</th>
                <th>Tổng Tiền</th>
                <th>Ghi Chú</th>
            </tr>
            </thead>

            <tbody>
            <c:choose>
                <c:when test="${empty transactions}">
                    <tr>
                        <td colspan="6" class="text-center">Chưa có giao dịch nào.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${transactions}" var="t">
                        <tr>
                            <td><strong>#${t.id}</strong></td>

                            <td>
                                <c:choose>
                                    <c:when test="${t.transactionType == 'IMPORT'}">
                                        <span class="status-badge status-import">Nhập Kho</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-badge status-export">Xuất Kho</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <td>${t.adminName}</td>

                            <td>
                                <fmt:formatDate value="${t.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                            </td>

                            <td>
                                <fmt:formatNumber value="${t.totalValue}" type="currency" currencySymbol="đ"
                                                  maxFractionDigits="0"/>
                            </td>

                            <td>${t.note}</td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>

        <div class="pagination"></div>
    </main>
</div>

<script type="module">
    import {initPagination} from '${pageContext.request.contextPath}/js/pagination-admin.js';

    document.addEventListener("DOMContentLoaded", () => {
        initPagination();
    });
</script>

<script>
    function toggleDropdown() {
        document.getElementById("dropdownOptions").classList.toggle("open");
        document.querySelector(".arrow").classList.toggle("open");
    }

    function selectOption(value, text) {
        document.getElementById("transactionType").value = value;
        document.getElementById("selectedValue").innerText = text;
        document.getElementById("filterForm").submit();
    }

    window.onclick = function(event) {
        if (!event.target.matches('.select-trigger') && !event.target.closest('.select-trigger')) {
            var dropdowns = document.getElementsByClassName("option-value");
            for (var i = 0; i < dropdowns.length; i++) {
                var openDropdown = dropdowns[i];
                if (openDropdown.classList.contains('open')) {
                    openDropdown.classList.remove('open');
                    document.querySelector(".arrow").classList.remove('open');
                }
            }
        }
    }
</script>
</body>
</html>
