<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>PaperCraft - Đăng Nhập</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@latest/css/boxicons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>

<body data-active-tab="${activeTab}">
<jsp:include page="../includes/header.jsp"/>

<div class="form-wrapper">
    <img src="${pageContext.request.contextPath}/images/login-bg.webp" alt="" class="login-background-image" fetchpriority="high" width="1920"
         height="1080">
    <div class="form-box">
        <div class="login-container" id="login">
            <div class="top">
                <header>Đăng Nhập</header>
            </div>

            <form action="login" method="post" class="two-forms">
                <c:if test="${not empty msg}">
                    <p style="color: red; text-align: center; margin-bottom: 10px;">${msg}</p>
                </c:if>
            <%--  redirect--%>
                <input type="hidden" name="redirect" value="${not empty param.redirect ? param.redirect : redirect}">

                <div class="input-box">
                    <input type="email" name="email" class="input-field" placeholder="Email" required value="${email}">
                    <i class="bx bx-user"></i>
                </div>

                <div class="input-box">
                    <input type="password" name="password" class="input-field" placeholder="Mật khẩu" required>
                    <i class="bx bx-lock-alt"></i>
                </div>

                <div class="two-col">
                    <div class="one">
                        <input type="checkbox" name="remember" id="login-check">
                        <label for="login-check">Ghi nhớ mật khẩu</label>
                    </div>

                    <div class="two">
                        <label><a href="${pageContext.request.contextPath}/forgot-password">Quên mật
                            khẩu?</a></label>
                    </div>
                </div>

                <div class="input-box">
                    <input type="submit" class="submit" value="Đăng nhập">
                </div>

                <div class="bottom">
                    <span>Bạn chưa có tài khoản? <a href="#" id="register-trigger">Đăng ký</a></span>
                </div>
            </form>
        </div>

        <div class="register-container" id="register">
            <div class="top">
                <header>Đăng Ký</header>
            </div>

            <form action="register" method="post" class="two-forms">
                <c:if test="${not empty errorRegister}">
                    <div class="error-box">
                        <c:forEach items="${errorRegister}" var="err">
                            <p><i class="fa-solid fa-circle-exclamation"></i> ${err}</p>
                        </c:forEach>
                    </div>
                </c:if>

                <input type="hidden" name="redirect" value="${param.redirect}">

                <div class="two-row">
                    <div class="input-box">
                        <input type="text" name="firstname" class="input-field" value="${requestScope.valueFName}"
                               placeholder="Họ" required>
                        <i class="bx bx-user"></i>
                    </div>

                    <div class="input-box">
                        <input type="text" name="lastname" class="input-field" value="${requestScope.valueLName}"
                               placeholder="Tên" required>
                        <i class="bx bx-user"></i>
                    </div>
                </div>

                <div class="input-box">
                    <input type="text" name="email" class="input-field" value="${requestScope.valueEmail}"
                           placeholder="Email" required>
                    <i class="bx bx-envelope"></i>
                </div>

                <div class="input-box">
                    <input type="tel" name="phone" class="input-field" value="${requestScope.valuePhone}"
                           placeholder="Số điện thoại" required>
                    <i class="bx bx-phone"></i>
                </div>

                <div class="input-box select-box">
                    <select name="gender" class="input-field" id="gender-select" required>
                        <option value="" disabled ${empty requestScope.valueGender ? 'selected' : ''}>Giới tính</option>
                        <option value="male" ${requestScope.valueGender == 'male' ? 'selected' : ''}>Nam</option>
                        <option value="female" ${requestScope.valueGender == 'female' ? 'selected' : ''}>Nữ</option>
                        <option value="other" ${requestScope.valueGender == 'other' ? 'selected' : ''}>Khác</option>
                    </select>
                    <i class="bx bx-user-check"></i>
                </div>

                <div class="input-box">
                    <input type="password" name="password" class="input-field" placeholder="Mật khẩu" required>
                    <i class="bx bx-lock-alt"></i>
                </div>

                <div class="input-box">
                    <input type="password" name="confirmPassword" class="input-field" placeholder="Nhập lại mật khẩu"
                           required>
                    <i class="bx bx-lock-alt"></i>
                </div>

                <div class="two-col">
                    <div class="one">
                        <input type="checkbox" id="register-check" required>
                        <label for="register-check">Tôi đã đọc và đồng ý với</label>
                    </div>

                    <div class="two">
                        <label><a href="${pageContext.request.contextPath}/policies-and-services">Điều khoản & Dịch
                            vụ</a></label>
                    </div>
                </div>

                <div class="input-box">
                    <input type="submit" class="submit" value="Đăng ký">
                </div>

            </form>

            <div class="bottom">
                <span>Bạn đã có tài khoản? <a href="#" id="login-trigger">Đăng nhập</a></span>
            </div>
        </div>

    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>

<c:if test="${showVerifyModal}">
    <div class="otp-overlay">
        <div class="otp-box">
            <h3>Xác thực Email</h3>
            <p>Mã OTP đã gửi đến: <strong>${tempUser.email}</strong></p>

            <form action="verify-code" method="post">
                <div class="input-box">
                    <input type="text" name="otp" class="input-field" placeholder="Nhập mã 6 số" required maxlength="6">
                </div>

                <c:if test="${not empty errorVerify}">
                    <p class="error-msg">${errorVerify}</p>
                </c:if>

                <div class="input-box" style="margin-top: 20px;">
                    <input type="submit" class="submit" value="Xác nhận">
                </div>

                <div style="margin-top: 15px;">
                    <a href="${pageContext.request.contextPath}/login" style="text-decoration: none; color: #555;">Quay lại</a>
                </div>
            </form>
        </div>
    </div>
</c:if>
</body>
<script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>

</html>