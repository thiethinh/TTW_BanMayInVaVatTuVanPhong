<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt hàng thành công - PaperCraft</title>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/order-success.css">
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
</head>

<body data-context="${pageContext.request.contextPath}">

<jsp:include page="../includes/header.jsp"/>

<main class="order-success-main">

    <section class="success-card">
        <div class="success-icon">
            <i class="fa-solid fa-circle-check"></i>
        </div>

        <h1>Cảm ơn bạn đã đặt hàng!</h1>

        <p class="order-code">
            Mã đơn hàng của bạn:
            <strong>#${orderId}</strong>
        </p>
        <p id="p1">
            Đơn hàng của bạn đã được ghi nhận thành công. PaperCraft sẽ xử lý sớm nhất.
        </p>
        <p>
            Nếu có thắc mắc về đơn hàng vui lòng liên hệ với chúng tôi!

        </p>

        <div class="order-status">
            <div class="status-step active">
                <i class="fa-solid fa-check"></i>
                <span>Đặt hàng thành công</span>
            </div>

            <div class="status-step">
                <i class="fa-solid fa-clock"></i>
                <span>Chờ xác nhận</span>
            </div>

            <div class="status-step">
                <i class="fa-solid fa-truck"></i>
                <span>Đang giao hàng</span>
            </div>

            <div class="status-step">
                <i class="fa-solid fa-house"></i>
                <span>Hoàn tất</span>
            </div>
        </div>

        <div class="success-actions">
            <a href="${pageContext.request.contextPath}/printer" class="btn-primary">
                Tiếp tục mua sắm
            </a>

            <a href="${pageContext.request.contextPath}/order-view?orderId=${orderId}" class="btn-secondary">
                Xem lịch sử đơn hàng
            </a>
        </div>
    </section>

    <section class="suggestion-section">
        <h2>Có thể bạn sẽ cần thêm</h2>
        <p class="suggestion-subtitle">
            Một số sản phẩm được nhiều khách hàng lựa chọn sau khi đặt hàng.
        </p>

        <div class="suggestion-grid">
            <c:forEach items="${suggestedProducts}" var="p">
                <div class="suggestion-card">
                    <a href="${pageContext.request.contextPath}/product-detail?productId=${p.id}">
                        <img src="${pageContext.request.contextPath}/${p.thumbnail}" alt="${p.productName}">
                    </a>

                    <h3>${p.productName}</h3>

                    <p class="suggestion-price">
                        <fmt:formatNumber value="${p.price}" type="number" groupingUsed="true"/> ₫
                    </p>

                    <button type="button"
                            onclick="addToCart(${p.id}, 1)"
                            class="btn-add-suggestion">
                        <i class="fa-solid fa-cart-plus"></i>
                        Thêm vào giỏ
                    </button>
                </div>
            </c:forEach>
        </div>
    </section>

</main>

<jsp:include page="../includes/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>

</body>
</html>