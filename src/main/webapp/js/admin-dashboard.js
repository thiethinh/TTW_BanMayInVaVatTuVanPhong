
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