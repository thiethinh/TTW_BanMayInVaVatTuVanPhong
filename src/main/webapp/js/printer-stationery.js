const brand = document.body.dataset.brand
const categoryId = document.body.dataset.categoryId
const sort = document.body.dataset.sort

function initDropdown() {
    const dropdowns = document.querySelectorAll(".custom-dropdown")
    dropdowns.forEach(dropdown => {
        const trigger = dropdown.querySelector(".select-trigger")
        const selectedValue = dropdown.querySelector(".selected-value")
        const menu = dropdown.querySelector(".option-value")
        const options = dropdown.querySelectorAll(".option-item")
        const arrow = dropdown.querySelector(".arrow")

        trigger.addEventListener("click", (e) => {
            e.stopPropagation();
            document.querySelectorAll(".option-value.open").forEach(openedMenu => {
                if (openedMenu !== menu) openedMenu.classList.remove("open")
            })

            document.querySelectorAll(".arrow.open").forEach(openedArrow => {
                if (openedArrow !== arrow) arrow.classList.remove("open")
            })

            const isOpen = menu.classList.toggle("open")
            arrow.classList.toggle("open", isOpen)
        });

        options.forEach(option => {
            option.addEventListener("click", () => {
                //cap nhat text da chon
                selectedValue.textContent = option.innerText

                //them class de hightlight
                options.forEach(option => option.classList.remove("selected"))
                option.classList.add("selected")

                //sau khi chon xong thi dong menu lai
                menu.classList.remove("open")
                arrow.classList.remove("open")

            })
        })

        menu.addEventListener("mouseleave", () => {
            menu.classList.remove("open")
            arrow.classList.remove("open")
        })
    })
}

function initPagination() {
    const paginationContainer = document.querySelector(".pagination")
    const productCards = [...document.querySelectorAll(".product-card")]
    if (!paginationContainer || productCards.length === 0) {
        if (paginationContainer) paginationContainer.style.display = 'none'
        return
    }
    const productsPerPage = 8
    let currentPage = 1
    const totalPage = Math.ceil(productCards.length / productsPerPage)
    console.log(productCards.length)
    console.log(totalPage)

    if (totalPage <= 1) {
        paginationContainer.style.display = 'none'
        productCards.forEach(product => {
            product.style.display = 'flex'
        })
        return
    } else {
        paginationContainer.style.display = 'flex'
    }

    function showPage(page) {
        const indexProductStart = (page - 1) * productsPerPage
        const indexProductEnd = indexProductStart + productsPerPage

        productCards.forEach((product, index) => {
            if (index >= indexProductStart && index < indexProductEnd) {
                product.style.display = 'flex'
            } else {
                product.style.display = 'none'
            }

        })
    }

    function createPagination(totalPage, current) {
        //reset inner code
        paginationContainer.innerHTML = '';

        const preLink = document.createElement('a')
        preLink.href = '#'
        preLink.innerHTML = "<i class='bx bx-chevron-left'></i>"
        preLink.classList.add("page-link", "prev")
        if (current === 1) {
            preLink.classList.add("disabled");
            preLink.style.pointerEvents = 'none';
        }
        preLink.dataset.page = current - 1;
        paginationContainer.appendChild(preLink)

        for (let i = 1; i <= totalPage; i++) {
            const pageLink = document.createElement('a')
            pageLink.href = "#"
            pageLink.innerText = i;
            pageLink.classList.add("page-link")
            if (i === current) pageLink.classList.add("active-page")
            pageLink.dataset.page = i
            paginationContainer.appendChild(pageLink)
        }

        const nextLink = document.createElement("a")
        nextLink.href = "#"
        nextLink.innerHTML = "<i class='bx bx-chevron-right'></i>";
        nextLink.classList.add("page-link", "next")
        if (current === totalPage) {
            nextLink.classList.add("disabled")
            nextLink.style.pointerEvents = 'none'
        }
        nextLink.dataset.page = current + 1
        paginationContainer.appendChild(nextLink)
    }

    paginationContainer.addEventListener("click", function (e) {
        //chan reload, js xử lí bên trong từ danh sách product
        e.preventDefault()
        const target = e.target.closest(".page-link")
        if (target && !target.classList.contains("disabled")) {
            const newPage = parseInt(target.dataset.page)
            if (newPage && newPage !== currentPage) {
                currentPage = newPage
                createPagination(totalPage, currentPage)
                showPage(currentPage)
            }

        }
    });
    createPagination(totalPage, currentPage)
    showPage(currentPage)
}

function assignFilterProduct() {
    document.querySelectorAll('.custom-dropdown').forEach(dropdown => {
        const hiddenInput = dropdown.previousElementSibling;
        const selectedValue = dropdown.querySelector('.selected-value');

        dropdown.querySelectorAll('.option-item').forEach(item => {
            item.addEventListener('click', () => {
                let value = item.dataset.value
                if (hiddenInput && hiddenInput.type === "hidden") {
                    hiddenInput.value = value
                }
                if (selectedValue) {
                    selectedValue.innerText = item.innerText;
                }
                console.log("value:", value);
                console.log("text:", item.innerText);

                dropdown.classList.remove("open");
            })
        })
    })
}

function reassignFilterProduct() {
    const brand = document.body.dataset.brand
    const categoryId = document.body.dataset.categoryId
    const sort = document.body.dataset.sort

    // CATEGORY
    if (categoryId || categoryId !== "0") {
        const selected = document.querySelector(`.option-item[data-value="${categoryId}"]`)

        if (selected) {
            document.getElementById("category-label").innerText = selected.innerText;
            document.getElementById("category-input").value = categoryId;
        }
    }


    //sort
    if (sort) {
        const selected = document.querySelector(`.option-item[data-value="${sort}"]`)
        if (selected) {
            document.getElementById("sort-label").innerText = selected.innerText;
            document.getElementById("sort-input").value = categoryId;
        }
    }

    //brand
    if (brand) {
        const selected = document.querySelector(`.option-item[data-value="${brand}"]`)
        if (selected) {
            document.getElementById("brand-label").innerText = selected.innerText;
            document.getElementById("brand-input").value = categoryId;
        }
    }
}

export function initializePrinterStationery() {
    initDropdown();
    initPagination();
    assignFilterProduct();
    reassignFilterProduct();
}