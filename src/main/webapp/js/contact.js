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
    function showFieldError(field, message) {
        const errorEl = errorMap[field];
        const inputEl = form.querySelector(`[name="${field}"]`);

        if (errorEl) errorEl.textContent = message || "";
        if (inputEl && message) inputEl.classList.add("input-error");
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
                showFieldError("fullname", data.errorFullname);
                showFieldError("email",data.errorEmail);
                showFieldError("subject",data.errorSubject);
                showFieldError("message",data.errorMessage);
                }
            showFieldError("fullname", data.errorFullname);
            showFieldError("email", data.errorEmail);
            showFieldError("subject", data.errorSubject);
            showFieldError("message", data.errorMessage);
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