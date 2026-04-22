<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>

<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <title>Thanh Toán - PaperCraft</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/payment.css">
</head>

<body>
<jsp:include page="/WEB-INF/views/includes/header.jsp"/>

<section class="payment">
    <div class="title">
        <h1>Thanh Toán</h1>
    </div>

    <c:if test="${not empty error}">
        <div style="background-color: #f8d7da; color: #721c24; padding: 15px; margin: 0 auto 20px; max-width: 1200px; border: 1px solid #f5c6cb; border-radius: 5px; text-align: center;">
            <i class="fa-solid fa-triangle-exclamation"></i> ${error}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/checkout" method="POST" id="checkoutForm" novalidate>

        <div class="block-paymentDetails-finalBill">

            <section class="payment-details">
                <h2>Thông tin giao hàng</h2>

                <div class="block-nation">
                    <label for="nation">Quốc Gia <span>*</span></label>
                    <select name="nation" id="nation" required>
                        <option value="VN" ${addr.nation == 'VN' ? 'selected' : ''}>Việt Nam</option>
                        <option value="US" ${addr.nation == 'US' ? 'selected' : ''}>Hoa Kỳ</option>
                    </select>
                    <div class="invalid-feedback">Vui lòng chọn quốc gia của bạn!</div>
                </div>

                <div class="block-firstname-lastname">
                    <div class="block-firstname" style="width: 100%;">
                        <label for="fullname">Họ và Tên <span>*</span></label>
                        <input id="fullname" name="fullname" type="text"
                               value="${not empty addr ? addr.fname : sessionScope.acc.fname} ${not empty addr ? addr.lname : sessionScope.acc.lname}"
                               placeholder="Nguyễn Văn A" required>
                        <div class="invalid-feedback">Vui lòng nhập họ tên!</div>
                    </div>
                </div>

                <div class="block-address">
                    <label for="address">Địa chỉ nhận hàng <span>*</span></label>
                    <input id="address" name="address" type="text"
                           value="${addr.detailAddress}"
                           placeholder="Số nhà, tên đường..." required>
                    <div class="invalid-feedback">Vui lòng nhập địa chỉ giao hàng!</div>
                </div>

                <div class="block-addressCity-postCode">
                    <div class="block-addressCity">
                        <label for="addressCity">Tỉnh / Thành phố <span>*</span></label>
                        <input id="addressCity" name="city" type="text"
                               value="${addr.city}"
                               placeholder="TP. Hồ Chí Minh" required>
                        <div class="invalid-feedback">Vui lòng nhập tỉnh/thành phố!</div>
                    </div>

                    <div class="block-postCode">
                        <label for="postCode">Mã bưu chính</label>
                        <input id="postCode" name="postCode" type="text"
                               value="${addr.postcode}"
                               placeholder="70000">
                    </div>
                </div>

                <div class="block-phone-email">
                    <div class="block-phone">
                        <label for="phone">Số điện thoại <span>*</span></label>
                        <input id="phone" name="phone" type="tel"
                               value="${addr.phone}"
                               placeholder="0901234567" required>
                        <div class="invalid-feedback">Vui lòng nhập số điện thoại!</div>
                    </div>
                    <div class="block-email">
                        <label for="email">Email <span>*</span></label>
                        <input id="email" name="email" type="email"
                               value="${sessionScope.acc.email}"
                               placeholder="email@example.com" required>
                        <div class="invalid-feedback">Vui lòng nhập Email!</div>
                    </div>
                </div>

                <div class="block-note">
                    <label for="note">Ghi chú đơn hàng</label>
                    <textarea name="note" id="note" style="resize: none;"
                              placeholder="Ghi chú về thời gian giao hàng, địa điểm..."></textarea>
                </div>
            </section>

            <section class="final-bill">
                <h2>Đơn hàng của bạn</h2>
                <table class="view-table">
                    <thead>
                    <tr class="row-title">
                        <th id="block-header-final-bill">Sản Phẩm</th>
                        <th id="block-header-final-bill" style="text-align: right;">Thành tiền</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${items}" var="item">
                        <tr class="product-row">
                            <th>
                                    ${item.product.productName}
                                <strong style="color: #165FF2;">x${item.quantity}</strong>
                            </th>
                            <th style="text-align: right;">
                                <fmt:formatNumber value="${item.total}" pattern="#,###"/> ₫
                            </th>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot>
                    <tr>
                        <th class="shiping">Tạm tính:</th>
                        <th class="shiping" style="text-align: right;">
                            <fmt:formatNumber value="${subTotal}" pattern="#,###"/> ₫
                        </th>
                    </tr>
                    <tr>
                        <th class="shiping">Vận chuyển:</th>
                        <th class="shiping" style="text-align: right;">
                            <c:choose>
                                <c:when test="${shippingFee == 0}">
                                    <p style="color: green;">Miễn phí</p>
                                </c:when>
                                <c:otherwise>
                                    <fmt:formatNumber value="${shippingFee}" pattern="#,###"/> ₫
                                </c:otherwise>
                            </c:choose>
                        </th>
                    </tr>
                    <tr>
                        <th>Thuế (VAT 5%):</th>
                        <th class="VAT" style="text-align: right;">
                            <div class="vat">
                                <p><fmt:formatNumber value="${vat}" pattern="#,###"/> ₫</p>
                            </div>
                        </th>
                    </tr>


                    <tr id="total">
                        <th>Tổng Đơn Hàng:</th>
                        <th style="color: #d70018; font-size: 18px; text-align: right;">
                            <fmt:formatNumber value="${grandTotal}" pattern="#,###"/> ₫
                        </th>
                    </tr>
                    </tfoot>
                </table>

                <div class="pay-method">

                    <div class="method cod-method" style="margin-bottom: 10px;">
                        <input type="radio" name="paymentMethod" id="cod" value="COD" checked>
                        <label for="cod" style="font-weight: bold; margin-left: 5px;">Thanh toán khi nhận hàng
                            (COD)</label>
                    </div>

                    <div class="method bank-method">
                        <input type="radio" name="paymentMethod" id="bankCard" value="BANK">
                        <label for="bankCard" style="font-weight: bold; margin-left: 5px;">Chuyển khoản Ngân
                            hàng</label>

                        <button type="button" class="toggle-btn"><i class="fa-solid fa-plus"></i></button>

                        <div class="hidden-content" style="display: none; margin-top: 10px;">
                            <p>Vui lòng chuyển khoản đến STK sau:</p>
                            <p><b>Vietcombank - 0123456789 - PAPER CRAFT</b></p>
                            <p>Nội dung: <b>SDT_DAT_HANG</b></p>
                        </div>
                    </div>

                    <div class="method momo-method">
                        <input type="radio" name="paymentMethod" id="momo" value="Momo">
                        <label for="momo" style="font-weight: bold; margin-left: 5px;">Ví MoMo</label>

                        <button type="button" class="toggle-btn"><i class="fa-solid fa-plus"></i></button>
                        <div class="hidden-content" style="display: none; margin-top: 10px;">
                            <p>Quét mã QR để thanh toán:</p>
                            <img id="momo_qr" src="${pageContext.request.contextPath}/images/momo_qr.jpg" alt="momo_qr"
                                 style="width: 150px;">
                        </div>
                    </div>
                </div>

                <button class="order-btn" id=orderBtn type="submit">ĐẶT HÀNG</button>
            </section>
        </div>
    </form>
</section>

<jsp:include page="/WEB-INF/views/includes/footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/payment.js"></script>
<script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>
</body>
</html>