<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vn">

<head>
    <meta charset="UTF-8">
    <title>PaperCraft - Trang Chủ</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp"/>

    <link rel="preload" href="${pageContext.request.contextPath}/images/introduce-img.webp" as="image"
          fetchpriority="high">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/policy.css">
</head>

<body>
<jsp:include page="../includes/header.jsp"/>

<main class="page-section">
    <div class="container">
        <h1>Chính Sách Đổi Trả & Bảo Hành</h1>
        <p class="last-updated">Cập nhật lần cuối: 15/11/2025</p>

        <p class="intro">
            Tại PaperCraft, sự hài lòng của bạn là ưu tiên hàng đầu. Chúng tôi cung cấp chính sách đổi trả và bảo
            hành rõ ràng, minh bạch để đảm bảo trải nghiệm mua sắm tốt nhất. Đặc biệt, chúng tôi tự hào với chương
            trình <strong>Bảo Hành 3 Năm</strong> cho nhiều sản phẩm máy in.
        </p>

        <h2>1. Quy Định Đổi Trả Hàng (Trong 7 ngày đầu)</h2>
        <p>Bạn có quyền yêu cầu đổi hoặc trả lại sản phẩm trong vòng 7 ngày kể từ ngày nhận hàng nếu sản phẩm gặp
            các lỗi sau:</p>
        <ul>
            <li>Sản phẩm bị lỗi kỹ thuật từ nhà sản xuất (ví dụ: máy in không lên nguồn, bản in bị lỗi hàng loạt
                không phải do cài đặt).
            </li>
            <li>Sản phẩm giao không đúng model, chủng loại, hoặc thông số kỹ thuật so với đơn đặt hàng.</li>
            <li>Sản phẩm bị hỏng hóc, trầy xước trong quá trình vận chuyển của PaperCraft.</li>
        </ul>

        <h2>2. Điều Kiện Đổi Trả</h2>
        <p>Để được chấp nhận đổi trả, sản phẩm của bạn cần đáp ứng:</p>
        <ul>
            <li>Sản phẩm phải còn nguyên vẹn, đầy đủ phụ kiện, sách hướng dẫn, thùng xốp và hộp (carton) đi kèm.
            </li>
            <li>Sản phẩm không có dấu hiệu đã qua sử dụng (đối với lỗi không phải từ nhà sản xuất) hoặc bị can
                thiệp, sửa chữa từ bên ngoài.
            </li>
            <li><strong>Lưu ý đặc biệt với mực in, vật tư:</strong> Các sản phẩm như hộp mực (toner), băng mực, giấy
                in... nếu đã bị bóc ra khỏi bao bì niêm phong (seal) sẽ không được hỗ trợ đổi trả, trừ trường hợp
                lỗi từ nhà sản xuất.
            </li>
        </ul>

        <h2>3. Quy Trình Bảo Hành (Chương Trình 3 Năm)</h2>
        <p>PaperCraft cung cấp chính sách <strong>Bảo Hành 3 Năm</strong> toàn diện cho các sản phẩm máy in được chỉ
            định.</p>
        <ul>
            <li><strong>Điều kiện bảo hành:</strong> Sản phẩm được bảo hành miễn phí nếu lỗi phát sinh là do linh
                kiện hoặc lỗi sản xuất.
            </li>
            <li><strong>Các trường hợp không được bảo hành:</strong>
                <ul>
                    <li>Sản phẩm hỏng hóc do thiên tai, hỏa hoạn, rơi vỡ, ẩm ướt.</li>
                    <li>Sử dụng không đúng điện áp quy định, hoặc sử dụng mực nạp, mực không chính hãng gây hỏng
                        hóc.
                    </li>
                    <li>Sản phẩm bị tự ý tháo dỡ, sửa chữa bởi các cá nhân không phải kỹ thuật viên của PaperCraft.
                    </li>
                    <li>Các bộ phận tiêu hao (như hộp mực, trống (drum), bao lụa...) sẽ được bảo hành theo quy định
                        riêng của nhà sản xuất, không nằm trong gói 3 năm.
                    </li>
                </ul>
            </li>
            <li><strong>Quy trình:</strong> Khi sản phẩm gặp sự cố, vui lòng liên hệ hotline. Chúng tôi cung cấp
                dịch vụ <strong>Hỗ Trợ 24/7</strong> để chẩn đoán sự cố từ xa hoặc sắp xếp kỹ thuật viên đến tận nơi
                (nếu áp dụng).
            </li>
        </ul>


        <h2>4. Quy Trình Xử Lý Đổi Trả / Bảo Hành</h2>
        <p><strong>Bước 1:</strong> Liên hệ bộ phận Hỗ Trợ 24/7 của PaperCraft qua email <a
                href="mailto:hotro@papercraft.vn">hotro@papercraft.vn</a> hoặc hotline. Vui lòng cung cấp mã đơn
            hàng và hình ảnh/video về sự cố.</p>
        <p><strong>Bước 2:</strong> Sau khi xác nhận, chúng tôi sẽ hướng dẫn bạn cách gửi trả sản phẩm về trung tâm
            bảo hành của PaperCraft.</p>
        <p><strong>Bước 3:</strong> Đối với hàng đổi trả, chúng tôi sẽ gửi sản phẩm mới cho bạn trong vòng 2-3 ngày
            làm việc sau khi nhận được hàng lỗi. Đối với bảo hành, thời gian xử lý sẽ phụ thuộc vào mức độ lỗi của
            sản phẩm.</p>

        <h2>5. Hoàn Tiền</h2>
        <p>Trong trường hợp trả hàng (do lỗi từ PaperCraft) mà không có sản phẩm tương tự để đổi, chúng tôi sẽ hoàn
            lại 100% giá trị đơn hàng cho bạn qua chuyển khoản ngân hàng trong vòng 5-7 ngày làm việc.</p>
    </div>
</main>

<jsp:include page="../includes/footer.jsp"/>
<script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>
</body>

</html>