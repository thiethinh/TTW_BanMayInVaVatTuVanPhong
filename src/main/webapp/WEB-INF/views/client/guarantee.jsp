<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="vn">

<head>
    <meta charset="UTF-8">
    <title>PaperCraft - Trang Chủ</title>
    <link rel="icon" href="${pageContext.request.contextPath}/images/logo.webp" />

    <link rel="preload" href="./images/introduce-img.webp" as="image" fetchpriority="high">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/policy.css">
</head>

<body>
    <jsp:include page="../includes/header.jsp"/>

    <main class="page-section">
        <div class="container">
            <h1>Chính Sách Bảo Hành (3 Năm)</h1>
            <p class="last-updated">Cập nhật lần cuối: 15/11/2025</p>

            <p class="intro">
                PaperCraft cam kết mang đến sự an tâm tuyệt đối cho khách hàng. Chúng tôi tự hào cung cấp chương trình
                <strong>Bảo Hành Vàng 3 Năm</strong> cho các sản phẩm máy in chính hãng và dịch vụ <strong>Hỗ Trợ Kỹ
                    Thuật 24/7</strong>, đảm bảo thiết bị của bạn luôn hoạt động ổn định.
            </p>

            <h2>1. Cam Kết "Bảo Hành 3 Năm"</h2>
            <p>Đây là chương trình bảo hành toàn diện, áp dụng cho các sản phẩm máy in được mua tại PaperCraft (có quy
                định cụ thể theo từng dòng sản phẩm).</p>

            <h3>A. Phạm vi bảo hành (Những gì được bảo hành)</h3>
            <ul>
                <li>Bảo hành miễn phí 100% cho bất kỳ lỗi nào liên quan đến phần cứng, linh kiện phát sinh từ phía nhà
                    sản xuất.</li>
                <li>Các lỗi kỹ thuật như: Máy không lên nguồn, không kết nối được với máy tính (đã loại trừ lỗi phần
                    mềm/driver), lỗi cơ học bên trong máy.</li>
                <li>Bo mạch chủ, bộ nguồn và các linh kiện điện tử quan trọng của máy.</li>
            </ul>

            <h3>B. Các trường hợp không được bảo hành (Miễn trừ)</h3>
            <p>Chính sách bảo hành 3 năm không áp dụng cho các trường hợp sau:</p>
            <ul>
                <li><strong>Lỗi do người sử dụng:</strong> Làm rơi vỡ, va đập mạnh, để nước hoặc hóa chất lọt vào máy.
                </li>
                <li><strong>Sử dụng vật tư không chính hãng:</strong> Sử dụng mực nạp, mực in không tương thích, hoặc
                    giấy in không đúng tiêu chuẩn gây hỏng hóc (ví dụ: hỏng đầu phun, hỏng bộ sấy).</li>
                <li><strong>Bộ phận tiêu hao:</strong> Bảo hành không áp dụng cho các vật tư tiêu hao như hộp mực
                    (toner/ink cartridge), trống (drum), bao lụa, trục cao su. Các linh kiện này sẽ được bảo hành theo
                    tiêu chuẩn riêng của nhà sản xuất (ví dụ: bảo hành "đến giọt mực cuối cùng" đối với lỗi mực).</li>
                <li><strong>Tự ý can thiệp:</strong> Sản phẩm bị hỏng do tự ý tháo dỡ, sửa chữa bởi các cá nhân hoặc đơn
                    vị không được PaperCraft ủy quyền.</li>
                <li><strong>Nguyên nhân khách quan:</strong> Hỏng hóc do thiên tai, hỏa hoạn, chập điện (do nguồn điện
                    không ổn định)...</li>
            </ul>

            <h2>2. Cam Kết "Hỗ Trợ 24/7"</h2>
            <p>Chúng tôi hiểu rằng công việc của bạn không thể gián đoạn. Đội ngũ kỹ thuật của PaperCraft luôn sẵn sàng
                hỗ trợ bạn bất cứ khi nào bạn cần.</p>
            <ul>
                <li><strong>Hỗ trợ qua điện thoại & Zalo:</strong> Chẩn đoán và xử lý nhanh các lỗi phần mềm (cài đặt
                    driver, thiết lập mạng LAN/Wifi cho máy in) từ xa.</li>
                <li><strong>Hỗ trợ kỹ thuật chuyên nghiệp:</strong> Đối với các lỗi phần cứng, kỹ thuật viên sẽ hướng
                    dẫn bạn các bước kiểm tra cơ bản. Nếu không thể khắc phục từ xa, chúng tôi sẽ sắp xếp lịch bảo hành.
                </li>
                <li><strong>Cam kết phản hồi:</strong> Mọi yêu cầu hỗ trợ sẽ được tiếp nhận và phản hồi trong thời gian
                    sớm nhất, kể cả ngoài giờ hành chính và ngày lễ.</li>
            </ul>

            <h2>3. Quy Trình Yêu Cầu Bảo Hành</h2>
            <p>Khi máy in hoặc thiết bị văn phòng phẩm gặp sự cố, vui lòng thực hiện theo các bước sau:</p>
            <ol>
                <li><strong>Bước 1:</strong> Liên hệ ngay lập tức với Bộ phận Hỗ Trợ 24/7 của PaperCraft qua Hotline
                    hoặc Email.</li>
                <li><strong>Bước 2:</strong> Cung cấp thông tin:
                    <ul>
                        <li>Tên sản phẩm, Model máy.</li>
                        <li>Mã số đơn hàng hoặc số điện thoại mua hàng.</li>
                        <li>Mô tả chi tiết sự cố, kèm theo hình ảnh hoặc video quay lại lỗi (nếu có thể).</li>
                    </ul>
                </li>
                <li><strong>Bước 3:</strong> Kỹ thuật viên sẽ hỗ trợ bạn xử lý từ xa.</li>
                <li><strong>Bước 4:</strong> Nếu không thể xử lý từ xa, chúng tôi sẽ hướng dẫn bạn gửi sản phẩm về trung
                    tâm bảo hành PaperCraft hoặc sắp xếp dịch vụ bảo hành tận nơi (tùy theo chính sách cụ thể của sản
                    phẩm).</li>
            </ol>

            <p>Mọi thắc mắc về chính sách, vui lòng liên hệ trực tiếp <a
                    href="mailto:hotro@papercraft.vn">hotro@papercraft.vn</a> để được giải đáp.</p>
        </div>
    </main>

    <jsp:include page="../includes/footer.jsp"/>
    <script type="module" src="${pageContext.request.contextPath}/js/main.js"></script>
</body>

</html>