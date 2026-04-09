<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>PaperCraft - Đăng Nhập</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp" />

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@latest/css/boxicons.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>

<body>
<jsp:include page="../includes/header.jsp"/>

<div class="form-wrapper">
    <img src="${pageContext.request.contextPath}/images/login-bg.webp" alt="" class="login-background-image" fetchpriority="high" width="1920"
         height="1080">
    <div class="form-box">
        <div class="login-container" id="login">
            <div class="top">
                <header>Quên Mật Khẩu</header>
            </div>

            <div class="two-forms">
                <form action="forgot-password" method="post" id="forgotForm">
                    <p id="status-msg" style="text-align: center;">
                        <c:if test="${not empty error}">
                            <span style="color: red;">${error}</span>
                        </c:if>
                        <c:if test="${not empty success}">
                            <span style="color: green;">${success}</span>
                        </c:if>
                    </p>

                    <div class="input-box">
                        <input type="email" class="input-field" name="email" id="emailInput" placeholder="Nhập Email đăng ký" required>
                        <i class="bx bx-envelope"></i> </div>

                    <div class="input-box">
                        <input type="text" class="input-field" name="otp" placeholder="Nhập mã OTP trong mail" required>
                        <i class="bx bx-lock"></i>
                    </div>

                    <div class="two-col">
                        <div class="one">
                            <label><a href="${pageContext.request.contextPath}/contact">Liên hệ giúp đỡ</a></label>
                        </div>
                        <div class="two">
                            <label><a href="#" id="btnSendOTP">Gửi mã OTP</a></label>
                            <span id="otpMessage" style="color: green; font-size: 12px; display: none;">Đang gửi...</span>
                        </div>
                    </div>

                    <div class="input-box">
                        <input type="submit" class="submit" value="Xác nhận">
                    </div>

                    <p style="color:red; text-align:center; margin-top:10px;">${message}</p>

                    <div class="bottom">
                        <span>Quay lại trang <a href="${pageContext.request.contextPath}/login">Đăng nhập</a></span>
                    </div>
                </form>
            </div>

            <script>
                document.getElementById('btnSendOTP').addEventListener('click', function(e) {
                    e.preventDefault();
                    var email = document.getElementById('emailInput').value;
                    var msgSpan = document.getElementById('otpMessage');

                    if(email === "") {
                        alert("Vui lòng nhập Email!");
                        return;
                    }

                    msgSpan.style.display = 'inline';
                    msgSpan.style.color = '#333';
                    msgSpan.innerText = "Đang gửi mail...";

                    // Gọi AJAX
                    fetch('${pageContext.request.contextPath}/forgot-password?action=sendOTP&email=' + email, {
                        method: 'GET'
                    })
                        .then(response => {
                            if (response.ok) {
                                return response.text();
                            } else {
                                throw new Error('Email chưa đăng ký!');
                            }
                        })
                        .then(data => {
                            msgSpan.style.color = 'green';
                            msgSpan.innerText = "Đã gửi mã vào email!";
                        })
                        .catch(error => {
                            msgSpan.style.color = 'red';
                            msgSpan.innerText = "Email không tồn tại!";
                        });
                });
            </script>
        </div>

    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
</body>
<script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>

</html>