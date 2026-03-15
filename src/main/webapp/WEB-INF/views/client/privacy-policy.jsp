<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vn">

<head>
    <meta charset="UTF-8">
    <title>PaperCraft - Trang Chủ</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp" />

    <link rel="preload" href="${pageContext.request.contextPath}/images/introduce-img.webp" as="image" fetchpriority="high">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/policy.css">
</head>

<body data-context="${pageContext.request.contextPath}">
    <jsp:include page="../includes/header.jsp"/>

    <main class="page-section">
        <div class="container">
            <h1>Chính Sách Bảo Mật</h1>
            <p class="last-updated">Cập nhật lần cuối: 15/11/2025</p>

            <p class="intro">
                Cảm ơn bạn đã tin tưởng và lựa chọn PaperCraft. Chúng tôi hiểu rằng thông tin cá nhân của bạn là quan
                trọng và cam kết bảo vệ quyền riêng tư của bạn. Chính sách này giải thích cách chúng tôi thu thập, sử
                dụng và bảo vệ thông tin của bạn khi bạn truy cập website và mua các sản phẩm máy in, văn phòng phẩm của
                chúng tôi.
            </p>

            <h2>1. Thông tin chúng tôi thu thập</h2>
            <p>Chúng tôi có thể thu thập các loại thông tin sau:</p>
            <ul>
                <li><strong>Thông tin cá nhân:</strong> Họ tên, địa chỉ email, số điện thoại, địa chỉ giao hàng khi bạn
                    đăng ký tài khoản hoặc đặt hàng.</li>
                <li><strong>Thông tin giao dịch:</strong> Chi tiết về các đơn hàng (máy in, mực in, giấy tờ...) bạn đã
                    mua, thông tin thanh toán (được xử lý an toàn qua cổng thanh toán của bên thứ ba).</li>
            </ul>

            <h2>2. Mục đích sử dụng thông tin</h2>
            <p>Thông tin của bạn được sử dụng cho các mục đích sau:</p>
            <ul>
                <li>Xử lý và giao đơn hàng.</li>
                <li>Cung cấp dịch vụ hỗ trợ khách hàng và bảo hành sản phẩm.</li>
                <li>Gửi thông báo về các chương trình khuyến mãi, sản phẩm mới (nếu bạn đồng ý nhận email marketing).
                </li>
                <li>Cải thiện chất lượng website và dịch vụ của chúng tôi.</li>
            </ul>

            <h2>3. Phạm vi chia sẻ thông tin</h2>
            <p>PaperCraft cam kết không bán hoặc chia sẻ thông tin cá nhân của bạn cho bên thứ ba không liên quan. Chúng
                tôi chỉ chia sẻ thông tin trong các trường hợp cần thiết sau:</p>
            <ul>
                <li><strong>Đối tác vận chuyển:</strong> Để giao hàng đến đúng địa chỉ của bạn.</li>
                <li><strong>Cơ quan pháp luật:</strong> Khi có yêu cầu hợp pháp từ cơ quan nhà nước.</li>
            </ul>

            <h2>4. Bảo mật thông tin</h2>
            <p>Chúng tôi áp dụng các biện pháp kỹ thuật và an ninh để bảo vệ dữ liệu của bạn khỏi bị truy cập, thay đổi
                hoặc phá hủy trái phép. Mọi thông tin thanh toán đều được mã hóa và xử lý qua các kênh an toàn.</p>

            <h2>5. Quyền của bạn</h2>
            <p>Bạn có quyền truy cập, chỉnh sửa hoặc yêu cầu xóa thông tin cá nhân của mình bất kỳ lúc nào bằng cách
                đăng nhập vào tài khoản hoặc liên hệ trực tiếp với chúng tôi qua email: <a
                    href="mailto:hotro@papercraft.vn">hotro@papercraft.vn</a>.</p>

            <h2>6. Thay đổi chính sách</h2>
            <p>Chúng tôi có thể cập nhật chính sách bảo mật này theo thời gian. Mọi thay đổi sẽ được đăng tải công khai
                trên website và có hiệu lực ngay lập tức. Chúng tôi khuyến khích bạn thường xuyên xem lại trang này để
                cập nhật thông tin.</p>
        </div>
    </main>

    <jsp:include page="../includes/footer.jsp"/>
    <script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>
</body>

</html>