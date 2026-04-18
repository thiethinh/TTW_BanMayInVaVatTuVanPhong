<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PaperCraft - Admin Chi Tiết Đơn Hàng</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-product-edit.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-order-manage.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-order-view.css">
</head>

<body>

<div class="admin-container">
    <jsp:include page="../includes/admin-sidebar.jsp"/>

    <!-- ===========main========= -->
    <main class="admin-main-content">
        <section class="order-detail">

            <!-- -------------UP--------- -->
            <section class="up">
                <div class="back">
                    <a id="icon-back" href="javascript:history.back()"><i
                            class="fa-solid fa-arrow-left"></i></a>
                    <h1> Chi Tiết Đơn Hàng</h1>
                </div>
                <div>
                    <h2>Mã đơn: <span>${order.id}</span></h2>
                    <a href="${pageContext.request.contextPath}/invoice-print?orderId=${order.id}" target="_blank"
                       class="btn">
                        In hóa đơn
                    </a>
                </div>
            </section>

            <!-- -------CENTER----- -->
            <section class="center">

                <!-- INFO -->
                <div class="info">
                    <div class="info-cus">
                        <h2>Thông Tin Khách Hàng</h2>
                        <table>
                            <tbody>
                            <tr>
                                <td>Họ Tên KH:</td>
                                <td><span>${user.fullname}</span></td>
                            </tr>
                            <tr>
                                <td>SĐT:</td>
                                <td><span>${user.phoneNumber}</span></td>
                            </tr>
                            <tr>
                                <td>Email:</td>
                                <td><span>${user.email}</span></td>
                            </tr>
                            <tr>
                                <td>Địa chỉ giao hàng:</td>
                                <td><span>${order.shippingAddress}</span></td>
                            </tr>
                            </tbody>
                        </table>

                    </div>

                    <div class="list-product">
                        <table>
                            <thead>
                            <tr>
                                <td>Ảnh SP</td>
                                <td>Tên Sản Phẩm</td>
                                <td>Số Lượng</td>
                                <td>Đơn Giá</td>
                                <td>Thành Tiền</td>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${orderItems}" var="oi">
                                <tr>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/product-detail?productId=${oi.product.id}">
                                            <img class="product-table-image"
                                                 src="${pageContext.request.contextPath}/${oi.product.thumbnail}"
                                                 alt="${oi.product.productName}">
                                        </a>
                                    </td>
                                    <td>${oi.product.productName}</td>
                                    <td>${oi.quantity}</td>
                                    <td><fmt:formatNumber value="${oi.price}" pattern="#,###"/> VNĐ</td>
                                    <td><fmt:formatNumber value="${oi.price * oi.quantity}" pattern="#,###"/> VNĐ</td>
                                </tr>
                            </c:forEach>
                            </tbody>

                        </table>
                        <div>
                            <h4>Ghi chú đơn hàng: </h4>
                            <p>${order.note}</p>
                        </div>
                    </div>
                </div>

                <!-- BILL -->
                <div class="bill">
                    <div class="bill-details">
                        <!-- <div>
                            <label for="admin-status-order-viewDetails"> Trạng thái đơn: </label>
                            <select name="admin-status-order-viewDetails" id="admin-status-order-viewDetails">
                                <option value="all">Tất cả</option>
                                <option value="pending">Chờ Xử Lý</option>
                                <option value="shipped">Đã Gửi</option>
                                <option value="completed">Hoàn Thành</option>
                                <option value="canceled">Đã Hủy</option>
                            </select>
                        </div> -->
                        <div class="summary-bill">
                            <h4>Tóm tắt thanh toán: </h4>
                            <p> Tạm tính: <span><fmt:formatNumber value="${order.totalPrice  - order.shippingFee}"
                                                                  pattern="#,###"/> VNĐ</span></p>
                            <p> Phí vận chuyển: <span><fmt:formatNumber value="${order.shippingFee}" pattern="#,###"/> VNĐ</span>
                            </p>
                            <p> Thuế(VAT): <span>Đã bao gồm</span></p>
                            <h3>Tổng Cộng: <span><fmt:formatNumber value="${order.totalPrice}"
                                                                   pattern="#,###"/> VNĐ</span></h3>

                        </div>
                        <button class="btn update-status" type="submit" name="btn-update-status"> Cập nhật trạng
                            thái
                        </button>
                        <!-- <button class="btn contact" type="button" name="btn-contact"> Liên hệ khách hàng </button> -->
                        <div id="block-accept-cancel">
                            <button
                                    class="btn-accept"
                                    type="button"
                                    onclick="location.href='${pageContext.request.contextPath}/admin-order-view?accept=shipped&orderId=${order.id}'">
                                Xác nhận
                            </button>

                            <button
                                    class="btn-cancel"
                                    type="button"
                                    onclick="location.href='${pageContext.request.contextPath}/admin-order-view?cancel=canceled&orderId=${order.id}'">
                                Hủy đơn
                            </button>
                        </div>

                    </div>
                    <c:if test="${updated}">
                        <c:choose>
                            <c:when test="${isCancel == true}">
                                <p style="text-align:center;color:red">
                                    Đơn hàng đã bị hủy
                                </p>
                            </c:when>

                            <c:when test="${isAccept == true}">
                                <p style="text-align:center;color:green">
                                    Đơn hàng đã giao
                                </p>
                            </c:when>
                        </c:choose>
                    </c:if>
                    <div class="payment-type">
                        <h4>Thông tin thanh toán: </h4>
                        <p> Phương thức:
                            <span>${not empty payment.paymentMethod ? payment.paymentMethod : 'Chưa cập nhật'}</span>
                        </p>
                        <p> Ngày thanh toán:
                            <span>${not empty payment.paidAt ? payment.paidAt : 'Chưa thanh toán'} </span></p>
                    </div>
                </div>

            </section>

        </section>

    </main>
</div>

</body>

</html>