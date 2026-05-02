<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PaperCraft - Admin Quản Lý Sản Phẩm</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-products.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
</head>

<body>

<div class="admin-container">

    <jsp:include page="../includes/admin-sidebar.jsp"/>

    <main class="admin-main-content">

        <header class="admin-content-header">
            <h1>Quản Lý Sản Phẩm</h1>
            <a href="${pageContext.request.contextPath}/admin-product-add" class="btn btn-primary">
                <i class="fa-solid fa-plus"></i> Thêm Sản Phẩm Mới
            </a>
        </header>

        <c:if test="${param.msg == 'delete-success'}">
            <script>
                Swal.fire({
                    icon:'success',
                    title:'Đã xóa!',
                    toast: true,
                    position:'top-end',
                    showConfirmButton:false,
                    timer:2000
                });
            </script>
        </c:if>
        <c:if test="${param.msg == 'delete-fail'}">
            <script>
                Swal.fire({
                    icon:'error',
                    title:'Xóa thất bại!',
                    text: 'Có lỗi xảy ra, vui lòng thử lại.',
                    confirmButtonColor: '#165FF2'
                });
            </script>
        </c:if>


        <section class="content-table-card">
            <div class="action-bar" style="display: flex; justify-content: space-between; margin-bottom: 20px;">

                <form action="admin-product" method="get" style="display: flex; gap: 10px;">
                    <input type="text" name="keyword" value="${keyword}"
                           placeholder="Tìm theo tên sản phẩm..."
                           class="form-input" style="padding: 8px; width: 300px;border-radius: 6px">

                    <button type="submit" class="btn btn-primary">
                        <i class="fa-solid fa-magnifying-glass"></i> Tìm kiếm
                    </button>

                    <c:if test="${not empty keyword}">
                        <a href="admin-product" class="btn btn-secondary"
                           style="display: flex; align-items: center; justify-content: center; padding: 10px 12px; border-radius: 6px; text-decoration: none; border: 1px solid #ccc; background: white; color: #d9534f;"
                           title="Xóa bộ lọc">
                            <i class="fa-solid fa-trash-can"></i>
                        </a>
                    </c:if>
                </form>


            </div>

            <c:if test="${empty products}">
                <div style="text-align: center; padding: 20px; color: #666;">
                    Không tìm thấy sản phẩm nào phù hợp với từ khóa "<b>${keyword}</b>"
                </div>
            </c:if>

            <table class="content-table product-table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Ảnh</th>
                    <th>Tên Sản Phẩm</th>
                    <th>Giá</th>
                    <th>Số lượng</th>
                    <th>Danh Mục</th>
                    <th>Hành Động</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${products}" var="p">
                    <tr class="item-page">
                        <td>${p.id}</td>

                        <td>
                            <img class="product-table-image"
                                 src="${pageContext.request.contextPath}/${p.thumbnail}"
                                 alt="${p.productName}">
                        </td>

                        <td>${p.productName}</td>

                        <td>
                            <fmt:formatNumber value="${p.originPrice != null ? p.originPrice : 0}" type="number"/> đ
                        </td>

                        <td>${p.stockQuantity}</td>

                        <td>
                            <c:choose>
                                <c:when test="${p.type == 'Printer'}">Máy In</c:when>
                                <c:otherwise>Văn Phòng Phẩm</c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <a href="${pageContext.request.contextPath}/admin-product-edit?id=${p.id}"
                               class="btn-action edit">Sửa</a>


                            <button type="button"
                                    class="btn-action delete"
                                    onclick="confirmDelete(${p.id},'$p.productName')">
                                Xóa

                            </button>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>

            </table>
        </section>
        <div class="pagination"></div>

    </main>
</div>

<script type="module">
    import {initPagination} from '${pageContext.request.contextPath}/js/pagination-admin.js';

    document.addEventListener("DOMContentLoaded", () => {
        initPagination();
    });
</script>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script>
    function confirmDelete(id,name){
        Swal.fire({
            title:"Xác nhận xóa?",
            html: `Bạn có chắc muốn xóa sản phẩm <br><strong>"${name}"</strong>?<br><small style="color:#e74c3c">Hành động này không thể hoàn tác!</small>`,
            icon:'warning',
            showCancelButton:true,
            confirmButtonColor:'#e74c3c',
            cancelButtonColor:'#718096',
            confirmButtonText:'<i class="fa-solid fa-trash"></i> Xóa',
            cancelButtonText:'Hủy',
            reverseButtons:true
        }).then((result) =>{
            if (result.isConfirmed){
                window.location.href='${pageContext.request.contextPath}/admin-product?delete=${id}';
            }
        });
    }
</script>


</body>

</html>