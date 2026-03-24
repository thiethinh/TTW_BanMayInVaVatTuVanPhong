<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${p.productName} | PaperCraft</title>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.css"/>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@latest/css/boxicons.min.css">

    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/product-details.css">
</head>

<body data-context="${pageContext.request.contextPath}">
<jsp:include page="../includes/header.jsp"/>

<main class="product-details-main">

    <section class="product-details">
        <nav class="block-select-img">
            <div class="img-select">
                <button class="bt-img-0 selected" type="button"
                        onclick="changeMainImage('${pageContext.request.contextPath}/${p.thumbnail}', this)">
                    <img src="${pageContext.request.contextPath}/${p.thumbnail}" alt="Main Thumb">
                </button>

                <c:forEach var="imgName" items="${listImages}" varStatus="loop" begin="1">
                    <button class="bt-img-0" type="button"
                            onclick="changeMainImage('${pageContext.request.contextPath}/${imgName}', this)">
                        <img src="${pageContext.request.contextPath}/${imgName}" alt="Gallery ${loop.index}">
                    </button>
                </c:forEach>
            </div>
        </nav>

        <div class="img-main">
            <img id="main-image-display" src="${pageContext.request.contextPath}/${p.thumbnail}" alt="${p.productName}">
        </div>

        <div class="info-product">
            <div class="block-rate-product">
                <h3 id="type-tag">${p.type}</h3>
                <div class="rate">
                    <c:forEach begin="1" end="5" var="i">
                        <c:choose>
                            <c:when test="${p.avgRating >= i}">
                                <i class="fa-solid fa-star"></i>
                            </c:when>
                            <c:when test="${p.avgRating >= i-0.5}">
                                <i class="fa-solid fa-star-half-stroke"></i>
                            </c:when>
                            <c:otherwise>
                                <i class="fa-regular fa-star"></i>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <span><a href="#review"
                             onclick="showTab('review', document.querySelectorAll('.tag-btn')[2])">(${countReview} đánh giá)</a></span>
                </div>
            </div>

            <h1 class="product-name">${p.productName}</h1>

            <div class="price">
                    <span class="original-price">
                        <fmt:formatNumber value="${p.originPrice}" type="number" groupingUsed="true"/>₫
                    </span>
                <span id="discount">
                        -<fmt:formatNumber value="${p.discount * 100}" maxFractionDigits="0"/>%
                    </span>
                <br/>
            </div>

            <p class="info-description">
                <c:forTokens items="${p.descriptionThumbnail}" delims="#" var="feature">
                    - ${feature.trim()} <br/>
                </c:forTokens>
            </p>

            <form action="add-to-cart" method="post">
                <input type="hidden" name="productId" value="${p.id}">

                <div class="block-quatity-cart">
                    <div class="quantity">
                        <button type="button" id="bt-down" class="qty-btn minus" onclick="updateQty(-1)">-</button>
                        <input type="text" name="quantity" class="qty-input" id="qty-input" value="1" min="1"
                               max="${p.stockQuantity > 0 ? p.stockQuantity : 1}"/>
                        <button type="button" id="bt-up" class="qty-btn plus" onclick="updateQty(1)">+</button>
                    </div>

                    <c:choose>
                        <c:when test="${p.stockQuantity > 0}">
                            <button type="submit" class="bt-add-cart">Thêm Vào Giỏ Hàng <i
                                    class="fa-solid fa-basket-shopping"></i></button>
                        </c:when>
                        <c:otherwise>
                            <button type="button" class="bt-add-cart" disabled
                                    style="opacity: 0.6; cursor: not-allowed; background-color: #888;">Hết Hàng
                            </button>
                        </c:otherwise>
                    </c:choose>
                </div>
            </form>

            <div class="product-share">
                <span>Chia sẻ:</span>
                <i id="fb" class="fa-brands fa-facebook-f"></i>
                <i id="twitter" class="fa-brands fa-twitter"></i>
                <i id="instagram" class="fa-brands fa-instagram"></i>
                <i id="pinterest" class="fa-brands fa-pinterest-p"></i>
            </div>
        </div>
    </section>

    <section class="block-description">
        <div class="tag-title">
            <button class="tag-btn active" onclick="showTab('tag-title-display-description',this)">
                <h2>Mô Tả Sản Phẩm</h2>
            </button>
            <button class="tag-btn " onclick="showTab('tag-title-display-info',this)">
                <h2>Thông Tin Chi Tiết</h2>
            </button>
            <button class="tag-btn" onclick="showTab('review',this)">
                <h2>Đánh Giá</h2>
            </button>
        </div>

        <div class="tag-title-display">
            <div id="tag-title-display-description" class="tag-display active">
                <h1>Mô tả sản phẩm</h1>
                <c:out value="${p.productDescription}" escapeXml="false"/>
            </div>

            <div id="tag-title-display-info" class="tag-display">
                <h1>Thông tin chi tiết</h1>
                <c:forTokens items="${p.productDetail}" delims="#" var="detail">
                    <li>${detail.trim()}</li>
                </c:forTokens>
            </div>

            <div id="review" class="tag-display">
                <h1>Đánh giá sản phẩm</h1>

                <div id="review-container">
                    <c:forEach items="${reviewList}" var="r">
                        <div class="block-User-feedback">
                            <div class="block-User">
                                <div class="user-avatar-placeholder">
                                        ${fn:substring(r.authorName, 0, 1)}
                                </div>
                                <div class="user">
                                    <h2 class="user-name">${r.authorName}</h2>
                                    <span><fmt:formatDate value="${r.createdAt}"
                                                          pattern="dd 'tháng' MM 'năm' yyyy, HH:mm"/></span>
                                </div>
                                <div class="user-rate">
                                    <c:forEach begin="1" end="5" var="i">
                                        <c:choose>
                                            <c:when test="${r.rating >= i}">
                                                <i class="fa-solid fa-star"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fa-regular fa-star"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                            </div>
                            <p class="user-write">${r.comment}</p>
                        </div>
                    </c:forEach>

                    <c:if test="${empty reviewList}">
                        <p>Chưa có đánh giá nào</p>
                    </c:if>
                </div>

                <div class="your-review">
                    <form action="add-review" method="post" id="review-form">
                        <input type="hidden" name="productId" value="${p.id}">
                        <input type="hidden" name="rating" value="0" id="rating-input">

                        <h2>HÃY VIẾT ĐÁNH GIÁ CỦA BẠN</h2>
                        <div class="your-rate">
                            <span>Đánh giá của bạn: </span>
                            <div class="star-selection">
                                <i class="fa-regular fa-star star-item" data-value="1"></i>
                                <i class="fa-regular fa-star star-item" data-value="2"></i>
                                <i class="fa-regular fa-star star-item" data-value="3"></i>
                                <i class="fa-regular fa-star star-item" data-value="4"></i>
                                <i class="fa-regular fa-star star-item" data-value="5"></i>
                            </div>
                        </div>
                        <textarea name="comment" id="your-write" required
                                  placeholder="Hãy nhập đánh giá của bạn..."></textarea>

                        <c:choose>
                            <c:when test="${not empty sessionScope.acc}">
                                <button type="submit" class="bt-submit-review">Gửi</button>
                            </c:when>
                            <c:otherwise>
                                <a href="javascript:void(0)" class="bt-submit-review"
                                   onclick="window.location.href='login.jsp?redirect='+encodeURIComponent(window.location.href)">Đăng
                                    nhập để đánh giá</a>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </div>
            </div>

        </div>

    </section>
</main>
<jsp:include page="../includes/footer.jsp"/>

<script>
    function showTab(tabId, btn) {
        document.querySelectorAll('.tag-display, .tag-btn').forEach(el => el.classList.remove('active'));
        document.getElementById(tabId).classList.add('active');
        if (btn) btn.classList.add('active');
    }

    function changeMainImage(src, btn) {
        document.getElementById('main-image-display').src = src;
        document.querySelectorAll('.img-select button').forEach(b => b.classList.remove('selected'));
        if (btn) btn.classList.add('selected');
    }

    function updateQty(val) {
        const input = document.getElementById('qty-input');
        const newVal = parseInt(input.value) + val;
        if (newVal >= 1 && newVal <= parseInt(input.getAttribute('max'))) input.value = newVal;
    }
</script>

<script src="https://cdn.jsdelivr.net/npm/swiper@12/swiper-bundle.min.js"></script>
<script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>