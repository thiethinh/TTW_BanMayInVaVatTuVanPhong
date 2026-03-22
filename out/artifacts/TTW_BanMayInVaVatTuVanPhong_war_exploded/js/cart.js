
const contextPath = document.body.getAttribute('data-context') || "";


function addToCart(productId, quantity = 1) {
    fetch(`${contextPath}/cart`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `action=add&id=${productId}&quantity=${quantity}`
    })
        .then(res => {
            if (res.ok) {
                updateCartCount(); // Cập nhật số sp trong giỏ hàng

                Swal.fire({ //Dùng SweetAlert2 để thông báo thành công
                    icon: 'success',
                    title: 'Đã thêm vào giỏ!',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1500
                });
            } else {
                alert("Không thể thêm sản phẩm.");
            }
        })
        .catch(err => console.error("Lỗi:", err));
}

// === Update ====
function updateQuantity(productId, change) {
    const input = document.getElementById(`qty-${productId}`);
    if (!input) return;

    let currentQty = parseInt(input.value);
    let newQty = (change === 0) ? currentQty : currentQty + change;

    // Nếu giảm xuống dưới 1 -> Hỏi để xóa
    if (newQty < 1) {
        if (confirm("Bạn muốn xóa sản phẩm này?")) {
            window.location.href = `${contextPath}/cart?action=remove&id=${productId}`;
        } else {
            input.value = 1;
        }
        return;
    }

    //  AJAX cập nhật số lượng
    fetch(`${contextPath}/cart`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `action=update&id=${productId}&quantity=${newQty}`
    })
        .then(res => {
            if (res.ok) {

                location.reload();
            }
        })
        .catch(err => console.error("Lỗi:", err));
}

// ==== updateCartCount ========
function updateCartCount() {
    fetch(`${contextPath}/cart?action=count`)
        .then(res => res.text())
        .then(count => {
            const badge = document.getElementById("cartCount");
            if (badge) {
                const num = parseInt(count) || 0;
                badge.innerText = num;
                badge.style.display = num > 0 ? "flex" : "none";
            }
        });
}

// Đồng bô giỏ hàng
document.addEventListener("DOMContentLoaded", updateCartCount);