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

<body>
    <jsp:include page="../includes/header.jsp"/>

    <main class="page-section">
        <div class="container">
            <h1>Điều Khoản và Dịch Vụ</h1>
            <p class="last-updated">Cập nhật lần cuối: 15/11/2025</p>

            <p class="intro">
                Chào mừng bạn đến với PaperCraft! Bằng cách truy cập hoặc sử dụng website của chúng tôi và mua các sản
                phẩm (máy in, văn phòng phẩm...), bạn đồng ý tuân thủ và bị ràng buộc bởi các điều khoản và dịch vụ dưới
                đây. Vui lòng đọc kỹ trước khi sử dụng.
            </p>

            <h2>1. Chấp nhận Điều khoản</h2>
            <p>Các điều khoản này áp dụng cho tất cả người dùng, bao gồm khách truy cập, khách hàng và bất kỳ ai khác
                truy cập hoặc sử dụng dịch vụ. Nếu bạn không đồng ý với bất kỳ phần nào của điều khoản, bạn không được
                phép truy cập dịch vụ.</p>

            <h2>2. Tài khoản Người dùng</h2>
            <p>Khi bạn tạo tài khoản với chúng tôi, bạn phải cung cấp thông tin chính xác, đầy đủ và cập nhật. Bạn có
                trách nhiệm bảo mật mật khẩu của mình và chịu hoàn toàn trách nhiệm cho mọi hoạt động diễn ra dưới tên
                tài khoản của bạn.</p>

            <h2>3. Sản phẩm và Giá cả</h2>
            <p>Chúng tôi nỗ lực hiển thị thông tin sản phẩm (máy in, mực in, giấy...) một cách chính xác nhất. Tuy
                nhiên, chúng tôi không đảm bảo rằng mô tả sản phẩm hoặc nội dung khác là hoàn toàn không có lỗi.</p>
            <p>Giá cả có thể thay đổi mà không cần thông báo trước. Chúng tôi có quyền từ chối hoặc hủy bỏ bất kỳ đơn
                hàng nào nếu phát hiện có lỗi về giá hoặc thông tin sản phẩm.</p>

            <h2>4. Đặt hàng và Thanh toán</h2>
            <p>Việc bạn đặt hàng là một đề nghị mua sản phẩm. Chúng tôi có quyền chấp nhận hoặc từ chối đơn hàng này vì
                bất kỳ lý do gì. Đơn hàng chỉ được xem là chấp nhận khi chúng tôi gửi email xác nhận vận chuyển.</p>
            <p>Bạn đồng ý cung cấp thông tin thanh toán hợp lệ và đầy đủ cho tất cả các giao dịch.</p>

            <h2>5. Vận chuyển và Giao hàng</h2>
            <p>Chúng tôi sẽ xử lý và vận chuyển đơn hàng theo "Chính sách Vận chuyển" được công bố. Chúng tôi cung cấp
                <strong>miễn phí vận chuyển cho các đơn hàng có giá trị từ 500.000 VNĐ</strong>. Thời gian giao hàng dự
                kiến chỉ là ước tính và có thể thay đổi.</p>

            <h2>6. Bảo hành và Đổi trả</h2>
            <p>PaperCraft tự hào cung cấp chính sách bảo hành ưu việt, bao gồm chương trình <strong>Bảo Hành 3
                    Năm</strong> cho nhiều danh mục sản phẩm chủ lực. Các điều kiện, quy trình và chi tiết về bảo hành,
                đổi trả được quy định rõ ràng trong "Chính sách Bảo hành & Đổi trả" của chúng tôi.</p>

            <h2>7. Sở hữu trí tuệ</h2>
            <p>Toàn bộ nội dung trên website này, bao gồm văn bản, đồ họa, logo (PaperCraft), hình ảnh, và phần mềm, là
                tài sản của PaperCraft hoặc các nhà cung cấp của chúng tôi và được bảo vệ bởi luật bản quyền. Bạn không
                được phép sao chép, phân phối hoặc sử dụng bất kỳ nội dung nào mà không có sự cho phép bằng văn bản của
                chúng tôi.</p>

            <h2>8. Giới hạn Trách nhiệm</h2>
            <p>PaperCraft sẽ không chịu trách nhiệm pháp lý cho bất kỳ thiệt hại gián tiếp, ngẫu nhiên hoặc hậu quả nào
                phát sinh từ việc bạn sử dụng hoặc không thể sử dụng website, sản phẩm hoặc dịch vụ của chúng tôi.</p>

            <h2>9. Thay đổi Điều khoản</h2>
            <p>Chúng tôi có quyền sửa đổi hoặc thay thế các Điều khoản này bất kỳ lúc nào. Việc bạn tiếp tục sử dụng
                dịch vụ sau khi các thay đổi có hiệu lực đồng nghĩa với việc bạn chấp nhận các điều khoản mới.</p>

            <h2>10. Luật áp dụng</h2>
            <p>Các Điều khoản này sẽ được điều chỉnh và hiểu theo luật pháp của Việt Nam, mà không xét đến xung đột các
                quy định pháp luật.</p>

            <h2>11. Liên hệ</h2>
            <p>Nếu bạn có bất kỳ câu hỏi nào về các Điều khoản này, vui lòng liên hệ với chúng tôi qua email: <a
                    href="mailto:hotro@papercraft.vn">hotro@papercraft.vn</a> hoặc hotline hỗ trợ.</p>
        </div>
    </main>

    <jsp:include page="../includes/footer.jsp"/>
    <script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>
</body>

</html>