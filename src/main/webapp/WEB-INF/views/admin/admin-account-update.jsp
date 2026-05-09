<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PaperCraft - Admin Bảng Điều Khiển</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-customer-manage.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-account-update.css">
</head>

<body>

<div class="admin-container">

    <jsp:include page="../includes/admin-sidebar.jsp"/>

    <main class="admin-main-content">

        <header class="admin-customer-update-header">
            <a href="${pageContext.request.contextPath}/admin-account"><i class="fa-solid fa-arrow-left"></i>
                Quay lại</a>
            <h1>Sửa thông tin khách hàng</h1>
            <a href="admin-account-details?id=${acc.id}"><i class="fa-solid fa-info-circle"></i> Chi tiết</a>
        </header>

        <section class="customer-update-view">
            <h3>Thông tin cá nhân</h3>
            <form id="editForm">
                <div>
                    <label>Mã khách hàng</label>
                    <input type="text" value="${acc.id}" disabled>
                </div>

                <div>
                    <label>Email</label>
                    <input type="email" value="${acc.email}" disabled>
                </div>


                <div>
                    <label>Họ</label>
                    <input type="text" value="${acc.fname}">
                </div>
                <div>
                    <label>Tên</label>
                    <input type="text" value="${acc.lname}">
                </div>

                <div class="input-box">
                    <label>Mật khẩu</label>
                    <input type="password" id="password" value="${acc.passwordHash}">
                    <i class="bx bx-lock-alt"></i>
                    <i class="fas fa-eye-slash toggle-password" onclick="togglePassword('password', this)"></i>
                </div>


                <!-- <div>
                    <label>Ngày sinh</label>
                    <input type="date" value="1990-05-12">
                </div> -->

                <div>
                    <label>Số điện thoại</label>
                    <input type="text" value="${acc.phoneNumber}">
                </div>

                <div class="block-full-width">
                    <label>Địa chỉ</label>
                    <textarea>${address.detailAddress}</textarea>
                </div>

                <div>
                    <label>Tỉnh/Thành</label>
                    <select>
                        <option value=""></option>
                        <option value="hcm" ${address.city == 'hcm' ? 'selected' : ''}>TP. Hồ Chí Minh</option>
                        <option value="hn" ${address.city == 'hn' ? 'selected' : ''}>Hà Nội</option>
                        <option value="dn" ${address.city == 'dn' ? 'selected' : ''}>Đà Nẵng</option>
                    </select>
                </div>

                <div>
                    <label>Trạng thái tài khoản</label>
                    <select>
                        <option ${acc.status == true ? selected : ''}selected>Đang hoạt động</option>
                        <option ${acc.status == false ? selected : ''}>Bị khóa</option>
                    </select>
                </div>


                <!-- <div class="block-full-width">
                    <label>Ghi chú nội bộ</label>
                    <textarea>Khách hàng VIP, ưu tiên xử lý đơn hàng nhanh.</textarea>
                </div> -->

                <div class="buttons block-full-width">
                    <a href="${pageContext.request.contextPath}/admin-account">
                        <button type="button" class="bt cancel">Hủy</button>
                    </a>
                    <button type="submit" class="bt save">💾 Lưu thay đổi</button>
                </div>
            </form>
        </section>

    </main>
</div>

</body>
<script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>

</html>