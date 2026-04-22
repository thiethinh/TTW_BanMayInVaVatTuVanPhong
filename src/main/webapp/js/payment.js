document.addEventListener("DOMContentLoaded", () => {
    //  Lấy tất cả các khối phương thức thanh toán
    const methods = document.querySelectorAll(".method");
    const checkoutForm = document.getElementById("checkoutForm");
    const orderBtn = document.getElementById("orderBtn");

    // Xử lý sự kiện CLICK cho từng khối
    methods.forEach((item) => {
        item.addEventListener("click", (e) => {

            //  Ngăn chặn sự kiện nếu bấm vào vùng nội dung con
            if (e.target.closest(".hidden")) return;

            //   Kiểm tra xem khối này CÓ ĐANG MỞ KHÔNG trước khi reset
            const wasActive = item.classList.contains("active");

            //  Đóng tất cả các khối (Reset)
            methods.forEach((m) => m.classList.remove("active"));

            //dùng Toggle
            if (!wasActive) {
                item.classList.add("active");

                // Tự động check vào nút Radio khi mở ra
                const radio = item.querySelector("input[type='radio']");
                if (radio) {
                    radio.checked = true;
                }
            }
        });
    });

    // Xử lý mặc định khi mới load trang
    const defaultChecked = document.querySelector(".method input[type='radio']:checked");
    if (defaultChecked) {
        const parentMethod = defaultChecked.closest(".method");
        if (parentMethod) {
            parentMethod.classList.add("active");
        }
    }

    if (checkoutForm && orderBtn) {
        let isSubmitting = false;
        const originText = orderBtn.textContent.trim();

        checkoutForm.addEventListener("submit", (e) => {
            //Invalid Field
            let isValid = true;
            const requiredInputs = checkoutForm.querySelectorAll("[required]");

            requiredInputs.forEach((input) => {
                input.classList.remove("is-invalid"); // Xóa lỗi cũ
                if (!input.value.trim()) {
                    isValid = false;
                    input.classList.add("is-invalid"); // Thêm class lỗi
                }
            });

            if (!isValid) {
                e.preventDefault(); // Ngăn submit
                const firstError = checkoutForm.querySelector(".is-invalid");
                if (firstError) firstError.focus();
                return; // Dừng lại không chạy code xử lý loading bên dưới
            }

            if (isSubmitting) {
                e.preventDefault();
                return;
            }
            isSubmitting = true;
            orderBtn.disabled = true;
            orderBtn.classList.add("is-loading");
            orderBtn.textContent = "ĐANG XỬ LÝ...";

            const allInputs = checkoutForm.querySelectorAll("input, textarea, select,button");
            allInputs.forEach((el) => {
                    if (el !== orderBtn) {
                        el.classList.add("is-disable-temp");
                    }
                }
            )
            ;

        });
        window.addEventListener("pageshow", () => {
            isSubmitting = false;
            orderBtn.disabled = false;
            orderBtn.classList.remove("is-loading");
            orderBtn.textContent = originText;

            const allInputs = checkoutForm.querySelectorAll(".is-disabled-temp");
            allInputs.forEach((el) => {
                el.classList.remove("is-disabled-temp");
            });
        });
    }
});