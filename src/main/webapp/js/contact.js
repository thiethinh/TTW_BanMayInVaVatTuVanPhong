document.addEventListener("DOMContentLoaded",()=>{
    const form= document.getElementById("contactForm");
    const formMessage= document.getElementById("formMessage")

    if (!form) return;

    const errorMap= {
        fullname: document.getElementById("error-fullname"),
        email: document.getElementById("error-email"),
        subject: document.getElementById("error-subject"),
        message: document.getElementById("error-message")
    };

    function clearError(){
        Object.values(errorMap).forEach((el) =>{
            if (el)
                el.textContent="";
        });
        form.querySelectorAll("input,textarea").forEach((el) => {
            el.classList.remove("input-error");
        });
        if (formMessage){
            formMessage.innerHTML="";
        }
    }
    function showFieldError(field,message){
        const errorE1= errorMap[field];
        const inputE1= form.querySelector('[name="${field}"]');

        if (errorE1)
            errorE1.textContent= message || "";
        if (inputE1)
            inputE1.classList.add("input-error");
    }

    form.addEventListener("submit", async (e) =>{
        e.preventDefault();
        clearError();

        const formData= new FormData(form);

        try{
            const res= await fetch(form.action,{
                method: "POST",
                body: formData,
                headers:{
                    "X-requested-with": "XMLHttpRequest"
                }
            });
            const data= await res.json();

            if (data.success){
                if (formMessage){
                    formMessage.innerHTML = `
                        <div class="ajax-success">
                            <i class="fa-solid fa-check-circle"></i> ${data.message}
                        </div>
                    `;
                }
                form.reset();
            }else {
                if (formMessage){
                    formMessage.innerHTML = `
                        <div class="ajax-error">
                            <i class="fa-solid fa-triangle-exclamation"></i> ${data.message}
                        </div>
                    `;
                }
                if (data.fieldErrors){
                    Object.entries(data.fieldErrors).forEach(([field,message]) => {
                        showFieldError(field,mesage);
                    });
                }
            }
        }catch (error){
            if (formMessage){
                formMessage.innerHTML = `
                    <div class="ajax-error">
                        <i class="fa-solid fa-triangle-exclamation"></i> Không thể gửi liên hệ lúc này. Vui lòng thử lại.
                    </div>
                `;
            }
        }
    })


})