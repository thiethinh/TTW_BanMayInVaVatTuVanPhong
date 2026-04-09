
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN"/>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>PaperCraft</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">

</head>

<body data-context="${pageContext.request.contextPath}">
<jsp:include page="../includes/header.jsp"/>

<!-- =================MAIN===================== -->

<!-- ============CART EMPTY================ -->
<c:if test="${empty items}">
    <main class="cart-empty-main">
        <div class="container">
            <i id="icon" class="fa-solid fa-shopping-basket"></i>
            <h1>Giỏ Hàng Trống</h1>
            <p>Thêm một sản phẩm để bắt đầu!</p>
            <a href="${pageContext.request.contextPath}/printer">
                <button type="button">MUA SẮM NGAY</button>
            </a>
        </div>
    </main>
</c:if>

<!-- ============CART FILL================= -->
<c:if test="${not empty items}">
    <main class="cart-fill-main">
        <div class="cart-banner">
            <marquee scrollamount="8">🎉 Đơn hàng từ 5.000.000đ được FREESHIP</marquee>
        </div>

        <div class="container">
            <h1>GIỎ HÀNG CỦA BẠN</h1>

            <section class="view">
                <div class="product-list">

                    <c:forEach items="${items}" var="item">
                        <div class="product-detail">
                            <a href="${pageContext.request.contextPath}/product-detail?productId=${item.id}">
                                <img src="${item.thumbnail}"/>
                            </a>

                            <div id="info">
                                <h2>${item.productName}</h2>

                                <div class="quantity-box-wrapper">
                                    <span class="label">Số lượng:</span>

                                    <div class="quantity-control">
                                        <button type="button" class="btn-qty"
                                                onclick="updateQuantity(${item.id}, -1)">
                                            <i class="fa-solid fa-minus"></i>
                                        </button>

                                        <input type="number"
                                               id="qty-${item.id}"
                                               value="${item.quantity}"
                                               min="1"
                                               maxlength="2"
                                               max="${item.stockQuantity}"
                                               class="input-qty"
                                               onchange="updateQuantity(${item.id}, 0)"
                                               oninput="
                                                       this.value = this.value.replace(/\D/g,'').slice(0,2);
                                                       this.value = Math.min(Math.max(1, this.value || 1), ${item.stockQuantity})"
                                               onblur="
                                                       if(this.value === '' || parseInt(this.value) < 1) this.value = 1;
                                                       if(parseInt(this.value) > ${item.stockQuantity}) this.value = ${item.stockQuantity};
                                                       "
                                        />

                                        <button type="button" class="btn-qty"
                                                onclick="updateQuantity(${item.id}, 1)">
                                            <i class="fa-solid fa-plus"></i>
                                        </button>
                                    </div>
                                </div>
                                <span class="stock-hint">
                                    Còn lại trong kho: <strong>${item.stockQuantity}</strong>
                                </span>

                                    <button id="bt-remove" onclick="removeItem(${item.id})">
                                        <i class="fa fa-trash-can"></i> Xoá
                                    </button>

                            </div>

                            <div class="item-cost">
                                <span class="label">Giá:</span>
                                <span class="price">
                                    <fmt:formatNumber value="${item.price * item.quantity}" type="number"
                                                      groupingUsed="true"/> ₫
                                </span>
                            </div>
                        </div>
                    </c:forEach>

                </div>

                <!-- BILL -->
                <div class="bill">
                    <h3>Tóm tắt đơn hàng</h3>

                    <p>Tạm tính:
                        <span>
                            <fmt:formatNumber value="${subTotal}" type="number" groupingUsed="true" maxFractionDigits="0"/> ₫
                        </span>
                    </p>

                    <p>Phí vận chuyển:
                        <span>
                            <c:choose>
                                <c:when test="${shippingFee == 0}">
                                    <strong style="color: #165FF2;">Miễn phí</strong>
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${shippingFee}" type="number" groupingUsed="true" maxFractionDigits="0"/> ₫
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </p>

                    <p>VAT (5%):
                        <span>
                            <fmt:formatNumber value="${vat}" type="number" groupingUsed="true" maxFractionDigits="0"/> ₫
                        </span>
                    </p>

                    <h2>Tổng cộng:
                        <p style="color: var(--danger-color);">
                            <strong><fmt:formatNumber value="${grandTotal}" type="number" groupingUsed="true" maxFractionDigits="0"/>
                                ₫</strong>
                        </p>
                    </h2>

                    <a href="${pageContext.request.contextPath}/checkout"
                       id="bt-payment"
                       class="block-bt-payment">
                        TIẾN HÀNH THANH TOÁN
                    </a>
                    <a href="#" onclick="history.back(); return false;"
                       id="bt-shopping-continous"
                       class="block-bt-shopping-continous">
                        Tiếp tục mua sắm
                    </a>
                </div>
            </section>
        </div>
    </main>
</c:if>

<script src="${pageContext.request.contextPath}/js/cart.js"></script>

<!-- ================= END MAIN===================== -->

<jsp:include page="../includes/footer.jsp"/>

<script src="https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>
</body>

</html>