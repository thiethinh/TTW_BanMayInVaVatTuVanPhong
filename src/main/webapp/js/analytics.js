export function initializeAnalytics(contextPath) {
    let myChart = null;

    function loadProfitChart() {
        const yearFilter = document.getElementById('yearFilter');
        if (!yearFilter) return;
        const selectedYear = yearFilter.value;
        const context = document.getElementById('profitChart').getContext('2d');

        fetch(`${contextPath}/admin/analytics-data?action=profit&year=${selectedYear}`)
            .then(response => response.json())
            .then(data => {
                    const labels = [];
                    const revenues = [];
                    const costs = [];
                    const profits = [];

                    for (let i = 1; i <= 12; i++) {
                        const monthLabel = (i < 10 ? '0' + i : i) + "/" + selectedYear;
                        labels.push('Tháng ' + i);

                        const match = data.find(item => item.month === monthLabel);
                        if (match) {
                            revenues.push(match.revenue);
                            costs.push(match.cost);
                            profits.push(match.profit);
                        } else {
                            revenues.push(0);
                            costs.push(0);
                            profits.push(0);
                        }
                    }

                    if (myChart) myChart.destroy();

                    myChart = new Chart(context, {
                        type: 'bar',
                        data: {
                            labels: labels,
                            datasets: [
                                {
                                    label: 'Doanh Thu Bán Ra',
                                    data: revenues,
                                    borderWidth: 1,
                                },
                                {
                                    label: 'Chi Phí Nhập Kho',
                                    data: costs,
                                    borderWidth: 1,
                                },
                                {
                                    label: 'Lợi Nhuận Gộp',
                                    type: 'line',
                                    fill: false,
                                    data: profits,
                                    borderWidth: 3,
                                }
                            ]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    ticks: {
                                        callback: function (value) {
                                            return new Intl.NumberFormat('vi-VN').format(value) + ' đ';
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            ).catch(error => console.error("Lỗi: " + error));
    }

    function loadRestockForecast() {
        const tableBody = document.getElementById('restockTableBody');
        if (!tableBody) return;

        fetch(`${contextPath}/admin/analytics-data?action=restock`, {})
            .then(response => response.json())
            .then(data => {
                let html = '';
                if (data && data.length > 0) {
                    data.forEach(item => {
                        let badgeClass = item.recommendedImportQty > 0 ? 'status-badge status-import' : 'status-badge status-safe';
                        let statusText = item.recommendedImportQty > 0 ? 'Cần Nhập Hàng' : 'An Toàn';

                        html += `
                            <tr>
                                <td>${item.productId}</td>
                                <td>${item.productName}</td>
                                <td>${item.currentStock}</td>
                                <td>${item.totalImported}</td>
                                <td>${item.totalSold}</td>
                                <td>${item.dailySalesVelocity}</td>
                                <td><strong>${item.recommendedImportQty}</strong></td>
                                <td><span class="${badgeClass}">${statusText}</span></td>
                            </tr>`;
                    });
                } else {
                    html = '<tr><td colspan="8">Không có dữ liệu</td></tr>';
                }
                tableBody.innerHTML = html;
            }).catch(error => console.error("Lỗi: " + error));
    }

    loadProfitChart();
    loadRestockForecast();
    const yearFilter = document.getElementById('yearFilter');
    if (yearFilter) {
        yearFilter.addEventListener('change', loadProfitChart);
    }
}