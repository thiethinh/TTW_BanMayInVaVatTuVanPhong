let revenueChart;
let revenueChartMonth;
const idRevenueChart = 'revenueChart';
const idRevenueChartMonth = 'revenueChartMonth';


function loadRevenue() {
    const from = document.getElementById("fromDate").value;
    const to = document.getElementById("toDate").value;

    fetch(`revenue?from=${from}&to=${to}`)
        .then(res => res.json())
        .then(data => renderChart(data,idRevenueChart))
        .catch(err => console.error(err));
}


function loadDailyRevenue() {
    const month = document.getElementById("monthDetail").value;

    fetch(`revenue-by-month?month=${month}`)
        .then(res => res.json())
        .then(data => renderChart(data,idRevenueChartMonth))
        .catch(err => console.error(err));
}

function renderChart(data, idChart) {
    const labels = data.map(i => i.label);
    const values = data.map(i => i.total);

    const ctx = document.getElementById(idChart);

    let chartInstance;

    if (idChart === idRevenueChart) {
        if (revenueChart) revenueChart.destroy();
    } else {
        if (revenueChartMonth) revenueChartMonth.destroy();
    }

    chartInstance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Doanh thu',
                data: values,
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            plugins: {
                tooltip: {
                    callbacks: {
                        label: function(ctx) {
                            return ctx.raw.toLocaleString('vi-VN') + ' đ';
                        }
                    }
                }
            }
        }
    });

    if (idChart === idRevenueChart) {
        revenueChart = chartInstance;
    } else {
        revenueChartMonth = chartInstance;
    }
}

// auto load lần đầu
window.onload = () => {
    const now = new Date();
    const currentMonth = now.toISOString().slice(0, 7);

    document.getElementById("fromDate").value = "2026-01";
    document.getElementById("toDate").value = currentMonth;

    document.getElementById("monthDetail").value = currentMonth;


    loadRevenue();
    loadDailyRevenue()
};

// auto refresh mỗi 5 phút
setInterval(loadRevenue, 300000);
setInterval(loadDailyRevenue, 300000);