
const contextPath = document.body.getAttribute('data-context') || "";


function addToCart(productId, quantity = 1) {
    // check đăng nhập
    if (!IS_LOGGED_IN) {
        Swal.fire({
            title: 'Bạn chưa đăng nhập!',
            text: "Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng !",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#165FF2',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Đăng nhập ngay',
            cancelButtonText: 'Để sau',
            reverseButtons: true
        }).then((result) => {
            if (result.isConfirmed) {
                const currentPath = window.location.pathname + window.location.search;
                window.location.href = CONTEXT_PATH + "/login?redirect=" + encodeURIComponent(currentPath);
            }
        });
        return;
    }

    // đã login-> gửi AJAX đến CartServlet
    fetch(CONTEXT_PATH + "/cart", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
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
            }else{
                //Bắt lỗi tồn kho
                swal.fire({
                    icon: 'warning',
                    title: 'Không thể thêm',
                    text: data.message,
                    confirmButtonColor: '#165FF2'
                });
            }
        })
    .catch(err => console.error("Lỗi addToCart: ",err));
}

// === Update ====
function updateQuantity(productId, change) {
    const input = document.getElementById(`qty-${productId}`);
    if (!input) return;

    let newQty = (change === 0) ? parseInt(input.value) : parseInt(input.value) +change;

    // Nếu giảm xuống dưới 1 -> Hỏi để xóa
    if (newQty < 1 ) {
        removeItem(productId);
        input.value = 1;
        return;
    }

    //  AJAX cập nhật số lượng
    fetch(`${contextPath}/cart`, {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: `action=update&id=${productId}&quantity=${newQty}`
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                location.reload();
            }else{
                // reset input và hiện cảnh báo
                input.value = parseInt(input.value) - change || 1 ;
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
// Validate input
// document.querySelectorAll(".input-qty").forEach(input => {
//     input.addEventListener("input", () => {
//         // chỉ nhận số
//         input.value = input.value.replace(/\D/g, "");
//         //bỏ trống
//         if(input.value.length = ""){
//             input.value =1;
//         }
//         // length = 2
//         if (input.value.length > 2) {
//             input.value = input.value.slice(0, 2);
//         }
//         // không nhập < 1
//         if (input.value < 1 || isNaN(input.value)) ;
//     });
// });

// === update sluong treen icon gio hang ====
function updateCartBadge(count){
    const badge= document.getElementById("cartCount");
    if (badge){
        badge.innerText=count;
        badge.style.display = count >0 ? "flex" : "none";
    }
}

// ==== updateCartCount ========
function updateCartCount() {
    fetch(`${contextPath}/cart?action=count`)
        .then(res => res.text())
        .then(count => updateCartBadge(parseInt(count)) || 0);
}

// ==== remove Item
function removeItem (productId){
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
        if (result.isConfirmed) {
            fetch(`${contextPath}/cart`,{
                method: "POST",
                headers: {"Content-Type": "application/x-www-form-urlencoded" },
                body: `action=remove&id=${productId}`
            })
                .then(() => location.reload());
        }
    });
}


document.addEventListener("DOMContentLoaded", updateCartCount);