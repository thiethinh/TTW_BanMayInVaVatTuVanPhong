<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Papercraft - Admin Tạo Phiếu/Xuất Kho</title>

    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-inventory.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
</head>
<body>

<div class="admin-container">
    <jsp:include page="../includes/admin-sidebar.jsp"/>

    <main class="admin-main-content">
        <h1>Tạo Phiếu Nhập / Xuất Kho</h1>
        <a href="${pageContext.request.contextPath}/admin/inventory-history" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Quay lại
        </a>

        <div id="error-msg-container">
            <c:if test="${not empty sessionScope.error}">
                <div class="alert alert-danger" style="color: red; margin: 10px 0;">${sessionScope.error}</div>
                <c:remove var="error" scope="session"/>
            </c:if>
        </div>

        <form action="${pageContext.request.contextPath}/admin/create-inventory" method="POST" id="inventoryForm">
            <div class="form-section">
                <h4>Thông tin chung</h4>
                <div class="form-group">
                    <label>Loại Phiếu:</label>
                    <select name="transactionType" class="form-control" id="transactionType">
                        <option value="IMPORT">Nhập Kho (Tăng số lượng tồn)</option>
                        <option value="EXPORT">Xuất Kho (Giảm số lượng tồn)</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>Ghi chú:</label>
                    <textarea name="note" class="form-control" rows="3"
                              placeholder="Ví dụ: Nhập hàng đợt 1 tháng 5..."></textarea>
                </div>
            </div>

            <div class="form-section">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <h4>Chi tiết sản phẩm</h4>
                    <button type="button" class="btn btn-success" id="addRowBtn">
                        <i class="fa-solid fa-plus"></i> Thêm Sản Phẩm
                    </button>
                </div>

                <table class="table-products" id="productTable">
                    <thead>
                    <tr>
                        <th style="width: 35%;">Sản phẩm</th>
                        <th style="width: 15%;">Số lượng</th>
                        <th style="width: 20%;">Đơn giá (đ)</th>
                        <th style="width: 20%;">Thành tiền (đ)</th>
                        <th style="width: 10%;">Xóa</th>
                    </tr>
                    </thead>

                    <tbody id="productTableBody">
                    <tr class="product-row">
                        <td>
                            <select name="productId[]" class="form-control product-select" required>
                                <option value="">Chọn sản phẩm</option>
                                <c:if test="${not empty productList}">
                                    <c:forEach items="${productList}" var="p">
                                        <option value="${p.id}" data-price="${p.price}" data-stock="${p.stockQuantity}">
                                                ${p.productName} (Tồn: ${p.stockQuantity})
                                        </option>
                                    </c:forEach>
                                </c:if>
                            </select>
                        </td>
                        <td><input type="number" name="quantity[]" class="form-control qty-input" min="1" required></td>
                        <td><input type="number" name="price[]" class="form-control price-input" min="0" required></td>
                        <td class="text-center" style="font-weight: 600;"><span class="row-total">0</span></td>
                        <td class="text-center">
                            <button type="button" class="btn btn-danger remove-btn"><i class="fas fa-trash"></i></button>
                        </td>
                    </tr>
                    </tbody>
                </table>

                <div style="margin-top: 20px; text-align: right; font-size: 18px;">
                    <strong>Tổng Giá Trị Phiếu: </strong>
                    <span id="grandTotalDisplay"
                          style="color: red; font-size: 24px; font-weight: bold; margin-left: 10px;">0 đ</span>
                    <input type="hidden" name="totalValue" id="grandTotalInput" value="0">
                </div>
            </div>

            <div style="text-align: right;">
                <button type="submit" class="btn btn-primary" style="padding: 12px 30px; font-size: 16px;">
                    <i class="fas fa-save"></i> Lưu Phiếu Kho
                </button>
            </div>
        </form>
    </main>
</div>

<template id="rowTemplate">
    <tr class="product-row">
        <td>
            <select name="productId[]" class="form-control product-select" required>
                <option value="">Chọn sản phẩm</option>
                <c:if test="${not empty productList}">
                    <c:forEach items="${productList}" var="p">
                        <option value="${p.id}" data-price="${p.price}" data-stock="${p.stockQuantity}">
                                ${p.productName} (Tồn: ${p.stockQuantity})
                        </option>
                    </c:forEach>
                </c:if>
            </select>
        </td>
        <td><input type="number" name="quantity[]" class="form-control qty-input" min="1" required></td>
        <td><input type="number" name="price[]" class="form-control price-input" min="0" required></td>
        <td class="text-center" style="font-weight: 600;"><span class="row-total">0</span></td>
        <td class="text-center" style="text-align: center;">
            <button type="button" class="btn btn-danger remove-btn"><i class="fas fa-trash"></i></button>
        </td>
    </tr>
</template>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>