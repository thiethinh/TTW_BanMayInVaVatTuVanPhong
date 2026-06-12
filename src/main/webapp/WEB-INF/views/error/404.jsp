<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>404 - Không tìm thấy trang</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", sans-serif;
            background: radial-gradient(circle at top left, rgba(37, 99, 235, .15), transparent 40%),
            radial-gradient(circle at bottom right, rgba(59, 130, 246, .12), transparent 40%),
            #f8fafc;
            overflow: hidden;
        }

        .error-page {
            position: relative;
            text-align: center;
            max-width: 750px;
            width: 90%;
        }

        .bg-number {
            position: absolute;
            top: -100px;
            left: 50%;
            transform: translateX(-50%);
            font-size: 18rem;
            font-weight: 900;
            color: rgba(37, 99, 235, 0.06);
            user-select: none;
            pointer-events: none;
            z-index: 0;
        }

        .card {
            position: relative;
            z-index: 1;
            background: rgba(255, 255, 255, .8);
            backdrop-filter: blur(12px);
            padding: 60px;
            border-radius: 30px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, .06),
            0 20px 80px rgba(37, 99, 235, .08);
        }

        h1 {
            color: #111827;
            font-size: 2.5rem;
            margin-bottom: 15px;
        }

        p {
            color: #6b7280;
            font-size: 1.05rem;
            line-height: 1.8;
        }

        .actions {
            margin-top: 35px;
            display: flex;
            justify-content: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        .btn {
            padding: 14px 28px;
            border-radius: 999px;
            text-decoration: none;
            font-weight: 600;
            transition: .25s ease;
        }

        .btn-home {
            background: #2563eb;
            color: white;
        }

        .btn-home:hover {
            transform: translateY(-3px);
            box-shadow: 0 12px 24px rgba(37, 99, 235, .25);
        }

        .btn-back {
            background: white;
            color: #111827;
            border: 1px solid #e5e7eb;
        }

        .btn-back:hover {
            background: #f9fafb;
            transform: translateY(-3px);
        }

        .error-url {
            margin-top: 25px;
            color: #9ca3af;
            font-size: .9rem;
        }

        .error-url code {
            background: #f3f4f6;
            color: #374151;
            padding: 4px 10px;
            border-radius: 8px;
        }

        @media (max-width: 768px) {
            .card {
                padding: 40px 25px;
            }

            .bg-number {
                font-size: 10rem;
                top: -40px;
            }

            h1 {
                font-size: 2rem;
            }
        }
    </style>
</head>
<body>
<%-- Lấy thuộc tính URI lỗi từ Jakarta Servlet ở đầu trang để code gọn gàng hơn --%>
<%
    String requestUri = (String) request.getAttribute("jakarta.servlet.error.request_uri");
%>

<div class="error-page">
    <div class="bg-number">404</div>

    <div class="card">
        <h1>Oops! Không tìm thấy trang</h1>

        <p>
            Có vẻ như đường dẫn bạn truy cập không tồn tại,
            đã bị di chuyển hoặc đã được xóa khỏi hệ thống.
        </p>

        <div class="actions">
            <a href="${pageContext.request.contextPath}/home" class="btn btn-home">Về trang chủ</a>
            <a href="javascript:history.back()" class="btn btn-back">← Quay lại</a>
        </div>

        <% if (requestUri != null) { %>
        <div class="error-url">
            URL: <code><%= requestUri %>
        </code>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
