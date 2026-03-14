<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="main-header">
    <nav>
        <div class="header-container">

            <!-- LOGO -->
            <a href="${pageContext.request.contextPath}/home" class="logo">
                <img src="${pageContext.request.contextPath}/images/logo.webp"
                     height="80" width="80"/>
            </a>

            <!-- MENU -->
            <div class="nav-wrapper">
                <div class="menu-bar">
                    <a class="menu" id="nav-home" href="${pageContext.request.contextPath}/home">Trang Chủ</a>
                    <a class="menu" id="nav-printer" href="${pageContext.request.contextPath}/printer">Máy In</a>
                    <a class="menu" id="nav-stationery" href="${pageContext.request.contextPath}/stationery">Văn Phòng Phẩm</a>
                    <a class="menu" id="nav-blog" href="${pageContext.request.contextPath}/blog">Blog</a>
                    <a class="menu" id="nav-contact" href="${pageContext.request.contextPath}/contact">Liên Hệ</a>
                </div>
            </div>

            <!-- RIGHT SIDE -->
            <div class="header-right-side">
                <div class="user-action">

                    <%-- ================= CART ================= --%>

                    <c:set var="cartCount" value="0"/>
                    <c:if test="${not empty sessionScope.cart}">
                        <c:forEach var="q" items="${sessionScope.cart.values()}">
                            <c:set var="cartCount" value="${cartCount + q}"/>
                        </c:forEach>
                    </c:if>

                    <a href="${pageContext.request.contextPath}/cart"
                       id="nav-cart"
                       class="cart-btn">

                        <i class="fa-solid fa-cart-shopping"></i>

                        <span id="cartCount"
                              class="cart-count"
                              style="${cartCount == 0 ? 'display:none' : ''}">
                            ${cartCount}
                        </span>
                    </a>

                    <%-- =============== LOGIN / USER ================= --%>

                    <c:if test="${empty sessionScope.acc}">
                        <a class="login-btn"
                           href="${pageContext.request.contextPath}/login">
                            Đăng Nhập
                        </a>
                    </c:if>

                    <c:if test="${not empty sessionScope.acc}">
                        <div class="logged-in-user">
                            <a href="${pageContext.request.contextPath}/account"
                               class="user-name-link">
                                <i class="fa-solid fa-circle-user"></i>
                                <span>Chào, ${sessionScope.acc.lname}</span>
                            </a>

                            <span class="separator"></span>

                            <a href="${pageContext.request.contextPath}/logout"
                               class="logout-icon-btn"
                               title="Đăng xuất">
                                <i class="fa-solid fa-right-from-bracket"></i>
                            </a>
                        </div>
                    </c:if>

                </div>

                <!-- MOBILE MENU -->
                <div class="menu-toggle">
                    <i class="fa-solid fa-bars" id="menu-open-icon"></i>
                    <i class="fa-solid fa-times" id="menu-close-icon"></i>
                </div>
            </div>

        </div>
    </nav>
</header>
//debug cart
<input type="hidden" id="globalContextPath" value="${pageContext.request.contextPath}">
