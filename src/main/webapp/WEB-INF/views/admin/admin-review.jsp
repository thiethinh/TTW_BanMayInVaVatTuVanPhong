<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PaperCraft - Admin Bảng Điều Khiển</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-review.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
</head>

<body>

<div class="admin-container">
    <jsp:include page="../includes/admin-sidebar.jsp"/>

    <main class="admin-main-content">
        <div class="admin-header">
            <h1>Quản Lý Đánh Giá</h1>

            <form action="admin-review" method="get" class="user-review-searchbox">
                <input type="text" name="keyword" value="${currentKeyword}" placeholder="Nhập nội dung dùng để tìm..."
                       class="search-control">
                <button type="submit" id="bt-search">Tìm kiếm</button>
            </form>
        </div>

        <section class="product-review">
            <table>
                <thead>
                <tr>
                    <th>ID Review</th>
                    <th>Người Đánh Giá</th>
                    <th>Tên Sản Phẩm</th>
                    <th>Số Sao</th>
                    <th>Nội dung</th>
                    <th>Thời Điểm</th>
                    <th>Hành động</th>
                </tr>

                </thead>
                <tbody>

                <c:forEach items="${reviews}" var="r">
                    <tr class="item-page">
                        <td>${r.id}</td>
                        <td>${r.authorName}</td>
                        <td>${r.productName}</td>
                        <td class="stars">${r.rating}</td>
                        <td>${r.comment}</td>
                        <td>${r.createdAt}</td>
                        <td class="actions">
                            <a href="admin-review?action=delete&id=${r.id}&keyword=${currentKeyword}" class="delete"
                               onclick="return confirm('Bạn có chắc chắn muốn xóa review này không ?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty reviews}">
                    <tr>
                        <td colspan="7" style="text-align: center;">Không có kết quả phù hợp</td>
                    </tr>
                </c:if>

                </tbody>
            </table>
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