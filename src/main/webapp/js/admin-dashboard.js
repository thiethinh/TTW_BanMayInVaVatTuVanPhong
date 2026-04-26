
// di chuyển đến đúng phần hiển thị chi tiết khi nhấn vào mục trên dashboard

document.querySelectorAll(".stat-card").forEach(card =>{
    card.addEventListener('click', function (){
        const targetId = this.getAttribute("data-target")
        const target = document.getElementById(targetId);
        target.scrollIntoView({
            behavior: "smooth"
        })
    })
})


function initAccountFilter() {
    const monthFilter = document.getElementById("monthFilterAccount");
    if (!monthFilter) return;

    monthFilter.addEventListener("change", function () {
        const value = this.value;
        if (!value) return;

        const [year, month] = value.split("-");

        fetch(`admin-account?action=get-by-month&month=${month}&year=${year}`)
            .then(res => res.json())
            .then(data => renderTable(data))
            .catch(err => console.error(err));
    });

    // set tháng hiện tại
    const today = new Date();
    const m = String(today.getMonth() + 1).padStart(2, '0');
    const y = today.getFullYear();

    monthFilter.value = `${y}-${m}`;
    monthFilter.dispatchEvent(new Event("change"));
}

function renderTable(users) {
    const tbody = document.getElementById("accountTableBody");
    if (!tbody) return;

    tbody.innerHTML = "";

    if (!users || users.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="8" style="text-align:center">Không có dữ liệu</td>
            </tr>
        `;
        return;
    }

    users.forEach(u => {
        const statusHtml = u.status
            ? `<span class="tag-status active">Hoạt động</span>`
            : `<span class="tag-status blocked">Bị khóa</span>`;

        const row = `
            <tr>
                <td>${u.id}</td>
                <td>${u.fullname}</td>
                <td>${u.phoneNumber}</td>
                <td>${u.email}</td>
                <td>${formatCurrency(u.totalSpending)}</td>
                <td>${u.role}</td>
                <td>${statusHtml}</td>
                <td>
                    <a href="admin-customer-details.jsp?id=${u.id}">Xem</a>
                </td>
            </tr>
        `;

        tbody.innerHTML += row;
    });
}

function formatCurrency(number) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(number);
}
window.addEventListener("DOMContentLoaded", () => {
    initAccountFilter();
});