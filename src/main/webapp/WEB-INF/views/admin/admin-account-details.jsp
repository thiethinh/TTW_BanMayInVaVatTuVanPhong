<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PaperCraft - Admin Chi Tiết Tài Khoản</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-customer-manage.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin-account-details.css">
</head>

<body>

<jsp:include page="../includes/admin-sidebar.jsp"/>

<main class="admin-main-content">

    <header class="admin-customer-details-header">
        <a href="${pageContext.request.contextPath}/admin-account"><i class="fa-solid fa-arrow-left"></i>
            Quay lại</a>
        <h1>Chi tiết khách hàng</h1>

    </header>

    <section class="customer-details-view">
        <h3>Thông tin cá nhân</h3>
        <div class="info-section">
            <div class="info-left">
                <p><strong>Mã khách hàng:</strong> KH001</p>
                <p><strong>Email:</strong> nguyenvana@gmail.com</p>
                <p><strong>Ngày đăng ký:</strong> 1/5/2025</p>
            </div>
            <div class="info-right">
                <p><strong>Họ và tên:</strong> Nguyễn Văn A</p>
                <p><strong>Số điện thoại:</strong> 0905123456</p>
                <p><strong>Trạng thái:</strong> <span class="status-active">Đang hoạt động</span></p>
            </div>
        </div>

        <h3>Lịch sử mua hàng</h3>
        <table>
            <thead>
            <tr>
                <th>Mã đơn hàng</th>
                <th>Ngày mua</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
            </tr>
            </thead>
            <tbody>
            <tr>
                <td>HD1023</td>
                <td>01/11/2025</td>
                <td>4.500.000VNĐ</td>
                <td><span class="status status-success">Hoàn tất</span></td>
            </tr>
            <tr>
                <td>HD0987</td>
                <td>23/10/2025</td>
                <td>2.250.000VNĐ</td>
                <td><span class="status status-success">Hoàn tất</span></td>
            </tr>
            <tr>
                <td>HD0875</td>
                <td>15/10/2025</td>
                <td>3.100.000VNĐ</td>
                <td><span class="status status-cancel">Đã hủy</span></td>
            </tr>

            <tr>
                <td>HD1201</td>
                <td>10/09/2025</td>
                <td>12.500.000VNĐ</td>
                <td><span class="status status-success">Hoàn tất</span></td>
            </tr>
            <tr>
                <td>HD1202</td>
                <td>02/09/2025</td>
                <td>9.850.000VNĐ</td>
                <td><span class="status status-success">Hoàn tất</span></td>
            </tr>
            <tr>
                <td>HD1203</td>
                <td>22/08/2025</td>
                <td>7.300.000VNĐ</td>
                <td><span class="status status-cancel">Đã Hủy</span></td>
            </tr>
            <tr>
                <td>HD1204</td>
                <td>05/08/2025</td>
                <td>5.500.000VNĐ</td>
                <td><span class="status status-success">Hoàn tất</span></td>
            </tr>

            <tr>
                <td>HD1075</td>
                <td>20/6/10/2025</td>
                <td>3.100.000VNĐ</td>
                <td><span class="status status-success">Hoàn tất</span></td>
            </tr>

            <tr>
                <td>HD0903</td>
                <td>15/10/2024</td>
                <td>7.300.000VNĐ</td>
                <td><span class="status status-success">Hoàn tất</span></td>
            </tr>
            </tbody>
        </table>

    </section>

</main>

</body>

</html>