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

                <div>
                    <label>Số điện thoại</label>
                    <input type="text" value="${acc.phoneNumber}">
                </div>

                <div>
                    <label>Trạng thái tài khoản</label>
                    <select>
                        <option ${acc.status == true ? selected : ''}selected>Đang hoạt động</option>
                        <option ${acc.status == false ? selected : ''}>Bị khóa</option>
                    </select>
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
                    <label>Quốc Gia</label>
                    <select>
                        <option value=""></option>
                        <option value="VN" ${address.nation == 'VN' ? 'selected' : ''}>Việt Nam</option>
                        <option value="US" ${address.nation == 'US' ? 'selected' : ''}>Hoa Kỳ</option>
                        <option value="UK" ${address.nation == 'UK' ? 'selected' : ''}>Vương quốc Anh</option>
                        <option value="JP" ${address.nation == 'JP' ? 'selected' : ''}>Nhật Bản</option>
                        <option value="KR" ${address.nation == 'KR' ? 'selected' : ''}>Hàn Quốc</option>
                        <option value="CN" ${address.nation == 'CN' ? 'selected' : ''}>Trung Quốc</option>
                        <option value="FR" ${address.nation == 'FR' ? 'selected' : ''}>Pháp</option>
                        <option value="DE" ${address.nation == 'DE' ? 'selected' : ''}>Đức</option>
                        <option value="RU" ${address.nation == 'RU' ? 'selected' : ''}>Nga</option>
                        <option value="IN" ${address.nation == 'IN' ? 'selected' : ''}>Ấn Độ</option>
                        <option value="CA" ${address.nation == 'CA' ? 'selected' : ''}>Canada</option>
                        <option value="AU" ${address.nation == 'AU' ? 'selected' : ''}>Úc</option>
                        <option value="BR" ${address.nation == 'BR' ? 'selected' : ''}>Brazil</option>
                        <option value="TH" ${address.nation == 'TH' ? 'selected' : ''}>Thái Lan</option>
                        <option value="MY" ${address.nation == 'MY' ? 'selected' : ''}>Malaysia</option>
                        <option value="SG" ${address.nation == 'SG' ? 'selected' : ''}>Singapore</option>
                        <option value="ID" ${address.nation == 'ID' ? 'selected' : ''}>Indonesia</option>
                        <option value="PH" ${address.nation == 'PH' ? 'selected' : ''}>Philippines</option>
                        <option value="IT" ${address.nation == 'IT' ? 'selected' : ''}>Ý</option>
                        <option value="ES" ${address.nation == 'ES' ? 'selected' : ''}>Tây Ban Nha</option>
                    </select>
                </div>

                <div class="buttons block-full-width">
                    <button type="submit" class="bt save">💾 Lưu thay đổi</button>
                </div>
            </form>
        </section>

    </main>
</div>

</body>

</html>