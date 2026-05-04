const contextPath = document.body.getAttribute('data-context') || "";


function addToCart(productId, quantity = 1) {

    //  gửi AJAX đến CartServlet
    fetch(contextPath + "/cart", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: `action=add&id=${productId}&quantity=${quantity}`
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                updateCartBadge(data.cartCount); //cập nhật Badge cart
                Swal.fire({
                    icon: 'success',
                    title: 'Đã thêm vào giỏ!',
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 1500
                });
            } else {
                //Bắt lỗi tồn kho
                Swal.fire({
                    icon: 'warning',
                    title: 'Không thể thêm',
                    text: data.message,
                    confirmButtonColor: '#165FF2'
                });
            }
        })
        .catch(err => console.error("Lỗi addToCart: ", err));
}

// === Update ====
function updateQuantity(productId, change) {
    const input = document.getElementById(`qty-${productId}`);
    if (!input) return;

    let newQty = (change === 0) ? parseInt(input.value) : parseInt(input.value) + change;

    // Nếu giảm xuống dưới 1 -> Hỏi để xóa
    if (newQty < 1) {
        removeItem(productId);
        input.value = 1;
        return;
    }


    //  AJAX cập nhật số lượng
    fetch(`${contextPath}/cart`, {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: `action=update&id=${productId}&quantity=${newQty}`
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                //update gias
                const itemTotalEl = document.getElementById(`item-total-${productId}`);
                if (itemTotalEl) itemTotalEl.textContent = formatVND(data.itemTotal);
                updateBillUI(data);
            } else {
                // reset input và hiện cảnh báo
                input.value = parseInt(input.value) - change || 1;
                Swal.fire({
                    icon: 'warning',
                    title: 'Vượt quá số lượng tồn kho',
                    text: data.message,
                    confirmButtonColor: '#165FF2'
                });
            }
        })
        .catch(err => console.error("Lỗi:", err));
}


// === update sluong treen icon gio hang ====
function updateCartBadge(count) {
    const badge = document.getElementById("cartCount");
    if (badge) {
        badge.innerText = count;
        badge.style.display = count > 0 ? "flex" : "none";
    }
}

// ==== updateCartCount ========
function updateCartCount() {
    fetch(`${contextPath}/cart?action=count`)
        .then(res => res.text())
        .then(count => updateCartBadge(parseInt(count)) || 0);
}

// ==== remove Item
function removeItem(productId) {
    Swal.fire({
        title: 'Xóa sản phẩm?',
        text: 'Bạn có chắc muốn xóa sản phẩm này không?',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#e53e3e',
        cancelButtonColor: '#718096',
        confirmButtonText: 'Xóa',
        cancelButtonText: 'Hủy',
        reverseButtons: true
    }).then((result) => {
        if (!result.isConfirmed)
            return;

        if (result.isConfirmed) {
            fetch(`${contextPath}/cart`, {
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded"},
                body: `action=remove&id=${productId}`
            })
                .then(res => res.json())
                .then(data => {
                    const row = document.getElementById(`row-${productId}`);
                    if (row) row.remove();

                    if (data.empty) {
                        location.reload();
                        return;
                    }

                    updateBillUI(data);

                })
                .catch(err => console.error("Lỗi remove: ", err));
        }
    });
}

function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN').format(Math.round(amount)) + ' đ';
}

function updateBillUI(data) {
    const fmt = formatVND;

    const subTotalEl = document.getElementById('bill-subTotal');
    const shippingEl = document.getElementById('bill-shippingFee');
    const vatEl = document.getElementById('bill-vat');
    const grandTotalEl = document.getElementById('bill-grandTotal');

    if (subTotalEl) subTotalEl.textContent = fmt(data.subTotal);
    if (vatEl) vatEl.textContent = fmt(data.vat);
    if (grandTotalEl) grandTotalEl.textContent = fmt(data.grandTotal);
    if (shippingEl) {
        shippingEl.innerHTML = data.shippingFee === 0 ? '<strong style="color:#165FF2 ">Miễn phí</strong>' : fmt(data.shippingFee);
    }
    updateCartBadge(data.cartCount)
}


document.addEventListener("DOMContentLoaded", updateCartCount);